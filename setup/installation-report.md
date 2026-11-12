# Missing Software Installation Report — Phase One

**Date:** 2026-11-12  
**Workspace:** `C:\Users\Administrator\Desktop\No Scroll`  
**Operating System:** Windows 11 Pro 64-bit (`10.0.26200`)

---

## 1. Preserved Existing Software (Not Reinstalled)

The following existing working installations passed the Phase Zero audit and were preserved without modification:
- **Node.js `v26.13.0` & npm `11.20.0`** (`C:\Program Files\nodejs\`) — Exceeds REA's `>=26.12.1` requirement.
- **Git `2.56.1.windows.1`** (`C:\Program Files\Git\cmd\git.exe`).
- **Python `3.13.7` (64-bit)** (`C:\Users\Administrator\AppData\Local\Programs\Python\Python313\python.exe`).
- **Android Studio `2026.2.2.9`** (`C:\Program Files\Android\Android Studio`).

---

## 2. Newly Installed Dependencies & Configuration

### A. Eclipse Adoptium Temurin JDK 21 LTS
1. **Why Needed:** Required by REA's headless JADX metadata bridge (`jdk.compiler` module required for runtime source compilation) and by the Android Gradle Plugin (AGP 8.x) + Kotlin compiler to build `FocusGuard`.
2. **Official Source:** Eclipse Adoptium (`https://adoptium.net/` / `winget` package `EclipseAdoptium.Temurin.21.JDK` `21.0.13.9` from `https://github.com/adoptium/temurin21-binaries`).
3. **Installation Path:** `C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot`
4. **Environment Configuration:**
   - `JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot`
   - Added `%JAVA_HOME%\bin` to User `PATH`.
5. **Verification Result:**
   - `java --version`: `openjdk 21.0.13 2026-10-20 LTS (Temurin-21.0.13+9)`
   - `javac --version`: `javac 21.0.13`

---

### B. JADX (`skylot/jadx`) & REA Pinned Headless JADX MCP Bridge (`0.7.1`)
1. **Why Needed:**
   - REA static Android APK analysis (`inspect-android-package`, `search-android-classes`, `inspect-android-class`, `inspect-android-method`, `trace-android-references`, `inspect-android-jni-bindings`) requires the commit-pinned `jadx-headless-mcp-0.7.1-all.jar`.
   - Standalone `jadx` (`1.5.6`) provides full CLI/GUI source decompilation when needed.
2. **Official Sources:**
   - Headless JADX MCP JAR: `https://github.com/1013503897/jadx-headless-mcp/releases/download/v0.7.1/jadx-headless-mcp-0.7.1-all.jar` (referenced in `https://github.com/morluto/rea/blob/main/docs/guides/android/android-analysis.md`).
   - Standalone JADX: `https://github.com/skylot/jadx/releases/download/v1.5.6/jadx-1.5.6.zip`.
3. **Installation Locations:**
   - `C:\Tools\rea\android\jadx-headless-mcp-0.7.1-all.jar` (and `C:\Users\Administrator\.local\share\rea\android\jadx-headless-mcp-0.7.1-all.jar`)
   - `C:\Tools\jadx\bin\jadx.bat`
4. **Environment Configuration:**
   - `REA_JADX_MCP_JAR = C:\Tools\rea\android\jadx-headless-mcp-0.7.1-all.jar`
   - Added `C:\Tools\jadx\bin` to User `PATH`.
5. **Verification Result:**
   - SHA-256 verified: `6e5eacf500b64292bfb73c49797c1958f6ee44646e43e868039ae7feb573ff75` (exact match).
   - `jadx --version`: `1.5.6`
   - `rea doctor --provider jadx --json`: `healthy: true`

---

### C. Apktool (`iBotPeaches/Apktool`)
1. **Why Needed:** Required by REA (`inspect-apktool-client`, `decode-android-resources`) and Phase Four resource analysis (`AndroidManifest.xml`, `res/values/strings.xml`, `res/xml/accessibility_service_config.xml`, drawables, layouts, colors).
2. **Official Source:** `https://github.com/iBotPeaches/Apktool/releases/download/v3.0.3/apktool_3.0.3.jar`
3. **Installation Location:**
   - `C:\Tools\apktool\apktool.jar` (`3.0.3`, patched against `CVE-2026-39973` / `GHSA-vgwr-4w3p-xmjv`)
   - `C:\Tools\apktool\apktool.bat` (non-interactive wrapper script with zero spaces or shell metacharacters in path)
4. **Environment Configuration:**
   - `REA_APKTOOL_COMMAND = C:\Tools\apktool\apktool.bat`
   - Added `C:\Tools\apktool` to User `PATH`.
5. **Verification Result:**
   - `apktool --version`: `3.0.3`
   - `rea inspect-apktool-client --json`: `apktool_version: "3.0.3"`, `confidence: "observed"`

---

### D. Android SDK, Command-Line Tools, Platform Tools, Build Tools & Emulator
1. **Why Needed:** Required for ADB device inspection (`rea inspect-adb-client`, `rea pull-adb-package`), compiling `FocusGuard` (`platforms;android-35`, `platforms;android-34`, `build-tools;35.0.0`), and running UI/functional tests on an Android Emulator.
2. **Official Source:** `https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip` and official `sdkmanager` repository.
3. **Installation Location:** `C:\Users\Administrator\AppData\Local\Android\Sdk`
   - `cmdline-tools\latest\bin\sdkmanager.bat`
   - `platform-tools\adb.exe` (`37.0.1-15733141`)
   - `platforms\android-35` & `platforms\android-34`
   - `build-tools\35.0.0`
   - `emulator\emulator.exe`
4. **Environment Configuration:**
   - `ANDROID_HOME = C:\Users\Administrator\AppData\Local\Android\Sdk`
   - `ANDROID_SDK_ROOT = C:\Users\Administrator\AppData\Local\Android\Sdk`
   - `REA_ADB_PATH = C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe`
   - Added `%ANDROID_HOME%\platform-tools`, `%ANDROID_HOME%\cmdline-tools\latest\bin`, and `%ANDROID_HOME%\emulator` to User `PATH`.
5. **Verification Result:**
   - `rea inspect-adb-client --json`: `version: "37.0.1-15733141"`, `confidence: "observed"`

---

### E. GitHub CLI (`gh`)
1. **Why Needed:** GitHub repository management.
2. **Official Source:** `GitHub.cli` (`winget` / `https://github.com/cli/cli`).
3. **Installation Location:** `C:\Program Files\GitHub CLI\gh.exe` (`2.103.3`).
