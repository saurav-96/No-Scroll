# Phase 9 — Known Limitations & Platform Notes (`FocusGuard`)

## 1. Android OS & OEM Constraints
1. **Accessibility Service View-ID Sensitivity:**
   - Like the original `NoScroll` application (`com.newswarajya.noswipe.reelshortblocker`), short-video detection relies on `AccessibilityNodeInfo.getViewIdResourceName()` and content descriptions exposed by third-party applications (`YouTube`, `Instagram`, `Snapchat`, `WhatsApp`, `X`, `LinkedIn`, `Reddit`, `YouTube Music`, `TikTok`, `Facebook`).
   - If a third-party app obfuscates or renames its internal resource IDs in a future update, `PlatformDetectorRegistry.kt` must be updated with the new view-ID suffixes (the original app uses Firebase Remote Config for this; `FocusGuard` centralizes all rules in `PlatformDetectorRegistry.kt`).
2. **ColorOS / Realme Background Process Management:**
   - On Realme / OPPO / OnePlus devices running ColorOS / Realme UI (`RMX3630`), aggressive battery optimization can suspend background services if the user swipes the app away from Recents unless **Allow Background Activity** / **Ignore Battery Optimizations** is enabled. `FocusGuard` includes both a persistent `AppMonitorForegroundService` notification and `BootCompletedReceiver` to maximize service uptime.
3. **Instagram Direct Message (DM) Reel Exception:**
   - Detecting whether an Instagram Reel was opened from a Direct Message thread checks for the presence of `:id/reply_bar_edittext` in the active window hierarchy (matching the original APK's `res/raw/platforms_config.json`). If Instagram changes its DM reel viewer layout to omit the reply bar, DM reels will be treated as standard Reels.
4. **Device Admin Uninstall Protection:**
   - When **Uninstall Protection** is enabled in Settings, Android's `DevicePolicyManager` prevents uninstallation until the user disables `FocusGuard` under `Settings -> Security -> Device Admin Apps` (or toggles it off inside `FocusGuard`).
