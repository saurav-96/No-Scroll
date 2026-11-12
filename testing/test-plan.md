# Phase 9 — Functional & UI Verification Test Plan (`FocusGuard`)

**Application Under Test:** `FocusGuard` (`com.saurav.focusguard`)  
**Target Device:** Realme `RMX3630` (`VSOBHEKBPRSCJRFI`, Android 13 / API 33, `1080x2400`)  
**Baseline Reference App:** `NoScroll: Block Reels & Shorts` (`com.newswarajya.noswipe.reelshortblocker`)  

---

## 1. Automated Unit Test Suite (`testDebugUnitTest`)

| Test Case ID | Component | Scenario | Expected Result |
|---|---|---|---|
| `UT-01` | `BlockingDecisionEngine` | `BLOCK_ALL` active plan with `AS_PER_PLAN` platform mode and no active pause | Immediately returns `BlockDecision.Block("Block All plan active")` |
| `UT-02` | `BlockingDecisionEngine` | Global temporary pause (`pausedUntilMillis > nowMillis`) while `BLOCK_ALL` is active | Returns `BlockDecision.Allow` with remaining pause duration |
| `UT-03` | `BlockingDecisionEngine` | Per-platform override set to `DONT_BLOCK` while `BLOCK_ALL` is active | Returns `BlockDecision.Allow` |
| `UT-04` | `BlockingDecisionEngine` | Timed platform allowance `ALLOW_1_MIN` (`60,000 ms` window, `15,000 ms` cooldown) | Allows for `60s`, transitions to `Block` at `61s`, enforces `15s` cooldown (`71s`), and starts fresh `60s` window at `77s` |
| `UT-05` | `BlockingDecisionEngine` | `ONE_REEL` active plan with `3,000 ms` grace period and swipe detection | Allows first reel on entry (`t = 0s`) and within grace period (`t = 1.5s`), then returns `Block` on vertical scroll / reel signature change after `3s` (`t = 4.0s`) |
| `UT-06` | `PinValidator` | `PinOption.CUSTOM` validation (`"2580"`) | Accepts `"2580"` and rejects `"0000"` |
| `UT-07` | `PinValidator` | `PinOption.TIME` dynamic PIN formatting (`14:37`) | Produces `"1437"` (`HH_MM_24`), `"0237"` (`HH_MM_12`), and `"3714"` (`MM_HH_24`) |
| `UT-08` | `PinValidator` | `PinOption.DATE` dynamic PIN formatting (`2026-11-08`) | Produces `"0811"` (`DD_MM`) and `"1108"` (`MM_DD`) |

---

## 2. Live On-Device Verification Suite (`VSOBHEKBPRSCJRFI`)

| Test Case ID | Feature Category | Verification Method | Pass Criteria |
|---|---|---|---|
| `DT-01` | APK Installation & Startup | Install `FocusGuard-debug.apk` via ADB (`install_adb_package`) and launch `com.saurav.focusguard/.MainActivity` | App installs cleanly, launches without crash, and renders `HomeScreen` |
| `DT-02` | UI Reconstruction — Home Top | Capture screenshot of `HomeScreen` top and compare against `./reference-ui/screen_01_home_top.jpg` | Matches header (`NoScroll` + `UPGRADE` + diamond circle), `Block All` concentric hero ring, pink quick-stats pill (`Sessions | Blocked | Scroll Time`), `SELECT PLAN` + amber `Pause`, 3 plan cards (`Curious`, `Block All`, `One Reel`), `NoScroll` card, and `Scroll Watchtime` row |
| `DT-03` | UI Reconstruction — Doom-Scrolling Apps & Browsers | Scroll `HomeScreen` through `DOOM-SCROLLING APPS`, `BROWSERS`, `ALL APPS & SITES`, `SUPPORT` and compare against `screen_02`, `screen_03`, `screen_04` | Matches `NoScroll Community` banner, `Scroll Blocklist` (`Collapse ^`), expandable platform cards (`Youtube`, `Instagram`, `Snapchat`, `Whatsapp`, `X (Twitter)`, `LinkedIn`, `Reddit`, `YouTube Music`), `Supported Browsers`, `All Apps`, `Block Websites`, and `Support` |
| `DT-04` | UI Reconstruction — Blocker Shield | Navigate to `Blocker` tab and compare against `screen_05_blocker_shield.jpg` | Matches `NoScroll Blocker Shield` header, `Daily Limit: 2h 30m ✎`, `240°` open-bottom circular gauge (`0m of 2h 30m`), `|| PAUSE`, `Blocked Apps`, and `Blocked Websites` cards |
| `DT-05` | UI Reconstruction — Settings | Navigate to `Settings` tab (top & bottom) and compare against `screen_06_settings_top.jpg` & `screen_07_settings_bottom.jpg` | Matches `Settings` + `Restore Purchase`, `NoScroll Basic` (`ACTIVE` + `UPGRADE TO PREMIUM` gradient button), `Haptic Feedback`, `Max Pause Duration`, `Blocking Mode` (`Close Player`, `Close App`, `Lock Screen`), `NoScroll Pin` (`Configure`), `Uninstall Protection`, `Skip mindful wait`, `App Theme` (`Light`, `Dark`, `System Default`), and `Privacy Policy` |
| `DT-06` | Accessibility Service Registration | Enable `com.saurav.focusguard/com.saurav.focusguard.services.FocusGuardAccessibilityService` on device and inspect service status | Service binds cleanly and monitors active window view IDs without impacting regular long-form video playback |
| `DT-07` | Sub-Screen Navigation & Persistence | Open `AnalyticsScreen`, `AllAppsScreen`, `BlockWebsitesScreen`, and `PinConfigScreen` | All sub-screens render, persist state in DataStore/Room, and navigate back cleanly |
