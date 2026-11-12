package com.saurav.focusguard.ui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saurav.focusguard.data.db.AnalyticsDao
import com.saurav.focusguard.data.db.BlockEventEntity
import com.saurav.focusguard.data.db.PlatformUsageAggregate
import com.saurav.focusguard.data.db.ScrollSessionEntity
import com.saurav.focusguard.data.prefs.FocusPreferencesRepository
import com.saurav.focusguard.data.prefs.FocusPreferencesState
import com.saurav.focusguard.detection.PlatformDetectorRegistry
import com.saurav.focusguard.domain.AppThemeMode
import com.saurav.focusguard.domain.BlockingDecisionEngine
import com.saurav.focusguard.domain.BlockingMode
import com.saurav.focusguard.domain.FocusPlan
import com.saurav.focusguard.domain.PinDateFormat
import com.saurav.focusguard.domain.PinOption
import com.saurav.focusguard.domain.PinTimeFormat
import com.saurav.focusguard.domain.PlatformRestrictionMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class InstalledAppItem(
    val packageName: String,
    val label: String
)

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val prefsRepository: FocusPreferencesRepository,
    private val analyticsDao: AnalyticsDao,
    private val decisionEngine: BlockingDecisionEngine
) : ViewModel() {

    private val todayKey: String = LocalDate.now().toString()

    val prefsState: StateFlow<FocusPreferencesState> = prefsRepository.stateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FocusPreferencesState())

    val todaySessionsCount: StateFlow<Int> = analyticsDao.observeSessionCountForDate(todayKey)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val todayBlockedCount: StateFlow<Int> = analyticsDao.observeBlockedCountForDate(todayKey)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val todayScrollTimeMillis: StateFlow<Long> = analyticsDao.observeScrollTimeMillisForDate(todayKey)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    val recentBlockEvents: StateFlow<List<BlockEventEntity>> = analyticsDao.observeRecentBlockEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentSessions: StateFlow<List<ScrollSessionEntity>> = analyticsDao.observeRecentSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val platformBreakdown: StateFlow<List<PlatformUsageAggregate>> =
        analyticsDao.observePlatformBreakdownForDate(todayKey)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _installedPackages = MutableStateFlow<Set<String>>(emptySet())
    val installedPackages: StateFlow<Set<String>> = _installedPackages.asStateFlow()

    private val _userApps = MutableStateFlow<List<InstalledAppItem>>(emptyList())
    val userApps: StateFlow<List<InstalledAppItem>> = _userApps.asStateFlow()

    private val _nowMillis = MutableStateFlow(System.currentTimeMillis())
    val nowMillis: StateFlow<Long> = _nowMillis.asStateFlow()

    init {
        refreshInstalledApps()
        viewModelScope.launch {
            while (true) {
                delay(1_000L)
                _nowMillis.value = System.currentTimeMillis()
            }
        }
    }

    fun refreshInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = appContext.packageManager
            val packages = runCatching {
                pm.getInstalledApplications(PackageManager.GET_META_DATA)
            }.getOrDefault(emptyList())

            val allPkgNames = packages.map { it.packageName }.toSet()
            // Also probe known doom-scrolling packages explicitly via getPackageInfo
            val knownSet = mutableSetOf<String>()
            knownSet.addAll(allPkgNames)
            PlatformDetectorRegistry.appGroups.flatMap { it.packageNames }.forEach { pkg ->
                runCatching {
                    pm.getPackageInfo(pkg, 0)
                    knownSet.add(pkg)
                }
            }
            PlatformDetectorRegistry.supportedBrowsers.keys.forEach { pkg ->
                runCatching {
                    pm.getPackageInfo(pkg, 0)
                    knownSet.add(pkg)
                }
            }
            _installedPackages.value = knownSet

            val launchableApps = packages
                .filter { appInfo ->
                    appInfo.packageName != appContext.packageName &&
                        ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 ||
                            pm.getLaunchIntentForPackage(appInfo.packageName) != null)
                }
                .map { appInfo ->
                    InstalledAppItem(
                        packageName = appInfo.packageName,
                        label = pm.getApplicationLabel(appInfo).toString()
                    )
                }
                .distinctBy { it.packageName }
                .sortedBy { it.label.lowercase() }

            _userApps.value = launchableApps
        }
    }

    fun selectPlan(plan: FocusPlan) {
        viewModelScope.launch {
            decisionEngine.resetAllStates()
            prefsRepository.setActivePlan(plan)
        }
    }

    fun pauseForMinutes(minutes: Int) {
        viewModelScope.launch {
            val until = if (minutes <= 0) 0L else System.currentTimeMillis() + minutes * 60_000L
            prefsRepository.setPausedUntil(until)
        }
    }

    fun resumeFromPause() {
        viewModelScope.launch {
            prefsRepository.setPausedUntil(0L)
        }
    }

    fun setScrollWatchtime(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepository.setScrollWatchtimeEnabled(enabled)
        }
    }

    fun setFeatureMode(featureId: String, mode: PlatformRestrictionMode) {
        viewModelScope.launch {
            decisionEngine.resetPlatformState(featureId)
            prefsRepository.setFeatureRestrictionMode(featureId, mode)
        }
    }

    fun setAllowInstagramDmReels(allow: Boolean) {
        viewModelScope.launch {
            prefsRepository.setAllowInstagramDmReels(allow)
        }
    }

    fun setDailyLimitMinutes(minutes: Int) {
        viewModelScope.launch {
            prefsRepository.setDailyLimitMinutes(minutes)
        }
    }

    fun setBlockingMode(mode: BlockingMode) {
        viewModelScope.launch {
            prefsRepository.setBlockingMode(mode)
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepository.setHapticFeedbackEnabled(enabled)
        }
    }

    fun setMaxPauseDurationMinutes(minutes: Int) {
        viewModelScope.launch {
            prefsRepository.setMaxPauseDurationMinutes(minutes)
        }
    }

    fun setUninstallProtection(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepository.setUninstallProtectionEnabled(enabled)
        }
    }

    fun setSkipMindfulWait(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepository.setSkipMindfulWaitEnabled(enabled)
        }
    }

    fun setAppThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            prefsRepository.setAppThemeMode(mode)
        }
    }

    fun savePinConfiguration(
        option: PinOption,
        customPin: String,
        timeFormat: PinTimeFormat,
        dateFormat: PinDateFormat
    ) {
        viewModelScope.launch {
            prefsRepository.setPinConfiguration(option, customPin, timeFormat, dateFormat)
        }
    }

    fun toggleBlockedApp(packageName: String) {
        viewModelScope.launch {
            prefsRepository.toggleBlockedApp(packageName)
        }
    }

    fun togglePinLockedApp(packageName: String) {
        viewModelScope.launch {
            prefsRepository.togglePinLockedApp(packageName)
        }
    }

    fun addBlockedWebsite(domain: String) {
        viewModelScope.launch {
            prefsRepository.addBlockedWebsite(domain)
        }
    }

    fun removeBlockedWebsite(domain: String) {
        viewModelScope.launch {
            prefsRepository.removeBlockedWebsite(domain)
        }
    }
}
