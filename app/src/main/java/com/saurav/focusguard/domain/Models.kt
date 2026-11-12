package com.saurav.focusguard.domain

enum class FocusPlan(
    val title: String,
    val subtitle: String,
    val bannerText: String
) {
    CURIOUS(
        title = "Curious",
        subtitle = "Allows mindful short bursts with mandatory cooldowns",
        bannerText = "Scroll mindfully in short sessions with breaks."
    ),
    BLOCK_ALL(
        title = "Block All",
        subtitle = "All Shorts & Reels are blocked",
        bannerText = "All Reels & Shorts are blocked."
    ),
    ONE_REEL(
        title = "One Reel",
        subtitle = "Watch the reel you tapped — blocks the next scroll",
        bannerText = "Allows 1 reel per visit, blocks on swipe."
    )
}

enum class BlockingMode(
    val displayName: String,
    val description: String
) {
    CLOSE_PLAYER(
        displayName = "Close Player",
        description = "Instantly exits the reel player"
    ),
    CLOSE_APP(
        displayName = "Close App",
        description = "Minimizes the app to the home screen"
    ),
    LOCK_SCREEN(
        displayName = "Lock Screen",
        description = "Locks the device screen immediately"
    )
}

/**
 * Exact timed restrictions matching NoScroll's PlatformRestrictionEnum.<clinit>:
 * AS_PER_PLAN (-1ms, 0ms cooldown)
 * ALWAYS (0ms, 0ms cooldown)
 * ONE_MIN (60,000ms, 15,000ms cooldown)
 * FIVE_MIN (300,000ms, 60,000ms cooldown)
 * TEN_MIN (600,000ms, 120,000ms cooldown)
 * NEVER (Long.MAX_VALUE, 0ms cooldown)
 */
enum class PlatformRestrictionMode(
    val label: String,
    val allowedMillis: Long,
    val calmDownMillis: Long
) {
    AS_PER_PLAN("Default: Active Plan", -1L, 0L),
    ALWAYS_BLOCK("Always Block", 0L, 0L),
    ALLOW_1_MIN("Allow: 01 Min", 60_000L, 15_000L),
    ALLOW_5_MIN("Allow: 05 Min", 300_000L, 60_000L),
    ALLOW_10_MIN("Allow: 10 Min", 600_000L, 120_000L),
    DONT_BLOCK("Don't Block", Long.MAX_VALUE, 0L)
}

enum class PinOption(val displayName: String, val description: String) {
    NONE("Disabled", "No PIN required"),
    CUSTOM("Custom PIN", "Fixed 4-digit PIN set by you"),
    TIME("Live Time PIN", "PIN changes dynamically to current device time"),
    DATE("Date PIN", "PIN changes dynamically to current device date"),
    DEVICE_DEFAULT("Device Default", "Uses standard PIN prompt")
}

enum class PinTimeFormat(val label: String) {
    HH_MM_24("HHMM (24-hour)"),
    HH_MM_12("hhmm (12-hour)"),
    MM_HH_24("MMHH (Reverse 24-hour)")
}

enum class PinDateFormat(val label: String) {
    DD_MM("DDMM (Day + Month)"),
    MM_DD("MMDD (Month + Day)")
}

enum class AppThemeMode(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM_DEFAULT("System Default")
}

data class SubFeatureDefinition(
    val id: String,
    val title: String,
    val defaultMode: PlatformRestrictionMode,
    val isToggleSwitch: Boolean = false,
    val defaultToggleValue: Boolean = false,
    val requiresPremiumBadge: Boolean = false
)

data class AppGroupDefinition(
    val groupId: String,
    val displayName: String,
    val packageNames: List<String>,
    val brandColorHex: Long,
    val badgeText: String,
    val subFeatures: List<SubFeatureDefinition>
)

data class DailyLimitEmojiBand(
    val id: String,
    val minMinutes: Int,
    val maxMinutes: Int,
    val emoji: String,
    val label: String
)

object MindfulQuotes {
    val quotes = listOf(
        "Ask yourself if what you're doing today is getting you closer to where you want to be tomorrow.",
        "Almost everything will work again if you unplug it for a few minutes, including you.",
        "You don't need more time, you just need to decide.",
        "Focus is the art of knowing what to ignore.",
        "Beware the barrenness of a busy, distracted life.",
        "Your attention is your most valuable currency. Spend it wisely."
    )

    val dailyLimitBands = listOf(
        DailyLimitEmojiBand("band_0_15", 0, 15, "🧘", "Zen Master (0–15m)"),
        DailyLimitEmojiBand("band_15_30", 15, 30, "🎯", "Deep Focus (15–30m)"),
        DailyLimitEmojiBand("band_30_45", 30, 45, "⚡", "Sharp Mind (30–45m)"),
        DailyLimitEmojiBand("band_45_60", 45, 60, "🌱", "Balanced (45–60m)"),
        DailyLimitEmojiBand("band_60_90", 60, 90, "⏳", "Moderate (1h–1h 30m)"),
        DailyLimitEmojiBand("band_90_120", 90, 120, "🌥️", "Casual (1h 30m–2h)"),
        DailyLimitEmojiBand("band_120_180", 120, 180, "⚠️", "High Usage (2h–3h)"),
        DailyLimitEmojiBand("band_180_240", 180, 240, "🔥", "Heavy Scroller (3h–4h)"),
        DailyLimitEmojiBand("band_240_360", 240, 360, "🌀", "Doomscrolling (4h+)")
    )

    fun bandForMinutes(minutes: Int): DailyLimitEmojiBand {
        return dailyLimitBands.firstOrNull { minutes in it.minMinutes..it.maxMinutes }
            ?: dailyLimitBands.last()
    }
}
