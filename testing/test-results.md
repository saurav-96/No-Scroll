# Phase 9 — Functional & On-Device Test Results (`FocusGuard`)

**Execution Date:** 2026-11-12  
**Device Tested:** Realme `RMX3630` (`VSOBHEKBPRSCJRFI`, Android 13 / API 33)  
**Build Variants Verified:** `FocusGuard-debug.apk` & `FocusGuard-release.apk`  

---

## 1. Automated Unit Test Results (`.\gradlew.bat testDebugUnitTest`)

| Test Case ID | Test Class & Method | Result | Notes |
|---|---|---|---|
| `UT-01` | `BlockingDecisionEngineTest.blockAll plan blocks immediately when not paused and mode is AS_PER_PLAN` | **PASS** | Verified immediate `BlockDecision.Block` |
| `UT-02` | `BlockingDecisionEngineTest.global pause allows playback even under Block All plan` | **PASS** | Verified `BlockDecision.Allow` with remaining pause duration |
| `UT-03` | `BlockingDecisionEngineTest.dontBlock override allows playback even under Block All plan` | **PASS** | Verified `DONT_BLOCK` overrides global `BLOCK_ALL` |
| `UT-04` | `BlockingDecisionEngineTest.allow1Min allows for 60s then blocks for 15s cooldown then resets` | **PASS** | Verified `60,000 ms` active window + `15,000 ms` mandatory cooldown + window reset |
| `UT-05` | `BlockingDecisionEngineTest.oneReel plan allows first reel and blocks on vertical scroll after 3s grace period` | **PASS** | Verified `3,000 ms` grace period and vertical swipe / signature change block |
| `UT-06` | `PinValidatorTest.custom pin validates exact match` | **PASS** | Verified static 4-digit PIN match and rejection |
| `UT-07` | `PinValidatorTest.dynamic time pin formats 24h 12h and reverse 24h accurately` | **PASS** | Verified `HHmm` (`1437`), `hhmm` (`0237`), and `mmHH` (`3714`) |
| `UT-08` | `PinValidatorTest.dynamic date pin formats DDMM and MMDD accurately` | **PASS** | Verified `ddMM` (`0811`) and `MMdd` (`1108`) |

---

## 2. Live On-Device Verification Results (`VSOBHEKBPRSCJRFI`)

| Test Case ID | Feature / Scenario | Result | Evidence |
|---|---|---|---|
| `DT-01` | APK Installation & Cold Start | **PASS** | Installed via `adb -s VSOBHEKBPRSCJRFI install -r release\FocusGuard-debug.apk`; launched `com.saurav.focusguard/.MainActivity` (`Status: ok`, `TotalTime: 2405ms`, zero crashes) |
| `DT-02` | Home Top UI & Quick-Stats Pill | **PASS** | [recreated_01_home_top.png](file:///c:/Users/Administrator/Desktop/No%20Scroll/testing/screenshots/recreated_01_home_top.png) and [fg_ui_home.xml](file:///c:/Users/Administrator/Desktop/No%20Scroll/testing/fg_ui_home.xml) confirm exact layout, colors, and interactive nodes |
| `DT-03` | Doom-Scrolling Apps, Browsers & Installed Package Detection | **PASS** | [recreated_02_home_apps_upper.png](file:///c:/Users/Administrator/Desktop/No%20Scroll/testing/screenshots/recreated_02_home_apps_upper.png), [recreated_03_home_apps_middle.png](file:///c:/Users/Administrator/Desktop/No%20Scroll/testing/screenshots/recreated_03_home_apps_middle.png), and [recreated_04_home_bottom.png](file:///c:/Users/Administrator/Desktop/No%20Scroll/testing/screenshots/recreated_04_home_bottom.png) confirm installed detection (`Youtube`, `Whatsapp`, `LinkedIn`, `Reddit`, `YouTube Music` expanded; `Instagram`, `Snapchat`, `X (Twitter)` marked `• App not installed!`) |
| `DT-04` | Blocker Shield & Daily Limit Arc Gauge | **PASS** | [recreated_05_blocker_shield.png](file:///c:/Users/Administrator/Desktop/No%20Scroll/testing/screenshots/recreated_05_blocker_shield.png) confirms `240°` circular gauge (`0m of 2h 30m`), `Daily Limit: 2h 30m ✎`, `PAUSE`, `Blocked Apps`, and `Blocked Websites` |
| `DT-05` | Settings Top & Bottom (`Blocking Mode`, `NoScroll Pin`, `Uninstall Protection`, `App Theme`) | **PASS** | [recreated_06_settings_top.png](file:///c:/Users/Administrator/Desktop/No%20Scroll/testing/screenshots/recreated_06_settings_top.png) and [recreated_07_settings_bottom.png](file:///c:/Users/Administrator/Desktop/No%20Scroll/testing/screenshots/recreated_07_settings_bottom.png) confirm all settings cards, segmented chips, and switches |
| `DT-06` | Live YouTube Shorts Detection, Blocking & Room Analytics Persistence | **PASS** | During on-device testing when YouTube Shorts was opened on `VSOBHEKBPRSCJRFI`, `FocusGuardAccessibilityService` detected `:id/reel_player_underlay`, executed `GLOBAL_ACTION_BACK`, and incremented the live `Blocked` counter on the Home Quick-Stats pill from `0` to `3` |
| `DT-07` | Release APK Build, R8 Minification & Signing | **PASS** | `:app:assembleRelease` succeeded; generated signed [release/FocusGuard-release.apk](file:///c:/Users/Administrator/Desktop/No%20Scroll/release/FocusGuard-release.apk) (`2,000,863` bytes) and [release/FocusGuard-debug.apk](file:///c:/Users/Administrator/Desktop/No%20Scroll/release/FocusGuard-debug.apk) (`19,511,273` bytes) |
