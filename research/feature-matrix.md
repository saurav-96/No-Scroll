# Phase 5 — Comprehensive Feature Matrix (`com.newswarajya.noswipe.reelshortblocker` → `FocusGuard`)

**Date:** 2026-11-12  
**Evidence Sources:**
- Static APK Investigation (`research/apk-investigation-report.md`, `res/raw/platforms_config.json`, `res/raw/initial_config.json`, `res/raw/daily_limit_emoji_bands.json`, `assets/mindful_timer_quotes.json`)
- Reference UI Inventory (`research/ui-screen-inventory.md`, `./reference-ui/screen_01_*.jpg` – `screen_07_*.jpg`)
- Live Runtime Observation on `RMX3630` (`VSOBHEKBPRSCJRFI`) via REA ADB tools (`ev_7d0eddd018d7252d1448074442d43cda965f3ee6fa40981c76ce9d762db540d8`, `ev_d300cca2a55276f29054c2fc8fb67a72cc4ba040b96d4d8b546cba729db8a6d8`, `ev_e2c5df501857e88e9f379ffbcf8ca21ded447ceaefc0d349408257de183ec5de`, `ev_e267ac8be65b9fe902735bd5377534544d799aeff001d5bcb61b3aad5a3636da`, `ev_dc90e53428289763cced3f8959c87c4159f70b7331fcb195d6e4c81e313ca465`, and `noscroll_live_dump.xml`)

---

## 1. Feature Matrix Summary

| # | Feature Name | Involved Screens | Trigger Condition | Required Permissions | Confidence Level |
|---|---|---|---|---|---|
| 1 | **Short-Form Video Detection & Blocking** (YouTube Shorts, Instagram Reels, Snapchat Spotlights/Stories/Discover, WhatsApp Status, X Reels, LinkedIn Watch, Reddit Watch, YT Music Samples, TikTok, Facebook Reels) | Home (`Scroll Blocklist`), Accessibility background service | `AccessibilityEvent` (`TYPE_WINDOW_STATE_CHANGED` `32`, `TYPE_WINDOW_CONTENT_CHANGED` `2048`, `TYPE_VIEW_SCROLLED` `4096`) matching platform view IDs / resource names / content descriptions | `BIND_ACCESSIBILITY_SERVICE` | **Confirmed (Static + Live)** |
| 2 | **Preserving Regular Long-Form Videos** | Background Accessibility Service | Only short-form container view IDs (e.g., `:id/reel_player_underlay` on YouTube) trigger blocking; standard YouTube watch player (`:id/watch_player`) is never matched | `BIND_ACCESSIBILITY_SERVICE` | **Confirmed (Static + Live)** |
| 3 | **Allow Reels By Friends (Instagram DM Exception)** | Home (`Instagram` expanded card), Accessibility Service | When `:id/reply_bar_edittext` is present in the Accessibility node tree, priority-`0` detector returns `BlockingModesEnum.NONE`, allowing DM-shared reels | `BIND_ACCESSIBILITY_SERVICE` | **Confirmed (Static)** |
| 4 | **Focus Plan 1 — `Block All`** | Home (`SELECT PLAN`) | Default active plan (`PlansEnum.BLOCK_ALL`); blocks every detected short/reel unless overridden by per-platform rule or Pause | `BIND_ACCESSIBILITY_SERVICE` | **Confirmed (Static + Live)** |
| 5 | **Focus Plan 2 — `Curious`** | Home (`SELECT PLAN`, Curious Config sheet) | `PlansEnum.CURIOUS`; allows scrolling during `Curious Session` (`X` mins), then enforces `Curious Cooldown` (`Y` mins) blocking all reels | `BIND_ACCESSIBILITY_SERVICE` | **Confirmed (Static + Live)** |
| 6 | **Focus Plan 3 — `One Reel`** | Home (`SELECT PLAN`) | `PlansEnum.ONE_REEL`; allows the first opened reel (`OneReelPlayStatus`), then blocks as soon as the user swipes (`reelStartedOn + 3000 ms` & signature change) or after `5 min` (`300,000 ms`) | `BIND_ACCESSIBILITY_SERVICE` | **Confirmed (Static + Live)** |
| 7 | **Per-Platform Restriction Modes** (`Default: Active Plan`, `Always Block`, `Don't Block`, `Allow: 01 Min`, `05 Min`, `10 Min`) | Home (`Scroll Blocklist` platform dropdowns) | Per-platform `PlatformRestrictionEnum` override with automatic cooldown (`15s` after `1m`, `1m` after `5m`, `2m` after `10m`) | `BIND_ACCESSIBILITY_SERVICE` | **Confirmed (Static + Live)** |
| 8 | **Blocking Action Modes** (`Close Player`, `Close App`, `Lock Screen`) | Settings (`BLOCKER CONFIG` -> `Blocking Mode`) | Selected `BlockingModesEnum`: `PRESS_BACK` (`GLOBAL_ACTION_BACK`), `KILL_APP` (`GLOBAL_ACTION_HOME`), `LOCK_SCREEN` (`GLOBAL_ACTION_LOCK_SCREEN`) | `BIND_ACCESSIBILITY_SERVICE` | **Confirmed (Static + Live)** |
| 9 | **Temporary Pause & Mindful Cooldown** | Home (`Pause` amber pill, Pause bottom sheet), Blocker Shield (`PAUSE`) | User taps `Pause` and selects duration; suspends blocking during Pause phase, optionally followed by lockdown/cooldown | None (persisted in DataStore) | **Confirmed (Static + Live)** |
| 10 | **Scroll Watchtime Floating Overlay** | Home (`SCROLL WATCHTIME` card + gear config), System overlay window | When enabled and a short-form platform is active, displays a draggable floating pill (`00:00`) over the target app showing live session scroll time | `SYSTEM_ALERT_WINDOW`, `BIND_ACCESSIBILITY_SERVICE` | **Confirmed (Static + Live)** |
| 11 | **Daily Limit & Blocker Shield** | Blocker Shield tab (`Daily Limit: 2h 30m` arc gauge, `Blocked Apps`, `Blocked Websites`) | Tracks cumulative daily usage of restricted apps; when daily quota is exceeded or app is blocked/locked, launches `PinBlockOverlayActivity` | `PACKAGE_USAGE_STATS`, `BIND_ACCESSIBILITY_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE` | **Confirmed (Static + Live)** |
| 12 | **All Apps Blocker & App Lock** | Home (`All Apps`), Blocker Shield (`Blocked Apps`), `AllAppsScreen` | Per-app lock (🔑) or restriction action (`OVERLAY`, `CLOSE_APP`, `LOCK_SCREEN`) + optional YouTube launch redirect (`Search` / `Subscriptions`) | `QUERY_ALL_PACKAGES`, `BIND_ACCESSIBILITY_SERVICE`, `PACKAGE_USAGE_STATS` | **Confirmed (Static + Live)** |
| 13 | **Website & Browser Short-Form Blocker** | Home (`Supported Browsers`, `Block Websites`), Blocker Shield (`Blocked Websites`), `BlockWebsitesScreen` | Inspects browser URL bar node (`url_bar` / `mozac_browser_toolbar_url_view`) for custom blocked domains/keywords or web Shorts/Reels URLs | `BIND_ACCESSIBILITY_SERVICE` | **Confirmed (Static + Live)** |
| 14 | **NoScroll PIN & Dynamic PIN** (`Custom PIN`, `Live Time PIN`, `Date PIN`, `Device Default`) | Settings (`NoScroll Pin` -> `Configure`), `PinBlockOverlayActivity` | Protects app settings, plan downgrades, pause, and locked apps using static 4-digit PIN, live clock (`HHmm`), current date (`ddMM`/`MMdd`), or biometric/credential | Biometric / Keyguard (for `Device Default`) | **Confirmed (Static + Live)** |
| 15 | **Uninstall Protection** | Settings (`Uninstall Protection` switch) | Registers `DeviceAdminReceiver` with `<disable-uninstall />` policy | `BIND_DEVICE_ADMIN` | **Confirmed (Static + Live)** |
| 16 | **Haptic Feedback & Skip Mindful Wait** | Settings (`Haptic Feedback`, `Skip mindful wait`), `PinBlockOverlayActivity` | Vibrates via `Vibrator`/`VibrationEffect` when a reel or app is blocked; controls mindful quote countdown timer on block overlay | `VIBRATE` | **Confirmed (Static + Live)** |
| 17 | **Analytics & Scroll History** | Home (`Sessions | Blocked | Scroll Time` pink pill), `AnalyticsScreen` | Logs every scroll session and block event in Room database; aggregates daily/weekly/total sessions, blocks, scroll duration, and per-app breakdown | None (local Room DB) | **Confirmed (Static + Live)** |
| 18 | **Live Accessibility Status & Permission Onboarding** | Home (`NoScroll is not Running!` warning banner, hero status transformation, `NOTIFICATIONS` / battery cards) | Dynamically switches Home hero between `"Doom-Scrolling Not Fixed Yet!"` (when Accessibility is off) and active plan (`"Block All"`, `"Curious"`, `"One Reel"`) when Accessibility is running | `POST_NOTIFICATIONS`, `BIND_ACCESSIBILITY_SERVICE`, Battery Optimization | **Confirmed (Static + Live)** |

---

## 2. Detailed Feature Specifications & Independent Implementation Plan (`FocusGuard`)

### 2.1 Feature 1–3: Short-Form Video Detection Engine (`com.saurav.focusguard.detection`)
- **Original Behavior:**
  - `NoScrollAccessibilityService` listens for window state changes (`32`), window content changes (`2048` on application windows), and vertical scroll events (`4096` where `scrollDeltaY > Math.abs(scrollDeltaX)`).
  - Evaluates a per-package detector table (`platforms_config.json`) using `AccessibilityNodeInfo.findAccessibilityNodeInfosByViewId` (`FINDBYID`), recursive view-ID suffix matching (`VIEWID_RES_NAME`), and content description matching (`CONT_DESC`).
  - Debounces package inspection by `150 ms` and block actions by `1,100–1,200 ms`.
- **Observed Limitations:**
  - Relies on target social apps' view IDs remaining consistent across updates (mitigated by matching multiple candidate view IDs per platform).
- **Independent Implementation in `FocusGuard`:**
  - Implement `PlatformDetectorRegistry` preloaded with all supported apps and sub-platforms (`YouTube Shorts`, `Instagram Reels`, `Allow Reels By Friends`, `Instagram Stories`, `Instagram Explore`, `Snapchat Spotlights`, `Snapchat Stories`, `Snapchat Discover`, `WhatsApp Status`, `X Reels`, `LinkedIn Watch`, `Reddit Watch`, `YT Music Samples`, `Facebook Reels`, `TikTok Videos`).
  - Check installed status dynamically via `PackageManager` (`App not installed!` indicator when absent, or expandable sub-platform controls when installed, plus toggle to test/configure all platforms).

### 2.2 Feature 4–8: Focus Plans, Per-Platform Timers & Blocking Actions (`com.saurav.focusguard.domain` & `services`)
- **Original Behavior:**
  - Evaluates hierarchy:
    1. If platform is `NEVER` (`Don't Block`) or `Allow Reels By Friends` matched -> allow.
    2. If platform is `ALWAYS` (`Always Block`) -> block immediately.
    3. If platform is `ONE_MIN` / `FIVE_MIN` / `TEN_MIN`:
       - Checks elapsed time since `firstWatchTimestamp`.
       - If within allowed window (`60s` / `300s` / `600s`), allows scrolling.
       - If between `allowed` and `allowed + calmDown` (`15s` / `60s` / `120s`), blocks immediately (cooldown active).
       - Once cooldown expires, resets `firstWatchTimestamp` on next visit.
    4. If platform is `AS_PER_PLAN` (`Default: Active Plan`):
       - Checks if global `Pause` is active -> allow.
       - If `BLOCK_ALL` -> block immediately.
       - If `CURIOUS` -> if within `curiousSessionMinutes` (default `5m`), allow; if in `curiousCooldownMinutes` (default `15m`), block.
       - If `ONE_REEL` -> allow first reel (`OneReelState`), then block when user swipes vertically (`TYPE_VIEW_SCROLLED` or node signature change after `3,000 ms`) or after `5` minutes.
  - Executes selected `BlockingMode`:
    - `CLOSE_PLAYER` -> `performGlobalAction(GLOBAL_ACTION_BACK)`
    - `CLOSE_APP` -> `performGlobalAction(GLOBAL_ACTION_HOME)`
    - `LOCK_SCREEN` -> `performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)`
- **Independent Implementation in `FocusGuard`:**
  - Implement `BlockingDecisionEngine` as a pure, unit-testable Kotlin domain service backed by `FocusPreferencesRepository` (DataStore) and invoked by `FocusGuardAccessibilityService`.
  - Unlock all premium features (`One Reel`, `LinkedIn Watch`, `Haptic Feedback`, `Max Pause Duration`, etc.) so they are 100% functional out of the box while preserving the exact visual badges from `./reference-ui/`.

### 2.3 Feature 9–10: Pause Timer & Floating Scroll Watchtime Overlay (`com.saurav.focusguard.services`)
- **Original Behavior:**
  - Tapping `Pause` opens a duration selector (`1m`, `5m`, `10m`, `15m`, `30m`, `60m`) and starts a live countdown pill on Home (`Pause 04:59`) and Blocker Shield.
  - Enabling `Scroll Watchtime` displays a floating pill window (`WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY`) showing `MM:SS` of active scroll time whenever a short-form feed is open.
- **Independent Implementation in `FocusGuard`:**
  - Implement `ScrollWatchtimeOverlayManager` inside `FocusGuardAccessibilityService` using `WindowManager` (`TYPE_APPLICATION_OVERLAY` when permission granted, with fallback to Accessibility overlay `TYPE_ACCESSIBILITY_OVERLAY` so it works seamlessly even before separate overlay permission!).

### 2.4 Feature 11–16: Blocker Shield, Daily Limit, App & Website Blocker, Dynamic PIN & Uninstall Protection
- **Original Behavior:**
  - `NoScroll Blocker Shield` displays a `240°` circular arc gauge for `Daily Limit` (default `2h 30m`, editable with live emoji bands from `daily_limit_emoji_bands.json`), a `PAUSE` button, and navigation cards for `Blocked Apps` and `Blocked Websites`.
  - `All Apps` lets the user toggle PIN lock or App Block per installed app.
  - `Block Websites` lets the user add/remove domain URLs and keywords, enforced inside supported browsers (`Chrome`, `Brave`, `Firefox`, `Edge`, `Opera`, `Samsung Internet`, etc.).
  - `NoScroll Pin` supports `Custom PIN`, `Live Time PIN` (current `HHmm` e.g. `1009`), `Date PIN` (current `ddMM` or `MMdd`), and `Device Default`.
  - When a locked/blocked app is opened, `PinBlockOverlayActivity` displays a mindful quote from `mindful_timer_quotes.json` and the PIN keypad.
  - `Uninstall Protection` activates `FocusGuardDeviceAdminReceiver`.
- **Independent Implementation in `FocusGuard`:**
  - Implement `PinValidator` supporting all 4 PIN modes (`CUSTOM`, `LIVE_TIME_24H`, `LIVE_TIME_12H`, `DATE_DDMM`, `DATE_MMDD`, `DEVICE_DEFAULT`) with comprehensive unit tests.
  - Implement `PinBlockOverlayActivity` with mindful countdown wait (skippable when `Skip mindful wait` is enabled) and keypad verification.
  - Implement `AppMonitorForegroundService` + `FocusGuardDeviceAdminReceiver` + `BootCompletedReceiver`.

### 2.5 Feature 17: Analytics Engine (`com.saurav.focusguard.analytics`)
- **Original Behavior:**
  - Tracks `Sessions` count, `Blocked` reels/apps count, and `Scroll Time` (`MM:SS`), displayed on the Home pink gradient pill (`#EC0D66` → `#FF1744`) and detailed in `AnalyticsScreen` (tapped via the `→` button on the pink pill).
- **Independent Implementation in `FocusGuard`:**
  - Implement Room database (`FocusGuardDatabase`) with `ScrollSessionEntity` and `BlockEventEntity`, DAO flows (`AnalyticsDao`), and `AnalyticsScreen` showing daily/weekly charts, platform breakdown, and recent block history.
