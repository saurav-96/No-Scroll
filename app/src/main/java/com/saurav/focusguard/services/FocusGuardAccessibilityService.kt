package com.saurav.focusguard.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import com.saurav.focusguard.data.db.AnalyticsDao
import com.saurav.focusguard.data.db.BlockEventEntity
import com.saurav.focusguard.data.db.ScrollSessionEntity
import com.saurav.focusguard.data.prefs.FocusPreferencesRepository
import com.saurav.focusguard.data.prefs.FocusPreferencesState
import com.saurav.focusguard.detection.PlatformDetectorRegistry
import com.saurav.focusguard.domain.BlockDecision
import com.saurav.focusguard.domain.BlockingDecisionEngine
import com.saurav.focusguard.domain.BlockingMode
import com.saurav.focusguard.domain.PinOption
import com.saurav.focusguard.domain.PlatformRestrictionMode
import com.saurav.focusguard.ui.pin.PinBlockOverlayActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class FocusGuardAccessibilityService : AccessibilityService() {

    @Inject lateinit var prefsRepository: FocusPreferencesRepository
    @Inject lateinit var decisionEngine: BlockingDecisionEngine
    @Inject lateinit var analyticsDao: AnalyticsDao

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var currentPrefs = FocusPreferencesState()
    private var todayScrollMillis: Long = 0L

    private var activeSessionPlatformId: String? = null
    private var activeSessionPackage: String? = null
    private var activeSessionStartedAtMillis: Long = 0L
    private var lastBlockExecutedAtMillis: Long = 0L
    private var unlockedPackageSession: String? = null

    private var overlayView: TextView? = null
    private var windowManager: WindowManager? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager

        serviceScope.launch {
            prefsRepository.stateFlow.collectLatest { state ->
                currentPrefs = state
                updateWatchtimeOverlayVisibility()
            }
        }

        serviceScope.launch {
            val todayKey = LocalDate.now().toString()
            analyticsDao.observeScrollTimeMillisForDate(todayKey).collectLatest { totalMillis ->
                todayScrollMillis = totalMillis
                updateWatchtimeOverlayText()
            }
        }

        serviceScope.launch {
            while (true) {
                delay(1_000L)
                if (activeSessionPlatformId != null) {
                    updateWatchtimeOverlayText()
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg == packageName || pkg.startsWith("com.android.systemui")) return

        val now = System.currentTimeMillis()
        val state = currentPrefs

        // 0. If user switched packages away from an unlocked app or active scroll session, close session
        if (unlockedPackageSession != null && unlockedPackageSession != pkg) {
            unlockedPackageSession = null
        }
        if (activeSessionPackage != null && activeSessionPackage != pkg) {
            finalizeActiveScrollSession(now)
        }

        // 1. Check whole-app blocklist & PIN-locked apps
        if (pkg in state.blockedApps && state.pausedUntilMillis <= now) {
            executeBlockingAction(
                platformId = "APP_BLOCK:$pkg",
                packageName = pkg,
                reason = "Blocked App",
                nowMillis = now,
                forceHome = true
            )
            return
        }

        if (pkg in state.pinLockedApps && state.pinOption != PinOption.NONE && unlockedPackageSession != pkg) {
            launchPinOverlay(targetPackage = pkg, reason = "PIN Locked App")
            return
        }

        // 2. Check supported browsers for blocked websites & web-based Shorts/Reels
        val root = rootInActiveWindow
        if (pkg in PlatformDetectorRegistry.supportedBrowsers) {
            val url = PlatformDetectorRegistry.extractBrowserUrl(root, pkg)?.lowercase(Locale.US)
            if (!url.isNullOrBlank()) {
                // Check custom blocked websites
                val matchedBlockedSite = state.blockedWebsites.firstOrNull { domain ->
                    domain.isNotBlank() && url.contains(domain)
                }
                if (matchedBlockedSite != null && state.pausedUntilMillis <= now) {
                    executeBlockingAction(
                        platformId = "WEB_BLOCK:$matchedBlockedSite",
                        packageName = pkg,
                        reason = "Blocked Website ($matchedBlockedSite)",
                        nowMillis = now
                    )
                    return
                }

                // Check browser short-video URLs
                val matchedShortUrl = PlatformDetectorRegistry.browserShortUrlPatterns.firstOrNull { (pattern, _) ->
                    url.contains(pattern)
                }
                if (matchedShortUrl != null && state.pausedUntilMillis <= now) {
                    executeBlockingAction(
                        platformId = matchedShortUrl.second,
                        packageName = pkg,
                        reason = "Browser Short Video (${matchedShortUrl.first})",
                        nowMillis = now
                    )
                    return
                }
            }
        }

        // 3. Inspect active window tree for short-video player view IDs / descriptions
        val match = PlatformDetectorRegistry.inspectWindowTree(
            rootNode = root,
            packageName = pkg,
            allowInstagramDmReels = state.allowInstagramDmReels
        )

        if (match == null) {
            if (activeSessionPackage == pkg) {
                finalizeActiveScrollSession(now)
            }
            return
        }

        // Start tracking scroll session if not already active
        if (activeSessionPlatformId != match.featureId) {
            finalizeActiveScrollSession(now)
            activeSessionPlatformId = match.featureId
            activeSessionPackage = pkg
            activeSessionStartedAtMillis = now
            updateWatchtimeOverlayVisibility()
        }

        val isVerticalScroll = event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED &&
            abs(event.scrollDeltaY) > abs(event.scrollDeltaX) &&
            abs(event.scrollDeltaY) > 20

        val effectiveRestriction = state.restrictionForFeature(
            featureId = match.featureId,
            defaultMode = match.defaultMode
        )

        val decision = decisionEngine.evaluate(
            platformId = match.featureId,
            restrictionMode = effectiveRestriction,
            activePlan = state.activePlan,
            pausedUntilMillis = state.pausedUntilMillis,
            nowMillis = now,
            reelSignature = match.reelSignature,
            isVerticalScrollEvent = isVerticalScroll,
            curiousAllowedMillis = state.curiousAllowedMinutes * 60_000L,
            curiousCalmDownMillis = state.curiousCalmDownMinutes * 60_000L
        )

        if (decision is BlockDecision.Block) {
            executeBlockingAction(
                platformId = match.featureId,
                packageName = pkg,
                reason = decision.reason,
                nowMillis = now
            )
        }
    }

    private fun executeBlockingAction(
        platformId: String,
        packageName: String,
        reason: String,
        nowMillis: Long,
        forceHome: Boolean = false
    ) {
        // Debounce rapid accessibility events (minimum 750ms between blocks)
        if (nowMillis - lastBlockExecutedAtMillis < 750L) return
        lastBlockExecutedAtMillis = nowMillis

        finalizeActiveScrollSession(nowMillis)

        if (currentPrefs.hapticFeedbackEnabled) {
            triggerHapticPulse()
        }

        val mode = if (forceHome) BlockingMode.CLOSE_APP else currentPrefs.blockingMode
        when (mode) {
            BlockingMode.CLOSE_PLAYER -> performGlobalAction(GLOBAL_ACTION_BACK)
            BlockingMode.CLOSE_APP -> performGlobalAction(GLOBAL_ACTION_HOME)
            BlockingMode.LOCK_SCREEN -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
                } else {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                }
            }
        }

        val todayKey = LocalDate.now().toString()
        serviceScope.launch(Dispatchers.IO) {
            analyticsDao.insertBlockEvent(
                BlockEventEntity(
                    platformId = platformId,
                    packageName = packageName,
                    dateKey = todayKey,
                    timestampMillis = nowMillis,
                    reason = reason
                )
            )
        }
    }

    private fun finalizeActiveScrollSession(nowMillis: Long) {
        val featureId = activeSessionPlatformId ?: return
        val pkg = activeSessionPackage ?: return
        val started = activeSessionStartedAtMillis
        activeSessionPlatformId = null
        activeSessionPackage = null
        activeSessionStartedAtMillis = 0L
        updateWatchtimeOverlayVisibility()

        val duration = (nowMillis - started).coerceAtLeast(0L)
        if (duration >= 1_500L) {
            val todayKey = LocalDate.now().toString()
            serviceScope.launch(Dispatchers.IO) {
                analyticsDao.insertSession(
                    ScrollSessionEntity(
                        platformId = featureId,
                        packageName = pkg,
                        dateKey = todayKey,
                        startedAtMillis = started,
                        endedAtMillis = nowMillis,
                        durationMillis = duration
                    )
                )
            }
        }
    }

    private fun launchPinOverlay(targetPackage: String, reason: String) {
        val intent = Intent(this, PinBlockOverlayActivity::class.java).apply {
            putExtra(PinBlockOverlayActivity.EXTRA_TARGET_PACKAGE, targetPackage)
            putExtra(PinBlockOverlayActivity.EXTRA_REASON, reason)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
    }

    private fun triggerHapticPulse() {
        runCatching {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            vibrator?.vibrate(VibrationEffect.createOneShot(90L, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun updateWatchtimeOverlayVisibility() {
        val wm = windowManager ?: return
        val shouldShow = currentPrefs.scrollWatchtimeEnabled && Settings.canDrawOverlays(this)
        if (!shouldShow) {
            overlayView?.let { view ->
                runCatching { wm.removeView(view) }
                overlayView = null
            }
            return
        }

        if (overlayView == null) {
            val pill = TextView(this).apply {
                setTextColor(Color.WHITE)
                textSize = 13f
                setPadding(32, 14, 32, 14)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 48f
                    setColor(Color.parseColor("#E60B5E46")) // Emerald teal matching reference UI
                }
            }
            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                x = 28
                y = 140
            }
            runCatching {
                wm.addView(pill, layoutParams)
                overlayView = pill
            }
        }
        updateWatchtimeOverlayText()
    }

    private fun updateWatchtimeOverlayText() {
        val pill = overlayView ?: return
        val currentSessionBonus = if (activeSessionStartedAtMillis > 0L) {
            (System.currentTimeMillis() - activeSessionStartedAtMillis).coerceAtLeast(0L)
        } else {
            0L
        }
        val totalSeconds = (todayScrollMillis + currentSessionBonus) / 1000L
        val mins = totalSeconds / 60L
        val secs = totalSeconds % 60L
        pill.text = String.format(Locale.US, "%02d:%02d", mins, secs)
    }

    override fun onInterrupt() {
        finalizeActiveScrollSession(System.currentTimeMillis())
    }

    override fun onDestroy() {
        finalizeActiveScrollSession(System.currentTimeMillis())
        overlayView?.let { view ->
            runCatching { windowManager?.removeView(view) }
            overlayView = null
        }
        serviceScope.cancel()
        super.onDestroy()
    }
}
