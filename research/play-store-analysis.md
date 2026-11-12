# Google Play Store Listing Analysis — Phase Three

**Target URL:** https://play.google.com/store/apps/details?id=com.newswarajya.noswipe.reelshortblocker&hl=en&pli=1  
**Application Name:** NoScroll: Block Reels & Shorts  
**Package Name:** `com.newswarajya.noswipe.reelshortblocker`  
**Developer:** NewSwarajya / Curizic  
**Category:** Productivity / Digital Wellbeing  

---

## 1. Core Value Proposition & Description

**NoScroll: Block Reels & Shorts** is an Android productivity and digital-wellbeing application designed to stop doomscrolling on short-form video feeds (Instagram Reels, YouTube Shorts, TikTok, Snapchat Spotlight, WhatsApp Status, LinkedIn Watch, Reddit Watch, YouTube Music Samples) **without blocking the entire host application**. Users retain full access to messaging, DMs, search, posts, and long-form videos while addictive short-video feeds are selectively intercepted.

---

## 2. Documented & Observable Feature Set

### 2.1 Short-Form Video & Feed Blocking (`Scroll Blocklist`)
- **YouTube Shorts** (`YouTube Shorts` sub-toggle with plan selector: `Default: Active Plan`, `Block All`, `Curious`, `One Reel`, `Don't Block`)
- **Instagram Reels**
- **Snapchat Spotlight / Stories**
- **WhatsApp Status** (`WhatsApp Status` sub-toggle)
- **X (Twitter)**
- **LinkedIn Watch** (`LinkedIn Watch` sub-toggle)
- **Reddit Watch** (`Reddit Watch` sub-toggle)
- **YouTube Music Samples** (`YT Music Samples` sub-toggle)
- **Supported Browsers** (intercepting short-video/blocked URLs inside mobile browsers)

### 2.2 Focus Modes (`SELECT PLAN`)
1. **Block All:**
   - Immediately blocks all enabled Reels and Shorts feeds as soon as the user navigates into them.
2. **Curious:**
   - Allows intentional viewing (e.g., when tapping a shared link or intentionally opening a short video with a mindful limit/wait) while preventing infinite vertical swipe-scrolling loops.
3. **One Reel:**
   - Allows viewing a single reel/short like a regular video and blocks as soon as the user swipes to the next reel.

### 2.3 Blocker Config & Restriction Actions (`Blocking Mode`)
- **Close Player:** Executes an Accessibility back action (`GLOBAL_ACTION_BACK`) to exit the short-video player back to the main app tab.
- **Close App:** Executes an Accessibility home action (`GLOBAL_ACTION_HOME`) to exit the host application to the home launcher.
- **Lock Screen:** Executes an Accessibility lock screen action (`GLOBAL_ACTION_LOCK_SCREEN`, API 28+) to lock the device when a blocked feed is entered.

### 2.4 Blocker Shield (`NoScroll Blocker Shield` Tab)
- **Daily Limit Gauge:** Configurable daily scroll/usage limit (e.g., `2h 30m`) with circular arc progress indicator (`0m of 2h 30m`).
- **Temporary Pause:** `PAUSE` button allowing temporary suspension of blocking with a countdown timer and configurable `Max Pause Duration`.
- **Blocked Apps (`All Apps`):** Full-app blocker/restrictor for any installed Android package.
- **Blocked Websites (`Block Websites`):** URL and keyword blocker across supported browsers.

### 2.5 Security, Protection & Preferences (`Settings` Tab)
- **NoScroll Pin:** PIN/Dynamic PIN protection for NoScroll settings and locked apps.
- **Uninstall Protection:** Extra confirmation/Device Admin or Accessibility settings guard against impulsive uninstallation.
- **Skip Mindful Wait:** Option controlling the mindful delay before pausing/bypassing.
- **Haptic Feedback:** Vibrate device when a reel or app is blocked.
- **Scroll Watchtime:** Floating watchtime counter (`00:00`) with toggle and settings gear.
- **App Theme:** `Light`, `Dark`, and `System Default`.

---

## 3. Permissions & Technical Disclosures

- **Accessibility Service (`android.permission.BIND_ACCESSIBILITY_SERVICE`):**
  - **Purpose:** Required to detect the active foreground package and view hierarchy nodes (view IDs / class names / text markers of Reels and Shorts players) in real time and perform `GLOBAL_ACTION_BACK`, `GLOBAL_ACTION_HOME`, or `GLOBAL_ACTION_LOCK_SCREEN`.
  - **Privacy Disclosure:** All detection happens 100% locally on the device; no personal messages, passwords, or unrelated screen contents are collected or transmitted off-device.
- **Display Over Other Apps (`android.permission.SYSTEM_ALERT_WINDOW`):**
  - **Purpose:** Used for the optional `Scroll Watchtime` floating overlay and blocking/mindful-wait overlays.
- **Usage Access (`android.permission.PACKAGE_USAGE_STATS`) / Query All Packages (`android.permission.QUERY_ALL_PACKAGES`):**
  - **Purpose:** Used to detect installed social/browser apps (`App not installed!` vs installed) and track app usage/scroll sessions.
- **Foreground Service & Notifications (`android.permission.FOREGROUND_SERVICE`, `android.permission.POST_NOTIFICATIONS`):**
  - **Purpose:** Keeps the blocker shield status persistent and displays remaining pause countdowns.
