# Phase 4 — APK Static Investigation Report (`com.newswarajya.noswipe.reelshortblocker`)

**Date:** 2026-11-12  
**Target Package:** `com.newswarajya.noswipe.reelshortblocker`  
**Version:** `2.1.4` (`versionCode = 118`)  
**Acquisition Method:** Pulled directly from user's connected physical Android device (`RMX3630`, serial `VSOBHEKBPRSCJRFI`) via REA `pull_adb_package` (`ev_5f362f6e721ee7adddee4b0122c7e222cdaa030e15220dee7a0522e6f3afc2fa`)  
**Analysis Providers:** REA `jadx-headless-mcp` (`0.7.1`), `apktool` (`3.0.3`), standalone `jadx` (`1.5.6`)

---

## 1. Acquired APK Artifacts & Cryptographic Digests

Stored in `research/original-apk/com.newswarajya.noswipe.reelshortblocker/`:

| Artifact | Size (Bytes) | SHA-256 Digest | Role |
|---|---|---|---|
| `base.apk` | `25,783,703` | `87fc919620bf827d9baef06357a73f2fba61a26f0c21f48b9eeaf019840ea7c7` | Main DEX (`22,873` classes), resources (`908` entries), `res/raw/*.json`, `assets/*` |
| `split_config.arm64_v8a.apk` | `237,982` | `94582a8db46c035d47ca58a8521b4d313d24abafc0d49633a06831fe4300da01` | Native ARM64 libraries (`libandroidx.graphics.path.so`, `libdatastore_shared_counter.so`, `libunitycoherencenative.so`) |
| `split_config.en.apk` | `53,458` | `b335e66ff82f6e29225460f27e250f59120055acad72f47568df295094b14e5c` | English string split resources |
| `split_config.xxhdpi.apk` | `79,275` | `446f5dc147a54202f48e38607bc825f1f565be3bd3a2c5541dddb9e0da61bab8` | Display density (`xxhdpi`) drawables |

---

## 2. Manifest & Component Architecture (`AndroidManifest.xml`)

Evidence ID: `ev_68a8ac9699420629569a8cbad594e5fece6ef721ef54805dba22e56346b23b59`

- **SDK Levels:** `minSdkVersion = 32`, `targetSdkVersion = 37`, `compileSdkVersion = 37`
- **Application Class:** `com.newswarajya.noswipe.reelshortblocker.NoScrollApplication` (Hilt-enabled `Application` entry point)

### A. Activities
1. **`com.newswarajya.noswipe.reelshortblocker.activities.home.HomeActivity`**
   - `exported="true"`, `launchMode="singleTask"`, `windowSoftInputMode="adjustResize"`
   - Intent Filter: `android.intent.action.MAIN` + `android.intent.category.LAUNCHER`
   - Hosts the single-activity Jetpack Compose navigation graph (`Dashboard`, `Blocker Shield`, `Settings`, `Analytics`, `All Apps`, `Block Websites`, `PIN Config`, `Support`).
2. **`com.newswarajya.noswipe.reelshortblocker.activities.pinblockoverlay.PinBlockOverlayActivity`**
   - `exported="false"`, `excludeFromRecents="true"`
   - Full-screen PIN / App Block / Mindful Wait / Cooldown intervention activity launched when a restricted or PIN-locked app is opened.

### B. Services
1. **`com.newswarajya.noswipe.reelshortblocker.service.accessibility.NoScrollAccessibilityService`**
   - `permission="android.permission.BIND_ACCESSIBILITY_SERVICE"`, `exported="false"`
   - `process=":as_process"` (configured in manifest) / `:accessibility_service_process` (in `@xml/site_manager_service`)
   - `foregroundServiceType="specialUse"` (`stopWithTask="false"`, `enabled="true"`)
   - Metadata `@xml/site_manager_service`:
     - `accessibilityEventTypes`: `typeViewClicked | typeViewLongClicked | typeWindowStateChanged | typeWindowContentChanged | typeViewScrolled | typeGestureDetectionStart | typeGestureDetectionEnd | typeTouchInteractionStart | typeTouchInteractionEnd`
     - `accessibilityFlags`: `flagRequestEnhancedWebAccessibility | flagReportViewIds | flagRequestFilterKeyEvents | flagRetrieveInteractiveWindows`
     - `canRetrieveWindowContent="true"`, `canRequestFilterKeyEvents="true"`, `notificationTimeout="150"`
2. **`com.newswarajya.noswipe.reelshortblocker.service.AppMonitorService`**
   - `exported="false"`, `foregroundServiceType="specialUse"`
   - Property `android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE`: *"Monitoring app usage for PIN lock functionality"*
   - Monitors foreground app usage via `UsageStatsManager` when `NoScrollAccessibilityService` is not active, and nudges the Accessibility Service if permission is granted.

### C. Broadcast Receivers
1. **`com.newswarajya.noswipe.reelshortblocker.service.receivers.SystemReceiver`**
   - `exported="false"`, `directBootAware="true"`, `priority="1000"`
   - Listens for `BOOT_COMPLETED`, `MY_PACKAGE_REPLACED`, `QUICKBOOT_POWERON`, `com.htc.intent.action.QUICKBOOT_POWERON`, `REBOOT`.
2. **`com.newswarajya.noswipe.reelshortblocker.service.broadcastlisteners.AccessibilityStatusReceiver`**
   - Listens for `com.newswarajya.noswipe.reelshortblocker.SERVICE_STOPPED`.
3. **`com.newswarajya.noswipe.reelshortblocker.service.deviceadmin.NSDeviceAdminReceiver`**
   - `permission="android.permission.BIND_DEVICE_ADMIN"`, `exported="true"`
   - Policy `@xml/device_admin_policies` (`<disable-uninstall />`) — prevents uninstallation while Uninstall Protection is enabled.
4. **`com.newswarajya.noswipe.reelshortblocker.notifications.NotificationDismissBroadcastReceiver`**
   - Handles notification dismissal callbacks.

### D. Content Providers
1. **`com.newswarajya.noswipe.reelshortblocker.data.database.contentprovider.NoScrollContentProvider`**
   - `authorities="com.newswarajya.noswipe.provider"`, `exported="false"`
   - Cross-process IPC bridge between the UI process and `:as_process`.

### E. Declared Permissions
- `android.permission.POST_NOTIFICATIONS`
- `android.permission.SYSTEM_ALERT_WINDOW` (floating Scroll Watchtime counter & overlays)
- `android.permission.PACKAGE_USAGE_STATS` (Usage Access for app time limits & fallback app monitor)
- `android.permission.QUERY_ALL_PACKAGES` (enumerate installed apps in `All Apps` & `Scroll Blocklist`)
- `android.permission.VIBRATE` (Haptic Feedback on block / counter milestone)
- `android.permission.FOREGROUND_SERVICE` & `android.permission.FOREGROUND_SERVICE_SPECIAL_USE`
- `android.permission.RECEIVE_BOOT_COMPLETED`
- `android.permission.WAKE_LOCK`

---

## 3. Core Data Models, Enums & Business Rules

### A. Focus Plans (`PlansEnum`)
1. **`BLOCK_ALL` (`Block All`):**
   - Tagline: `"All Reels & Shorts are blocked."`
   - Immediately blocks any detected short-form video node using the active `BlockingModesEnum` unless overridden by a per-platform rule or active `Pause`.
2. **`CURIOUS` (`Curious`):**
   - Tagline: `"Watch reels for %1$s minutes then block for %2$s minutes"`
   - Managed by `CuriousSessionData` (`consumeVideoIfAvailable()`). Allows watching during the configured `Curious Session` window, then transitions into `Curious Cooldown` where all reels are blocked.
3. **`ONE_REEL` (`One Reel`):**
   - Tagline: `"Watch 1 reel like a normal video"` / `"Watch one reel, then stop"`
   - Decompiled in `NoScrollServiceModel.processAndBlockShortContent$handleOneReel` (`ev_56e8fb0c3c8b3dda9afa583fe64a08e423311093b5ef81f0331e73502942c90a`):
     - When `oneReelPlayStatus == null` (and `now - lastVideoBlocked > 5000 ms`), records `OneReelPlayStatus(reelStartedOn = now, lastReel = signature)` and allows the first reel.
     - Once `now - reelStartedOn >= 3000 ms`, if the reel signature changes (user swiped to a second reel) or `now - reelStartedOn >= 300,000 ms` (5-minute cap), invokes the block callback (`li3Var.invoke(ow6Var.a)`).

### B. Per-Platform Restriction Modes (`PlatformRestrictionEnum`)
Evidence ID: `ev_1ae0b7324c6278ad685b594bbfc5515d804ac4cb2b053754c62f5c0b35264dc1`

| Enum Constant | UI Label | Allowed Duration (`millis`) | Cooldown Duration (`calmDown`) | Behavior |
|---|---|---|---|---|
| `AS_PER_PLAN` | `Default: Active Plan` | `-1` | `0` | Follows global active plan (`Block All`, `Curious`, `One Reel`) |
| `ALWAYS` | `Always Block` | `0` | `0` | Always blocks regardless of active plan or pause |
| `ONE_MIN` | `Allow: 01 Min` | `60,000 ms` (1 min) | `15,000 ms` (15 sec) | Allows scrolling for 1 min from `firstWatch`, then blocks for 15 sec cooldown |
| `FIVE_MIN` | `Allow: 05 Min` | `300,000 ms` (5 min) | `60,000 ms` (1 min) | Allows scrolling for 5 min from `firstWatch`, then blocks for 1 min cooldown |
| `TEN_MIN` | `Allow: 10 Min` | `600,000 ms` (10 min) | `120,000 ms` (2 min) | Allows scrolling for 10 min from `firstWatch`, then blocks for 2 min cooldown |
| `NEVER` | `Don't Block` | `Long.MAX_VALUE` | `0` | Never blocks this specific platform |

### C. Blocking Actions (`BlockingModesEnum` & `AppLockActionEnum`)
Evidence ID: `ev_67701427a16930d5dec8ff87e042e5a5c2863062c8021bd02adb3721136fcd41` & `ev_ff6d7ea2940e559d47501f795cd2ae7f03c64a01949797ce83e9e467ec2bcf08`

1. **`PRESS_BACK` (`Close Player`):**
   - Description: `"Instantly exits the reel player"`
   - Execution in `NoScrollAccessibilityService.c`: Calls `performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)` (`1`) with a `1,100 ms` / `1,200 ms` debounce guard (`this.r`), triggers optional vibration (`im3.O(...)`), and logs a block event to Room.
2. **`KILL_APP` (`Close App`):**
   - Description: `"Exits the reel and Minimizes the app"`
   - Execution: Calls `k(AppLockActionEnum.CLOSE_APP)` which performs `GLOBAL_ACTION_HOME` (`2`) to minimize the app to the launcher.
3. **`LOCK_SCREEN` (`Lock Screen`):**
   - Description: `"Exits the reel and locks your phone"`
   - Execution: Calls `k(AppLockActionEnum.LOCK_SCREEN)` which performs `GLOBAL_ACTION_LOCK_SCREEN` (`8`, API 28+).
4. **`NONE` (`None`):**
   - Description: `"No blocking for 1 reel"` (used for whitelist rules like `Allow Reels By Friends`).

### D. Short-Form Video Detection Rules (`res/raw/platforms_config.json`)
The shipped `res/raw/platforms_config.json` (version `51`) defines view-ID (`FINDBYID`), resource-name (`VIEWID_RES_NAME`), and content-description (`CONT_DESC`) detectors for short-form feeds while leaving regular long-form video players untouched:

| App Name | Package Name | Platform Name (`platformId`) | Detector Type & View Identifiers | Default Status |
|---|---|---|---|---|
| **YouTube** | `com.google.android.youtube` | `YouTube Shorts` (`yt_shorts`) | `FINDBYID`: `:id/reel_player_underlay` (also supports Open Action URLs: `Search` & `Subscriptions`) | `true` (`AS_PER_PLAN`) |
| **YouTube ReVanced / Vanced** | `app.revanced.android.youtube`, `com.biomes.vanced` | `YouTube Revanced Shorts` | `FINDBYID`: `:id/reel_player_underlay`, `:id/progress` | `true` |
| **Instagram** | `com.instagram.android` | `Instagram Reels` (`ig_reel`) | `FINDBYID`: `:id/like_button`, `:id/clips_video_container` | `true` (`AS_PER_PLAN`) |
| **Instagram** | `com.instagram.android` | `Allow Reels By Friends` (`ig_reel_by_friend`) | `FINDBYID`: `:id/reply_bar_edittext` -> `BlockingModesEnum.NONE` (priority `0` overrides `ig_reel` when viewing DM reels!) | `false` |
| **Instagram** | `com.instagram.android` | `Instagram Stories` (`ig_stories`) | `FINDBYID`: `:id/reel_viewer_title` | `false` |
| **Instagram** | `com.instagram.android` | `Instagram Explore/Search` (`ig_explore`) | `FINDBYID`: `:id/action_bar_search_edit_text` | `false` |
| **Snapchat** | `com.snapchat.android` | `Snapchat Spotlights` (`snap_spotlights`) | `FINDBYID`: `:id/spotlight_view_count`, `:id/favorite` | `true` (`AS_PER_PLAN`) |
| **Snapchat** | `com.snapchat.android` | `Snapchat Stories` (`snap_stories`) | `FINDBYID`: `:id/view_profile`; `VIEWID_RES_NAME`: `context_vertical_actions/context_vertical_action_share` | `true` |
| **Snapchat** | `com.snapchat.android` | `Snapchat Discover Page` (`snap_discover`) | `FINDBYID`: `:id/df_large_story` | `false` |
| **WhatsApp** | `com.whatsapp` | `WhatsApp Status` (`wa_status`) | `FINDBYID`: `:id/status_header` | `false` (`NEVER`) |
| **X (Twitter)** | `com.twitter.android` | `X Reels` (`x_twitter`) | `VIEWID_RES_NAME`: `VideoTab`; `FINDBYID`: `:id/mediacontroller_progress` | `true` (`AS_PER_PLAN`) |
| **LinkedIn** | `com.linkedin.android` | `LinkedIn Watch` (`linkedin_video`) | `FINDBYID`: `:id/media_viewer_actor_bottom` | `true` |
| **Reddit** | `com.reddit.frontpage` | `Reddit Watch` (`reddit_watch`) | `VIEWID_RES_NAME`: `content_video_view` | `true` (`AS_PER_PLAN`) |
| **YouTube Music** | `com.google.android.apps.youtube.music` | `YT Music Samples` (`ytm_shorts`) | `FINDBYID`: `:id/reel_player_page_container` | `false` (`NEVER`) |
| **Facebook** | `com.facebook.katana` | `Facebook Reels` (`fb_reel`) | `CONT_DESC`: `"Add a comment…"`, `"Add a comment"` | `true` |
| **TikTok** | `com.zhiliaoapp.musically`, `com.ss.android.ugc.trill` | `TikTok Videos` (`tiktok_clips`) | `FINDBYID`: `:id/desc` (`KILL_APP` / `LOCK_SCREEN`) | `true` |

### E. Browser URL & Short-Form Web Blocking
- `NoScrollServiceModel.getBrowsersList()` maps installed browser packages (`com.android.chrome`, `org.mozilla.firefox`, `com.brave.browser`, `com.microsoft.emmx`, `com.opera.browser`, `com.opera.mini.native`, `com.sec.android.app.sbrowser`, `com.duckduckgo.mobile.android`, `com.vivaldi.browser`, `com.jio.web`, etc.) to their URL bar view IDs (e.g., `com.android.chrome:id/url_bar`).
- Inspects the active URL in the browser's address bar for:
  1. Web short-form URLs (`youtube.com/shorts/`, `instagram.com/reels/`, `tiktok.com/@`, `facebook.com/reel/`).
  2. User-configured blocked domains and keywords from `Blocked Websites` (`WebRestrictionsStatus`).

### F. App Blocker, Daily Limit & Dynamic PIN (`PinConfig`, `DailyAppBlocker`, `PinBlockOverlayActivity`)
- **Daily Limit (`DailyAppBlocker`):**
  - Tracks cumulative daily usage across restricted apps against a configurable daily limit (default `2h 30m` = `150` minutes).
  - Uses `res/raw/daily_limit_emoji_bands.json` for visual feedback when editing the limit (`0m`: 🛑 *Total Lockdown!*, `1–30m`: 🚀 *Laser Focus*, `31–120m`: ⚖️ *Healthy Balance*, `121–240m`: 🕰️ *Slipping Away...*, `241+m`: 🧟 *Doomscroller Mode*).
- **PIN Protection (`PinOptionsEnum`):**
  - `NONE` (Disabled)
  - `CUSTOM` (User-defined 4–8 digit PIN)
  - `LIVE_TIME` (Dynamic PIN matching current device clock in `HHmm` 12h/24h or reversed format via `PINTimeFormatEnum`)
  - `DATE` (Dynamic PIN matching current date in `ddMM`, `MMdd`, `ddMMyy` format via `PINDateFormatEnum`)
  - `DEVICE_DEFAULT` (System Biometric / Device Credential prompt)
- **Mindful Wait Quotes (`assets/mindful_timer_quotes.json`):**
  - Displays a motivational quote from the 50-quote dataset during countdown/pause/unlock screens.

### G. Analytics & Persistence (`AnalyticsDatabase`, DataStore)
- **Room Database (`AnalyticsDatabase`):**
  - Persists scroll sessions (`startTime`, `endTime`, `durationMs`, `packageName`, `platformId`), block events (`timestamp`, `packageName`, `platformId`, `blockingMode`, `activePlan`), and daily app usage records.
  - Feeds the Home screen summary pill (`Sessions`, `Blocked`, `Scroll Time`) and the detailed Analytics screen.
- **Preferences DataStore:**
  - Stores active `PlansEnum`, `BlockingModesEnum`, `PinConfig`, `CuriousSessionData`, `PauseSessionData`, `PlatformRestrictionStatus` map, `WebRestrictionsStatus` map, theme selection (`Light`, `Dark`, `System Default`), haptic feedback toggle, and `Scroll Watchtime` overlay configuration.

---

## 4. Native Libraries Assessment
Inspection of `split_config.arm64_v8a.apk` confirmed only three standard third-party `.so` libraries:
1. `lib/arm64-v8a/libandroidx.graphics.path.so` — AndroidX Graphics Path library.
2. `lib/arm64-v8a/libdatastore_shared_counter.so` — Jetpack DataStore multi-process atomic counter.
3. `lib/arm64-v8a/libunitycoherencenative.so` — Unity Ads SDK native library.

**Conclusion:** No custom application logic resides in native code; 100% of NoScroll's functional behavior is implemented in Kotlin/Java DEX and JSON configuration resources, which have been completely recovered and documented above.
