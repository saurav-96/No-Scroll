# FocusGuard (NoScroll Recreation)

A high-performance, privacy-focused Android application recreated independently from **NoScroll: Block Reels & Shorts** (`com.newswarajya.noswipe.reelshortblocker`).

Built from the ground up in modern **Kotlin** + **Jetpack Compose** (Material 3), using clean architectural patterns (Hilt DI, Room DB, Preferences DataStore, Android AccessibilityService, and DeviceAdminReceiver).

---

## 📱 Features

- **Reels & Shorts Blocker**: Automatically detects and exits short-form video players without impacting regular long-form videos:
  - YouTube Shorts (`:id/reel_player_underlay`)
  - Instagram Reels (`:id/clips_video_container`, `:id/clips_viewer_view_pager`)
  - Instagram Direct Message Exception: Friends' Reels shared in DMs are preserved via `:id/reply_bar_edittext`
  - Snapchat Spotlights & Stories
  - WhatsApp Status
  - X (Twitter) Video Tab
  - LinkedIn Watch
  - Reddit Watch
  - YouTube Music Samples
  - TikTok & Facebook Reels
  - Supported Browsers (Chrome, Brave, Firefox, Edge, Opera, Samsung Internet)
- **3 Focus Plans**:
  - **Block All**: All short-form video feeds are strictly blocked immediately.
  - **Curious**: Mindful bursts (default 5 minutes) followed by a mandatory cooldown break.
  - **One Reel**: Allows viewing the exact reel you tapped, but instantly closes the player if you swipe vertically to the next reel.
- **Per-Platform Custom Timers**: Customize restrictions per platform (`Default: Active Plan`, `Always Block`, `Allow: 01 Min`, `Allow: 05 Min`, `Allow: 10 Min`, `Don't Block`).
- **Temporary Pause**: Temporarily pause blocker with a countdown timer (1m, 5m, 10m, 15m, 30m).
- **Blocker Shield**:
  - Daily Limit 240° circular gauge tracking today's scroll time against your set limit.
  - Whole-App Blocker & PIN-locked apps.
  - Website & Keyword URL Blocker in mobile browsers.
- **Scroll Watchtime Floating Overlay**: Live emerald pill overlay (`00:00`) showing today's accumulated scroll time.
- **Dynamic PIN Protection**:
  - **Custom PIN**: Fixed 4-digit code.
  - **Live Time PIN**: Changes dynamically based on current phone clock (`HHmm`, `hhmm`, `mmHH`).
  - **Date PIN**: Changes based on current date (`ddMM`, `MMdd`).
  - **Device Default**.
- **Uninstall Protection**: Registers as a `DeviceAdminReceiver` (`<force-lock />` policy) so the app cannot be uninstalled impulsively without entering settings.
- **Haptic Vibration Feedback**: Haptic feedback on blocked events.
- **Local Analytics**: On-device Room database logging sessions, blocked counts, and platform breakdown. Zero analytics or user data ever leave your phone.

---

## 🚀 How to Install and Use on Android Device

### Prerequisites
- Android device running **Android 8.0 (API 26) or higher** (tested on Android 13/14).
- Developer Options & USB Debugging enabled (if installing via PC / ADB), OR simply copy the APK to your phone.

---

### Method 1: Instant Install via ADB (Recommended)

Connect your Android phone to your PC with a USB cable (ensure USB Debugging is ON), then run:

```powershell
# From the project root folder:
adb install -r release/FocusGuard-debug.apk
# OR for the minified, signed release version:
adb install -r release/FocusGuard-release.apk
```

---

### Method 2: Direct APK Install on Your Phone

1. Transfer `release/FocusGuard-debug.apk` or `release/FocusGuard-release.apk` to your phone via Google Drive, WhatsApp, Telegram, or USB file transfer.
2. Open your phone's **Files / File Manager** app and tap the APK file.
3. If prompted, allow **"Install unknown apps"** for your File Manager.
4. Tap **Install** and open **FocusGuard** (labeled as **NoScroll** on launcher).

---

## ⚙️ Initial Setup & Permissions (One-Time)

To enable automatic short-video blocking and the watchtime overlay, activate the permissions inside the app:

1. **Accessibility Service (Required for Reel Detection & Blocking)**:
   - When you open the app, tap the **Block All** hero icon or open **Settings**.
   - Tap **Enable Accessibility Service**.
   - Under **Downloaded apps / Installed services**, find **NoScroll Reels & Shorts Blocker** (or FocusGuard) and turn it **ON**.
   - Tap **Allow** when the system dialog asks for accessibility permission.
2. **Display Over Other Apps (Required for Scroll Watchtime Pill)**:
   - In the **Home** tab, toggle **Scroll Watchtime** ON.
   - Grant **"Display over other apps"** in your phone settings.
3. **Uninstall Protection (Optional)**:
   - Go to the **Settings** tab (gear icon at bottom right).
   - Under **Blocker Config**, toggle **Uninstall Protection** ON.
   - Tap **Activate this device admin app**.

---

## 🛠️ How to Build from Source

### Requirements
- JDK 17 or JDK 21 (Eclipse Temurin recommended)
- Android SDK (API 34 or 35)

```powershell
# Run Unit Tests:
.\gradlew.bat testDebugUnitTest

# Build Debug APK:
.\gradlew.bat assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Build Signed Release APK:
.\gradlew.bat assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

---

## 📁 Repository Structure

```
├── app/
│   ├── src/main/java/com/saurav/focusguard/
│   │   ├── core/               # Hilt Dependency Injection module
│   │   ├── data/               # Room Database (AnalyticsDao) & Preferences DataStore
│   │   ├── detection/          # PlatformDetectorRegistry (view IDs & URL rules)
│   │   ├── domain/             # BlockingDecisionEngine, PinValidator, Models
│   │   ├── navigation/         # Jetpack Compose Navigation & Bottom Navigation Pill Bar
│   │   ├── permissions/        # DeviceAdminReceiver & PermissionHelper
│   │   ├── services/           # FocusGuardAccessibilityService, AppMonitorForegroundService
│   │   └── ui/                 # HomeScreen, BlockerShieldScreen, SettingsScreen, AnalyticsScreen
│   └── src/test/               # Comprehensive Unit Tests (BlockingDecisionEngineTest, PinValidatorTest)
├── release/                    # Built APKs & Keystore
├── research/                   # REA investigation report, feature matrix, UI comparison report
├── setup/                      # Environment audit, installation report, REA config
└── testing/                    # Test plan, on-device test results, UI screenshots
```
