# SETUP.md — Hướng dẫn chuẩn bị & Build SafariBrowser

## Yêu cầu hệ thống

| Thứ | Phiên bản tối thiểu |
|-----|---------------------|
| OS | macOS 12+ / Windows 11 / Ubuntu 22.04 |
| RAM | 8 GB (khuyến nghị 16 GB) |
| Disk | 15 GB trống (SDK + Gradle cache) |
| Android Studio | Hedgehog 2023.1.1+ hoặc Iguana 2023.2.1+ |
| JDK | 17 (đi kèm Android Studio) |
| Android SDK | API 35 (compileSdk) |
| minSdk thiết bị test | API 31 (Android 12) — bắt buộc cho AGSL shaders |

---

## 1. Cài Android Studio

Tải tại https://developer.android.com/studio

Trong lúc cài, chọn:
- Android SDK (API 35)
- Android SDK Build-Tools 35.0.0
- Android Emulator
- Intel HAXM (Windows/macOS Intel) hoặc để trống (Apple Silicon/Linux KVM)

---

## 2. Clone repository

```bash
git clone https://github.com/<your-username>/SafariBrowser.git
cd SafariBrowser
```

---

## 3. Chuẩn bị Gradle Wrapper

Project cần file `gradlew`. Tạo bằng một trong hai cách:

### Cách A — Dùng Android Studio (khuyến nghị)
Mở project trong Android Studio → nó tự generate `gradlew`.

### Cách B — Dùng Gradle CLI
```bash
# Cài gradle nếu chưa có (macOS)
brew install gradle

# Generate wrapper
gradle wrapper --gradle-version=8.7 --distribution-type=bin

# Cấp quyền thực thi
chmod +x gradlew
```

Kiểm tra: `ls -la gradlew` — phải thấy `-rwxr-xr-x`.

---

## 4. SDK & Build Tools

Trong Android Studio: **Tools → SDK Manager**

Cài các mục sau trong **SDK Platforms**:
- ✅ Android 15.0 (API 35)
- ✅ Android 12.0 (API 31) — để test minSdk

Trong **SDK Tools**:
- ✅ Android SDK Build-Tools 35.0.0
- ✅ Android SDK Platform-Tools
- ✅ Android Emulator
- ✅ NDK (Side by side) — không bắt buộc nhưng nên có

---

## 5. Liquid Glass — Yêu cầu thiết bị

`io.github.kyant0:backdrop` dùng **AGSL (Android Graphics Shading Language)** thông qua `RuntimeShader` và `RenderEffect`.

| API Level | Hỗ trợ |
|-----------|---------|
| API 33+ (Android 13+) | ✅ Full — vibrancy, lens, blur đầy đủ |
| API 31–32 (Android 12) | ✅ Partial — blur + colorControls, không có vibrancy/lens |
| API < 31 | ❌ Không hỗ trợ (minSdk trong project là 31) |

**Khuyến nghị test:** Pixel 6+ (API 33+) hoặc emulator API 33.

---

## 6. Build Debug (không cần keystore)

```bash
# Sync dependencies
./gradlew dependencies

# Build APK debug
./gradlew assembleDebug

# APK output
ls app/build/outputs/apk/debug/
# → app-debug.apk
```

### Install lên thiết bị/emulator
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 7. Build Release (cần keystore)

### 7.1 Tạo Keystore

```bash
keytool -genkey -v \
  -keystore safari-release.jks \
  -alias safari-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD \
  -dname "CN=SafariBrowser, OU=Dev, O=Example, L=City, S=State, C=VN"
```

Giữ file `safari-release.jks` ở **nơi an toàn, không commit lên git**.

### 7.2 Build Release local

```bash
export KEYSTORE_PATH=/absolute/path/to/safari-release.jks
export KEYSTORE_PASSWORD=YOUR_STORE_PASSWORD
export KEY_ALIAS=safari-key
export KEY_PASSWORD=YOUR_KEY_PASSWORD

./gradlew assembleRelease

# APK đã signed
ls app/build/outputs/apk/release/
```

### 7.3 Verify chữ ký

```bash
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
```

---

## 8. GitHub Actions CI/CD

File `.github/workflows/build.yml` đã được cấu hình sẵn với 3 jobs:
- `build-debug` — chạy mỗi push, tạo debug APK artifact
- `build-release` — chạy khi push vào main/master hoặc tag `v*`
- `lint` — chạy song song, báo cáo lint

### 8.1 Cấu hình Secrets để sign release

Vào **GitHub repo → Settings → Secrets and variables → Actions → New repository secret**

Thêm 4 secrets sau:

| Secret name | Giá trị |
|-------------|---------|
| `KEYSTORE_BASE64` | Base64 của file .jks (xem bên dưới) |
| `KEYSTORE_PASSWORD` | Store password |
| `KEY_ALIAS` | Tên alias (ví dụ: `safari-key`) |
| `KEY_PASSWORD` | Key password |

**Encode keystore sang Base64:**
```bash
# macOS / Linux
base64 -i safari-release.jks | pbcopy   # macOS → copy vào clipboard
base64 -w 0 safari-release.jks           # Linux → in ra stdout

# Windows PowerShell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("safari-release.jks"))
```

Dán output vào secret `KEYSTORE_BASE64`.

### 8.2 Tạo Release tự động

```bash
# Tạo tag → trigger build-release + tạo GitHub Release
git tag v1.0.0
git push origin v1.0.0
```

Release sẽ tự động:
1. Build signed APK
2. Tạo GitHub Release với release notes từ commit history
3. Đính kèm APK vào release

---

## 9. Cấu trúc thư mục sau khi setup

```
SafariBrowser/
├── .github/
│   └── workflows/
│       └── build.yml              ← CI/CD pipeline
├── app/
│   ├── build.gradle.kts           ← App dependencies + signing config
│   ├── proguard-rules.pro         ← R8 rules cho backdrop + Compose
│   └── src/main/java/com/example/safari/
│       ├── MainActivity.kt
│       ├── browser/
│       │   ├── BrowserViewModel.kt
│       │   └── SafariWebView.kt
│       ├── model/BrowserModels.kt
│       ├── navigation/NavGraph.kt
│       └── ui/
│           ├── components/
│           │   ├── CupertinoIcons.kt
│           │   ├── glass/
│           │   │   └── LiquidGlass.kt     ← Real backdrop API
│           │   └── toolbar/
│           │       ├── SafariToolbar.kt   ← Fallback toolbar
│           │       └── GlassToolbar.kt    ← Backdrop glass toolbar ✨
│           ├── screens/
│           │   ├── BrowserRootScreen.kt  ← LiquidGlassRoot wrapper ✨
│           │   ├── BrowserScreen.kt
│           │   ├── StartPageScreen.kt
│           │   ├── TabSwitcherScreen.kt
│           │   ├── PrivateModeScreen.kt
│           │   ├── SearchScreen.kt
│           │   └── BookmarksAndCustomize.kt
│           └── theme/IOSDesignSystem.kt
├── gradle/
│   ├── libs.versions.toml          ← backdrop = "1.0.6" được thêm vào đây
│   └── wrapper/
│       └── gradle-wrapper.properties
├── gradlew                         ← Phải có file này!
├── gradlew.bat
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
└── SETUP.md                        ← File này
```

---

## 10. Troubleshooting

### ❌ `./gradlew: Permission denied`
```bash
chmod +x gradlew
```

### ❌ `SDK location not found`
Tạo file `local.properties` ở root:
```properties
# macOS
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk

# Linux
sdk.dir=/home/YOUR_USERNAME/Android/Sdk

# Windows
sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk
```

### ❌ `Unresolved reference: backdrop` hoặc `com.kyant.backdrop`
- Kiểm tra internet khi sync (backdrop cần download từ Maven Central)
- Thử: `./gradlew --refresh-dependencies assembleDebug`
- Kiểm tra `libs.versions.toml` có dòng `kyant0-backdrop`

### ❌ Liquid Glass không hiển thị trên emulator
- Emulator phải là API 31+, bật hardware acceleration
- Trong AVD Manager: chọn **Hardware - GLES 2.0** hoặc cao hơn
- Nếu dùng API < 33: vibrancy/lens sẽ degrade gracefully (chỉ blur)

### ❌ `KEYSTORE_PATH` null trong CI
- Kiểm tra secret `KEYSTORE_BASE64` đã được set chưa
- Build release không có keystore → APK sẽ là unsigned (vẫn build được)

### ❌ OOM khi build trên CI
Thêm vào `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m
```

---

## 11. Kiểm tra nhanh sau khi install

1. Mở app → thấy Start Page với glass card
2. Nhấn search bar → glass blur trên toolbar
3. Load Google.com → BrowsingToolbar với glass address pill
4. Nhấn tab count → Tab Switcher với glass cards
5. Long press → Context menu với glass background
6. Kiểm tra Private mode: background tối, glass màu dark

---

## 12. Phiên bản dependencies chính

```toml
agp             = "8.5.0"
kotlin          = "2.0.0"
composeBom      = "2024.06.00"
backdrop        = "1.0.6"   # io.github.kyant0:backdrop
navigationCompose = "2.7.7"
coil            = "2.6.0"
room            = "2.6.1"
```

Kiểm tra phiên bản mới nhất của backdrop tại:
https://central.sonatype.com/artifact/io.github.kyant0/backdrop
