# UI Screenshot Inventory & Visual Specification (`./reference-ui/`)

**Reference Application:** NoScroll: Block Reels & Shorts (`com.newswarajya.noswipe.reelshortblocker`)  
**Recreated Application:** FocusGuard (`com.saurav.focusguard`)  
**Total Reference Screenshots Analyzed:** 7 (`screen_01` through `screen_07`)

---

## 1. Global Design System Extracted from Screenshots

### 1.1 Color Palette (Dark Theme — Default Observed)
| Token Name | Hex Code | Usage |
| :--- | :--- | :--- |
| `BackgroundPrimary` | `#060D1F` -> `#1A0B2E` | Deep midnight navy with subtle top-right & top-left plum/magenta radial ambient glow |
| `SurfaceCardPrimary` | `#0C1C38` | Standard card container background (Doom-scrolling app cards, Blocker Config card, All Apps & Sites) |
| `SurfaceCardSecondary` | `#081428` | Inner nested sub-row container (e.g., `"YouTube Shorts"`, `"WhatsApp Status"`, `"YT Music Samples"` rows) |
| `SurfaceCardElevated` | `#122338` | Blocker Shield daily-limit gauge card with subtle teal-slate center gradient |
| `SurfacePlumSelected` | `#38102C` | Selected Focus Plan card (`Block All`), info banner (`#2A122E`), selected pill fill (`#3B122B`) |
| `PrimaryPink` | `#E91E63` / `#F50057` | Hero prohibition ring, Stats summary bar (`Sessions / Blocked / Scroll Time`), `"Default: Active Plan"` dropdown pill, active plan icon |
| `PrimaryMaroonPill` | `#8E1747` / `#9E194D` | Active Bottom Navigation tab pill, `"UPGRADE"` badge, `"ACTIVE"` badge, `"Configure"` button, `"0 Restricted"` / `"None blocked"` badges |
| `GradientPremiumStart` | `#FF1D58` / `#EC407A` | Left stop of `"UPGRADE TO PREMIUM"`, `"Upgrade"` buttons, active switches, top-right diamond button |
| `GradientPremiumEnd` | `#3D5AFE` / `#2962FF` | Right stop of `"UPGRADE TO PREMIUM"`, `"Upgrade"` buttons, active switches |
| `WarningAmber` | `#FFB300` | `"Pause"` pill button on Home screen (`SELECT PLAN` header) |
| `AccentBlueCurious` | `#3D5AFE` | `"Curious"` plan brain icon circle |
| `AccentGreenWatchtime` | `#0F6848` / `#4CAF50` | `"Scroll Watchtime"` `00:00` pill badge and Blocker Shield trend icon |
| `AccentLavender` | `#DCD6F7` | `"Collapse ^"` button in Scroll Blocklist and `"Support"` headset icon circle |
| `TextPrimary` | `#FFFFFF` | Primary titles, headings, active labels |
| `TextSecondary` | `#9EABB8` / `#B0BEC5` | Subtitles, descriptions, section headers |
| `TextMuted` | `#6C7A89` | `"• App not installed!"` status text |
| `BorderSubtle` | `#1A2E4C` | Card outlines and unselected pill borders |
| `BorderAccentPink` | `#D81B60` | Selected option pill border (`Close Player`, `System Default`, `Scroll Blocklist` card border) |

### 1.2 Typography & Spacing
- **Font Family:** Sans-serif (`Inter` / `Roboto` / `Plus Jakarta Sans` style geometric sans)
- **Top App Bar Title:** `20sp`–`24sp`, Bold (`FontWeight.Bold`), `#FFFFFF`
- **Section Headers (`SELECT PLAN`, `DOOM-SCROLLING APPS`, `BLOCKER CONFIG`, etc.):** `12sp`, SemiBold (`FontWeight.SemiBold`), uppercase, letter-spacing `0.8sp`, `#B0BEC5`, `16dp` horizontal margin, `12dp` bottom spacing
- **Card Corner Radius:** `20dp`–`24dp` for outer cards; `14dp`–`16dp` for nested sub-cards; `50%` (`CircleShape`) for status pills and action buttons
- **Screen Horizontal Padding:** `16dp`
- **Bottom Navigation Bar Height:** `72dp` content container above gesture navigation bar, with `3` items (`Home`, `Blocker`, `Settings`); active item expands into a `#8E1747` rounded pill (`height = 46dp`, `horizontalPadding = 22dp`) showing icon + label, while inactive items show icon only (`#CFD8DC`).

---

## 2. Individual Screenshot Inventory

### Screenshot 1: `screen_01_media_1794457780150_83612301.jpg`
- **Screen Name:** `SettingsScreen` (Lower Scroll Position — Blocker Config & App Settings)
- **Corresponding Compose Screen:** `ui/settings/SettingsScreen.kt`
- **UI Components:**
  1. **Sticky Top Bar:** Circular back button (`#0C213B`), `"Settings"` title, `"Restore Purchase"` (`#D81B60`) action button.
  2. **`BLOCKER CONFIG` Section:**
     - **Blocking Mode Card (`#0C1C38`):**
       - Left icon: prohibition sign `⊘` inside dark circle (`#081428`).
       - Title: `"Blocking Mode"`, Subtitle: `"Instantly exits the reel player"`.
       - 3 Segmented Choice Pills:
         - `"Close Player"` (Selected: `#3B122B` fill, `#D81B60` border, `#FF4081` text)
         - `"Close App"` (Unselected: `#081428` fill, `#1A2E4C` border, `#FFFFFF` text)
         - `"Lock Screen"` (Unselected: `#081428` fill, `#1A2E4C` border, `#FFFFFF` text)
     - **NoScroll Pin Row:**
       - Left icon: White padlock in dark circle.
       - Title: `"NoScroll Pin"`, Subtitle: `"Protect NoScroll and locked apps with a PIN"`.
       - Action: `"Configure"` button (`#8E1747` fill, rounded `12dp`, white bold text).
     - **Uninstall Protection Row:**
       - Left icon: Shield icon in dark circle.
       - Title: `"Uninstall Protection"`, Subtitle: `"Require extra confirmation before uninstalling"`.
       - Action: Toggle switch (OFF state: `#78909C` track, white thumb).
     - **Skip mindful wait Row:**
       - Left icon: Lightning bolt `⚡` in dark circle.
       - Title: `"Skip mindful wait"`, Subtitle: `"Show the ad as soon as it loads & skip mindful wait if ad is not available"`.
       - Action: Toggle switch (ON state: pink-to-purple gradient track, white thumb).
  3. **`APP SETTINGS` Section:**
     - **App Theme Card:**
       - Left icon: Artist palette icon in dark circle.
       - Title: `"App Theme"`, Subtitle: `"Select your preferred appearance"`.
       - 3 Segmented Choice Pills: `"Light"`, `"Dark"`, `"System Default"` (Selected).
     - **Privacy Policy Row (partially visible at bottom):**
       - Left icon: Shield with checkmark/home, Title: `"Privacy Policy"`, right chevron `>`.
  4. **Bottom Navigation Bar:** `"Settings"` active pill (`#8E1747`).

---

### Screenshot 2: `screen_02_media_1794457784397_0f2f1871.jpg`
- **Screen Name:** `SettingsScreen` (Upper Scroll Position — Subscription & Premium Features)
- **Corresponding Compose Screen:** `ui/settings/SettingsScreen.kt`
- **UI Components:**
  1. **Top App Bar:** Back arrow button, `"Settings"`, `"Restore Purchase"`.
  2. **`ACTIVE SUBSCRIPTION` Section:**
     - **Subscription Card (`#111936` -> `#1B1738` gradient, `24dp` radius):**
       - Top-left: Dark rounded square (`#081226`) with pink 8-point starburst checkmark icon.
       - Top-right: `"ACTIVE"` pill badge (`#8E1747`).
       - Title: `"NoScroll Basic"` (`22sp` Bold).
       - Subtitle: `"Lifetime"` (`14sp` `#B0BEC5`).
       - Full-width Gradient CTA Button (`height = 52dp`, `CircleShape`): `"★ UPGRADE TO PREMIUM"` (`#FF1D58` -> `#2962FF`).
  3. **`PREMIUM FEATURES` Section:**
     - **Haptic Feedback Row:** Vibrating phone icon, `"Haptic Feedback"`, `"Vibrate when a reel or app is blocked"`, right `"Upgrade"` gradient pill.
     - **Max Pause Duration Row:** Stopwatch icon, `"Max Pause Duration"`, `"Longest you can pause blocking at once"`, right `"Upgrade"` gradient pill.
  4. **`BLOCKER CONFIG` Header & Top of Blocking Mode Card.**
  5. **Bottom Navigation Bar:** `"Settings"` active pill.

---

### Screenshot 3: `screen_03_media_1794457787083_9a24999e.jpg`
- **Screen Name:** `BlockerScreen` (`NoScroll Blocker Shield` — Middle Navigation Tab)
- **Corresponding Compose Screen:** `ui/blocker/BlockerShieldScreen.kt`
- **UI Components:**
  1. **Top App Bar:**
     - Circular back button (`#0C213B`).
     - Title: `"NoScroll Blocker Shield"` (`20sp` Bold).
     - Subtitle: `"Your focus is currently protected. 🛡️"` (`13sp` `#9EABB8`).
  2. **Daily Limit Circular Gauge Card (`#122338`, `24dp` radius, `280dp` height):**
     - Top header: `"Daily Limit: 2h 30m ✏️"` (clickable to edit daily limit).
     - Center Horseshoe Arc Progress Indicator:
       - 240° sweep angle from 150° to 390°, `18dp` stroke width, `#081629` background track.
       - Center content: Green line-chart icon (`#4CAF50`), `"0m"` (`42sp` Bold `#FFFFFF`), `"of 2h 30m"` (`14sp` `#B0BEC5`).
     - Bottom action: `"⏸ PAUSE"` button (`14sp` Bold `#FFFFFF`, letter-spacing `1sp`).
  3. **`BLOCK LIST` Section:**
     - **Blocked Apps Card (`#0C1C38`, `20dp` radius):**
       - Pink 3x3 grid icon inside `#081428` rounded square.
       - Title: `"Blocked Apps"`, Badge: `"None blocked"` (`#8E1747` pill).
       - Right circular chevron `>` button.
     - **Blocked Websites Card (`#0C1C38`, `20dp` radius):**
       - Pink globe icon inside `#081428` rounded square.
       - Title: `"Blocked Websites"`, Badge: `"None blocked"` (`#8E1747` pill).
       - Right circular chevron `>` button.
  4. **Bottom Navigation Bar:** Middle tab `"Blocker"` active in `#8E1747` pill with padlock icon.

---

### Screenshot 4: `screen_04_media_1794457788745_1d85705f.jpg`
- **Screen Name:** `HomeScreen` (Bottom Scroll Position — Browsers, All Apps & Sites, Support)
- **Corresponding Compose Screen:** `ui/dashboard/HomeScreen.kt`
- **UI Components:**
  1. **YouTube Music Card** (bottom item of `Scroll Blocklist`):
     - Red YouTube Music icon, `"YouTube Music"`, action icons (`🔒`, `123`, `v`), inner row `"YT Music Samples"` with `"Don't Block ▾"` pill.
  2. **`BROWSERS` Section:**
     - Expandable header row: `"Supported Browsers"` (`18sp` Bold) + count badge `"1"` (`#0D2847`) + chevron `v`.
  3. **`ALL APPS & SITES` Section:**
     - Card container (`#0C1C38`, `22dp` radius) with 2 rows:
       - **All Apps:** Pink circle (`#8E1747`) with white grid icon, `"All Apps"` + `"0 Restricted"` badge (`#8E1747`), subtitle `"Lock or restrict any installed app"`, right circular chevron `>`.
       - **Block Websites:** Pink circle (`#8E1747`) with white globe icon, `"Block Websites"`, subtitle `"Manage blocked URLs & keywords"`, right circular chevron `>`.
  4. **`SUPPORT` Section:**
     - Card (`#0C1C38`, `20dp` radius): Lavender circle (`#C5CAE9`) with dark navy support headset icon, `"Support"`, subtitle `"Troubleshoot & contact us"`.
  5. **Bottom Navigation Bar:** Left tab `"Home"` active in `#8E1747` pill.

---

### Screenshot 5: `screen_05_media_1794457815207_49384a91.jpg`
- **Screen Name:** `HomeScreen` (Lower-Middle Scroll Position — Scroll Blocklist Expanded)
- **Corresponding Compose Screen:** `ui/dashboard/HomeScreen.kt`
- **UI Components:**
  1. **Scroll Blocklist App Cards (Continued):**
     - **WhatsApp (bottom half visible):** `"WhatsApp Status"` -> `"Don't Block ▾"`.
     - **X (Twitter):** Black X logo circle, `"X (Twitter)"`, `"• App not installed!"`.
     - **LinkedIn:** Blue `in` logo circle, `"LinkedIn"`, right icons (`🔒`, `123`, `v`), inner row `"LinkedIn Watch"` -> `"Upgrade"` gradient pill (`#FF1D58` -> `#3D5AFE`).
     - **Reddit:** Orange Reddit logo circle, `"Reddit"`, right icons (`🔒`, `123`, `v`), inner row `"Reddit Watch"` -> `"Default: Active Plan ▾"` (`#E91E63` pill).
     - **YouTube Music:** Red YT Music logo circle, `"YouTube Music"`, right icons (`🔒`, `123`, `v`), inner row `"YT Music Samples"` -> `"Don't Block ▾"`.
  2. **`BROWSERS` & `ALL APPS & SITES` Sections** below.

---

### Screenshot 6: `screen_06_media_1794457817285_6050ff4a.jpg`
- **Screen Name:** `HomeScreen` (Upper-Middle Scroll Position — Community Banner & Top of Scroll Blocklist)
- **Corresponding Compose Screen:** `ui/dashboard/HomeScreen.kt`
- **UI Components:**
  1. **Community Carousel Banner Card:**
     - Left vertical pink accent bar, dark circle with document icon, `"NoScroll Community"`, `"Join NoScroll Reddit community!"`, right `"Join ->"` pink pill button (`#E91E63`), 2 pagination dots below (`#E91E63` active, `#4A2348` inactive).
  2. **`DOOM-SCROLLING APPS` Section:**
     - **Scroll Blocklist Summary Card (`#101935` with `#6A1B4D` border, `20dp` radius):**
       - Title: `"Scroll Blocklist"` + circular count badge `"3"` (`#0B2239`).
       - Overlapping mini icon row: YouTube Shorts, LinkedIn, Reddit.
       - Right button: `"Collapse ^"` pill (`#DCD6F7` lavender fill, `#1A1A2E` dark text).
     - **App Cards:**
       - **Youtube:** Red Shorts logo, `"Youtube"`, right icons (`🔒`, `123`, `v`), inner row `"YouTube Shorts"` -> `"Default: Active Plan ▾"` (`#E91E63`).
       - **Instagram:** Gradient Reels logo, `"Instagram"`, `"• App not installed!"`.
       - **Snapchat:** Yellow Snapchat ghost logo, `"Snapchat"`, `"• App not installed!"`.
       - **Whatsapp:** Green WhatsApp logo, `"Whatsapp"`, right icons (`🔒`, `123`, `v`), inner row `"WhatsApp Status"` -> `"Don't Block ▾"`.
       - **X (Twitter):** Black X logo, `"X (Twitter)"`, `"• App not installed!"`.
       - **LinkedIn (top visible):** Blue `in` logo, `"LinkedIn"`.

---

### Screenshot 7: `screen_07_media_1794457820209_86016301.jpg`
- **Screen Name:** `HomeScreen` (Top Scroll Position — Hero Status, Stats Bar, Select Plan, Scroll Watchtime)
- **Corresponding Compose Screen:** `ui/dashboard/HomeScreen.kt`
- **UI Components:**
  1. **Top Brand Header:**
     - Left: `"NoScroll"` (`24sp` Bold) + `"UPGRADE"` badge (`#8E1747` pill), subtitle `"Reclaim your focus today."` (`13sp` `#B0BEC5`).
     - Right: `44dp` circular pink-to-purple gradient button with white diamond `💎` icon.
  2. **Hero Active Mode Indicator (Center):**
     - `136dp` outer dark-magenta glow ring (`#3A0E2E`), `108dp` middle ring (`#FF1744`), `88dp` inner circle (`#F50057`) with dark maroon ring border and white prohibition icon `⊘` (`36dp`).
     - Mode Title: `"Block All"` (`22sp` Bold `#FFFFFF`).
     - Mode Subtitle: `"All Shorts & Reels are blocked"` (`14sp` `#B0BEC5`).
  3. **Quick Stats Pill Bar (`height = 68dp`, `CircleShape`, `#F50057` -> `#D81B60`):**
     - 3 metrics separated by vertical `1dp` semi-transparent white dividers:
       - `⏱ 0` / `Sessions`
       - `⊘ 0` / `Blocked`
       - `📈 00:00` / `Scroll Time`
     - Right circular dark button (`38dp`, `#091526`) with white right arrow `->` (opens Analytics screen).
  4. **`SELECT PLAN` Section:**
     - Header row: `"SELECT PLAN"` on left; amber `"⏸ Pause"` pill button (`#FFB300`) on right.
     - Status description banner (`#2A122E`, `16dp` radius): Pink circle with `⊘` icon + `"All Reels & Shorts are blocked."`.
     - **3 Focus Mode Cards (`height = 116dp`, `18dp` radius, equal width):**
       - **Card 1 (`Curious`):** `#0B162C` fill, `#1A2E4C` border, blue circle (`#3D5AFE`) with brain icon, `"Curious"` label.
       - **Card 2 (`Block All` — Active):** `#38102C` fill, `#6A1B4D` border, pink circle (`#F50057`) with `⊘` icon, `"Block All"` label + pink bottom accent bar (`24dp` wide, `3dp` height).
       - **Card 3 (`One Reel`):** `#0B162C` fill, purple-blue gradient border, gray circle (`#78909C`) with hand-tap icon, small `💎` icon at top-right, `"One Reel"` label.
     - **Large Action Banner (`height = 80dp`, `#0C1C38`, `20dp` radius):**
       - Centered hand-swipe icon + `"NoScroll"` (`18sp` Bold).
  5. **`SCROLL WATCHTIME` Section (bottom of viewport):**
     - Header: `"SCROLL WATCHTIME"`.
     - Card (`#0C1C38`): Green pill (`#0F6848`) showing `"00:00"`, `"Scroll Watchtime ⓘ"`, gear icon `⚙️`, and toggle switch.
