package com.saurav.focusguard.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.saurav.focusguard.domain.AppThemeMode
import com.saurav.focusguard.domain.BlockingMode
import com.saurav.focusguard.domain.FocusPlan
import com.saurav.focusguard.domain.PinDateFormat
import com.saurav.focusguard.domain.PinOption
import com.saurav.focusguard.domain.PinTimeFormat
import com.saurav.focusguard.domain.PlatformRestrictionMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "focusguard_prefs")

data class FocusPreferencesState(
    val activePlan: FocusPlan = FocusPlan.BLOCK_ALL,
    val blockingMode: BlockingMode = BlockingMode.CLOSE_PLAYER,
    val pausedUntilMillis: Long = 0L,
    val maxPauseDurationMinutes: Int = 15,
    val dailyLimitMinutes: Int = 150, // 2h 30m default matching screenshot
    val scrollWatchtimeEnabled: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true,
    val uninstallProtectionEnabled: Boolean = false,
    val skipMindfulWaitEnabled: Boolean = true,
    val isPremiumUnlocked: Boolean = true,
    val appThemeMode: AppThemeMode = AppThemeMode.SYSTEM_DEFAULT,
    val pinOption: PinOption = PinOption.NONE,
    val customPin: String = "0000",
    val pinTimeFormat: PinTimeFormat = PinTimeFormat.HH_MM_24,
    val pinDateFormat: PinDateFormat = PinDateFormat.DD_MM,
    val allowInstagramDmReels: Boolean = true,
    val curiousAllowedMinutes: Int = 5,
    val curiousCalmDownMinutes: Int = 15,
    val platformRestrictions: Map<String, PlatformRestrictionMode> = emptyMap(),
    val blockedApps: Set<String> = emptySet(),
    val pinLockedApps: Set<String> = emptySet(),
    val blockedWebsites: Set<String> = emptySet()
) {
    fun restrictionForFeature(featureId: String, defaultMode: PlatformRestrictionMode): PlatformRestrictionMode {
        return platformRestrictions[featureId] ?: defaultMode
    }
}

@Singleton
class FocusPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ACTIVE_PLAN = stringPreferencesKey("active_plan")
        val BLOCKING_MODE = stringPreferencesKey("blocking_mode")
        val PAUSED_UNTIL_MILLIS = longPreferencesKey("paused_until_millis")
        val MAX_PAUSE_MINUTES = intPreferencesKey("max_pause_minutes")
        val DAILY_LIMIT_MINUTES = intPreferencesKey("daily_limit_minutes")
        val SCROLL_WATCHTIME_ENABLED = booleanPreferencesKey("scroll_watchtime_enabled")
        val HAPTIC_FEEDBACK_ENABLED = booleanPreferencesKey("haptic_feedback_enabled")
        val UNINSTALL_PROTECTION_ENABLED = booleanPreferencesKey("uninstall_protection_enabled")
        val SKIP_MINDFUL_WAIT_ENABLED = booleanPreferencesKey("skip_mindful_wait_enabled")
        val IS_PREMIUM_UNLOCKED = booleanPreferencesKey("is_premium_unlocked")
        val APP_THEME_MODE = stringPreferencesKey("app_theme_mode")
        val PIN_OPTION = stringPreferencesKey("pin_option")
        val CUSTOM_PIN = stringPreferencesKey("custom_pin")
        val PIN_TIME_FORMAT = stringPreferencesKey("pin_time_format")
        val PIN_DATE_FORMAT = stringPreferencesKey("pin_date_format")
        val ALLOW_IG_DM_REELS = booleanPreferencesKey("allow_ig_dm_reels")
        val CURIOUS_ALLOWED_MINUTES = intPreferencesKey("curious_allowed_minutes")
        val CURIOUS_CALMDOWN_MINUTES = intPreferencesKey("curious_calmdown_minutes")
        val BLOCKED_APPS = stringSetPreferencesKey("blocked_apps")
        val PIN_LOCKED_APPS = stringSetPreferencesKey("pin_locked_apps")
        val BLOCKED_WEBSITES = stringSetPreferencesKey("blocked_websites")
    }

    val stateFlow: Flow<FocusPreferencesState> = context.dataStore.data.map { prefs ->
        val platformMap = mutableMapOf<String, PlatformRestrictionMode>()
        for ((key, value) in prefs.asMap()) {
            if (key.name.startsWith("feature_mode_") && value is String) {
                val featureId = key.name.removePrefix("feature_mode_")
                runCatching { PlatformRestrictionMode.valueOf(value) }.getOrNull()?.let {
                    platformMap[featureId] = it
                }
            }
        }
        FocusPreferencesState(
            activePlan = prefs[Keys.ACTIVE_PLAN]?.let { runCatching { FocusPlan.valueOf(it) }.getOrNull() }
                ?: FocusPlan.BLOCK_ALL,
            blockingMode = prefs[Keys.BLOCKING_MODE]?.let { runCatching { BlockingMode.valueOf(it) }.getOrNull() }
                ?: BlockingMode.CLOSE_PLAYER,
            pausedUntilMillis = prefs[Keys.PAUSED_UNTIL_MILLIS] ?: 0L,
            maxPauseDurationMinutes = prefs[Keys.MAX_PAUSE_MINUTES] ?: 15,
            dailyLimitMinutes = prefs[Keys.DAILY_LIMIT_MINUTES] ?: 150,
            scrollWatchtimeEnabled = prefs[Keys.SCROLL_WATCHTIME_ENABLED] ?: false,
            hapticFeedbackEnabled = prefs[Keys.HAPTIC_FEEDBACK_ENABLED] ?: true,
            uninstallProtectionEnabled = prefs[Keys.UNINSTALL_PROTECTION_ENABLED] ?: false,
            skipMindfulWaitEnabled = prefs[Keys.SKIP_MINDFUL_WAIT_ENABLED] ?: true,
            isPremiumUnlocked = prefs[Keys.IS_PREMIUM_UNLOCKED] ?: true,
            appThemeMode = prefs[Keys.APP_THEME_MODE]?.let { runCatching { AppThemeMode.valueOf(it) }.getOrNull() }
                ?: AppThemeMode.SYSTEM_DEFAULT,
            pinOption = prefs[Keys.PIN_OPTION]?.let { runCatching { PinOption.valueOf(it) }.getOrNull() }
                ?: PinOption.NONE,
            customPin = prefs[Keys.CUSTOM_PIN] ?: "0000",
            pinTimeFormat = prefs[Keys.PIN_TIME_FORMAT]?.let { runCatching { PinTimeFormat.valueOf(it) }.getOrNull() }
                ?: PinTimeFormat.HH_MM_24,
            pinDateFormat = prefs[Keys.PIN_DATE_FORMAT]?.let { runCatching { PinDateFormat.valueOf(it) }.getOrNull() }
                ?: PinDateFormat.DD_MM,
            allowInstagramDmReels = prefs[Keys.ALLOW_IG_DM_REELS] ?: true,
            curiousAllowedMinutes = prefs[Keys.CURIOUS_ALLOWED_MINUTES] ?: 5,
            curiousCalmDownMinutes = prefs[Keys.CURIOUS_CALMDOWN_MINUTES] ?: 15,
            platformRestrictions = platformMap,
            blockedApps = prefs[Keys.BLOCKED_APPS] ?: emptySet(),
            pinLockedApps = prefs[Keys.PIN_LOCKED_APPS] ?: emptySet(),
            blockedWebsites = prefs[Keys.BLOCKED_WEBSITES] ?: emptySet()
        )
    }

    suspend fun setActivePlan(plan: FocusPlan) {
        context.dataStore.edit { it[Keys.ACTIVE_PLAN] = plan.name }
    }

    suspend fun setBlockingMode(mode: BlockingMode) {
        context.dataStore.edit { it[Keys.BLOCKING_MODE] = mode.name }
    }

    suspend fun setPausedUntil(epochMillis: Long) {
        context.dataStore.edit { it[Keys.PAUSED_UNTIL_MILLIS] = epochMillis }
    }

    suspend fun setMaxPauseDurationMinutes(minutes: Int) {
        context.dataStore.edit { it[Keys.MAX_PAUSE_MINUTES] = minutes }
    }

    suspend fun setDailyLimitMinutes(minutes: Int) {
        context.dataStore.edit { it[Keys.DAILY_LIMIT_MINUTES] = minutes.coerceIn(15, 360) }
    }

    suspend fun setScrollWatchtimeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SCROLL_WATCHTIME_ENABLED] = enabled }
    }

    suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTIC_FEEDBACK_ENABLED] = enabled }
    }

    suspend fun setUninstallProtectionEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.UNINSTALL_PROTECTION_ENABLED] = enabled }
    }

    suspend fun setSkipMindfulWaitEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SKIP_MINDFUL_WAIT_ENABLED] = enabled }
    }

    suspend fun setAppThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { it[Keys.APP_THEME_MODE] = mode.name }
    }

    suspend fun setPinConfiguration(
        option: PinOption,
        customPin: String,
        timeFormat: PinTimeFormat,
        dateFormat: PinDateFormat
    ) {
        context.dataStore.edit {
            it[Keys.PIN_OPTION] = option.name
            it[Keys.CUSTOM_PIN] = customPin
            it[Keys.PIN_TIME_FORMAT] = timeFormat.name
            it[Keys.PIN_DATE_FORMAT] = dateFormat.name
        }
    }

    suspend fun setFeatureRestrictionMode(featureId: String, mode: PlatformRestrictionMode) {
        val key = stringPreferencesKey("feature_mode_$featureId")
        context.dataStore.edit { it[key] = mode.name }
    }

    suspend fun setAllowInstagramDmReels(allow: Boolean) {
        context.dataStore.edit { it[Keys.ALLOW_IG_DM_REELS] = allow }
    }

    suspend fun setCuriousConfig(allowedMinutes: Int, calmDownMinutes: Int) {
        context.dataStore.edit {
            it[Keys.CURIOUS_ALLOWED_MINUTES] = allowedMinutes
            it[Keys.CURIOUS_CALMDOWN_MINUTES] = calmDownMinutes
        }
    }

    suspend fun toggleBlockedApp(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.BLOCKED_APPS]?.toMutableSet() ?: mutableSetOf()
            if (!current.add(packageName)) current.remove(packageName)
            prefs[Keys.BLOCKED_APPS] = current
        }
    }

    suspend fun togglePinLockedApp(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.PIN_LOCKED_APPS]?.toMutableSet() ?: mutableSetOf()
            if (!current.add(packageName)) current.remove(packageName)
            prefs[Keys.PIN_LOCKED_APPS] = current
        }
    }

    suspend fun addBlockedWebsite(domainOrKeyword: String) {
        val cleaned = domainOrKeyword.trim().lowercase()
        if (cleaned.isBlank()) return
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.BLOCKED_WEBSITES]?.toMutableSet() ?: mutableSetOf()
            current.add(cleaned)
            prefs[Keys.BLOCKED_WEBSITES] = current
        }
    }

    suspend fun removeBlockedWebsite(domainOrKeyword: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.BLOCKED_WEBSITES]?.toMutableSet() ?: mutableSetOf()
            current.remove(domainOrKeyword)
            prefs[Keys.BLOCKED_WEBSITES] = current
        }
    }
}
