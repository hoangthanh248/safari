# SafariBrowser — iOS 26 Safari Clone for Android

Trình duyệt Android mô phỏng Safari iOS 26, xây dựng bằng Jetpack Compose thuần túy.

---

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose (pure, no Material) |
| Architecture | MVVM + StateFlow |
| Navigation | Navigation Compose |
| Browser Engine | Android WebView |
| Images | Coil |
| Persistence | Room + DataStore |
| Async | Kotlin Coroutines + Flow |

---

## Project Structure

```
com.example.safari/
├── MainActivity.kt
├── navigation/
│   └── NavGraph.kt
├── model/
│   └── BrowserModels.kt          # Tab, BrowserState, History, Favorite, Bookmark
├── browser/
│   ├── BrowserViewModel.kt       # Core state management
│   └── SafariWebView.kt          # WebView wrapper
└── ui/
    ├── theme/
    │   └── IOSDesignSystem.kt    # IOSColors, IOSTypography, IOSShapes, IOSSpacing
    ├── components/
    │   ├── CupertinoIcons.kt     # Custom SF Symbol-style icons (Canvas-drawn)
    │   ├── glass/
    │   │   └── LiquidGlass.kt   # LiquidGlassSurface, Container, Button, Toolbar, Sheet
    │   └── toolbar/
    │       └── SafariToolbar.kt  # SafariToolbar, BrowsingToolbar, TabCountButton
    └── screens/
        ├── BrowserScreen.kt      # Main orchestrator + overlay management
        ├── StartPageScreen.kt    # Start Page, Favorites, Privacy Report
        ├── TabSwitcherScreen.kt  # Tab grid, context menu, bottom bar
        ├── PrivateModeScreen.kt  # Locked Private Browsing onboarding
        ├── SearchScreen.kt       # Fullscreen search + category icons
        └── BookmarksAndCustomize.kt  # Bookmarks, history, customize sheet
```

---

## Design System

### IOSColors
- `systemBackground` `#F2F2F7`
- `glassWhite` — 80% white với blur effect
- `glassDark` — 80% dark cho private mode
- `iosBlue` `#007AFF`
- `privateBackground` `#1C1C1E`

### IOSTypography
Không dùng Material Typography. Toàn bộ text dùng `BasicText` với `TextStyle` custom:
- largeTitle 34sp / title1 28sp / title2 22sp / headline 17sp SemiBold
- body 17sp / subheadline 15sp / caption 12sp

### IOSShapes
- `capsuleRadius` 100dp
- `cardRounded` 24dp
- `largeRounded` 20dp
- `iconRounded` 16dp

---

## Liquid Glass System

Implement không dùng RenderEffect (API 31+ only) để tối đa compatibility.
Thay vào đó dùng:

```kotlin
// Transparent background + white stroke + gradient sheen
LiquidGlassSurface(
    cornerRadius = 20.dp,
    isDark = false,       // true = private mode glass
    alpha = 0.85f
) {
    // content
}
```

Components:
- `LiquidGlassSurface` — general purpose
- `LiquidGlassContainer` — card containers
- `LiquidGlassButton` — capsule/pill buttons
- `LiquidGlassToolbar` — toolbar với separator line
- `LiquidGlassSheet` — bottom sheet
- `FrostedCard` — white/dark frosted card

---

## Cupertino Icons

Toàn bộ icon được vẽ bằng `Canvas` DrawScope — không dùng vector drawable hay Material Icons:

```kotlin
CupertinoIcon(
    icon = CupertinoIcons.Search,
    tint = IOSColors.label,
    size = 22.dp,
    strokeWidth = 2f
)
```

Icons có sẵn: `ArrowLeft`, `ArrowRight`, `Search`, `Plus`, `Ellipsis`, `Shield`, `Bookmark`, `Hand`, `XMark`, `Checkmark`, `Star`, `Globe`, `Tabs`, `Microphone`, `Lock`, `Reload`, `Share`, `List`, `Clock`, `Glasses`, `Person`, `SortUpDown`, `CheckCircle`

---

## Screens

### Start Page
- Customize card với preview illustration
- Favorites grid (4 columns, spring animation)
- Privacy Report card
- Private mode header (dark glass)

### Browser
- WebView với progress bar
- BrowsingToolbar: back + address pill + tab count + more
- Swipe left/right gesture cho back/forward

### Tab Switcher
- 2-column grid preview
- Context menu (Manage, Select, Arrange)
- Bottom bar: + | Private | N Tabs | ✓

### Private Mode
- Full dark screen
- FaceID lock illustration
- "Turn On Locked Private Browsing" button

### Bookmarks
- 3-tab segmented: Bookmarks | Reading | History
- Folder list + bookmark items

### Customize Start Page
- Toggle rows (Favorites, Privacy Report, Reading List, etc.)
- iOS-style green toggles
- Wallpaper grid

---

## Setup

### Requirements
- Android Studio Hedgehog+
- Android SDK 35
- minSdk 31 (Android 12+, for RenderEffect)
- Kotlin 2.0

### Run
```bash
git clone <repo>
cd SafariBrowser
./gradlew assembleDebug
```

### Install APK
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Gestures

| Gesture | Action |
|---|---|
| Swipe right (>80dp) | Go back |
| Swipe left (<-80dp) | Go forward |
| Swipe up on toolbar | Open tabs |
| Long press toolbar | Context menu |

---

## Không sử dụng

- ❌ MaterialTheme
- ❌ MaterialButton / MaterialCard
- ❌ Material Icons
- ❌ Scaffold
- ❌ Material Typography
- ❌ Surface (Material)

Thay bằng:
- ✅ Box / Column / Row
- ✅ Canvas + DrawScope
- ✅ BasicText + TextStyle
- ✅ Modifier.clip + Modifier.background
- ✅ Custom LiquidGlass components
