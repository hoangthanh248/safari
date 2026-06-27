# ICON.md — Tạo Launcher Icon cho SafariBrowser

App **bắt buộc** phải có launcher icon trước khi build. Nếu thiếu, Gradle sẽ báo lỗi:

```
error: <path>/mipmap-mdpi/ic_launcher.png: error: file not found.
```

---

## Kích thước icon cần tạo

Android yêu cầu icon ở nhiều mật độ màn hình. Tạo **tất cả** các file sau:

```
app/src/main/res/
├── mipmap-mdpi/
│   ├── ic_launcher.png          48 × 48 px
│   └── ic_launcher_round.png    48 × 48 px
├── mipmap-hdpi/
│   ├── ic_launcher.png          72 × 72 px
│   └── ic_launcher_round.png    72 × 72 px
├── mipmap-xhdpi/
│   ├── ic_launcher.png          96 × 96 px
│   └── ic_launcher_round.png    96 × 96 px
├── mipmap-xxhdpi/
│   ├── ic_launcher.png         144 × 144 px
│   └── ic_launcher_round.png   144 × 144 px
└── mipmap-xxxhdpi/
    ├── ic_launcher.png         192 × 192 px
    └── ic_launcher_round.png   192 × 192 px
```

Ngoài ra, nếu dùng Adaptive Icon (API 26+), thêm:

```
app/src/main/res/
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml          Adaptive icon XML
│   └── ic_launcher_round.xml    Adaptive icon XML (round)
└── drawable/
    ├── ic_launcher_background.xml   Background layer
    └── ic_launcher_foreground.xml   Foreground layer
```

---

## Cách 1 — Android Studio Image Asset (khuyến nghị)

Đây là cách nhanh nhất, tự động tạo đủ tất cả kích thước.

### Bước 1: Chuẩn bị file nguồn

Cần một trong các định dạng:
- **PNG** 1024×1024 px trở lên (nền trong suốt)
- **SVG** vector
- **PSD** Photoshop

### Bước 2: Mở Image Asset Studio

Trong Android Studio:

```
File → New → Image Asset
```

Hoặc chuột phải vào thư mục `res/`:

```
New → Image Asset
```

### Bước 3: Cấu hình

**Tab "Foreground Layer":**
- **Asset Type**: Image
- **Path**: Chọn file PNG/SVG của bạn
- **Trim**: Yes (bỏ viền trắng thừa)
- **Resize**: Điều chỉnh để icon vừa vùng safe zone

**Tab "Background Layer":**
- **Asset Type**: Color
- Chọn màu nền (ví dụ: `#FFFFFF` trắng, hoặc `#007AFF` xanh iOS)

**Tab "Legacy":**
- **Generate**: Yes
- **Shape**: Square + Round (tạo cả hai)
- **Color**: Chọn màu nền cho icon legacy (Android < 8.0)

### Bước 4: Nhấn Next → Finish

Android Studio tự tạo toàn bộ file vào đúng thư mục `mipmap-*`.

---

## Cách 2 — Script tự động (không cần Android Studio)

Script này dùng **ImageMagick** để tạo placeholder icon từ màu sắc. Chạy từ root project.

### Cài ImageMagick

```bash
# macOS
brew install imagemagick

# Ubuntu / Debian
sudo apt-get install imagemagick

# Windows — tải tại https://imagemagick.org/script/download.php
```

### Script tạo icon Safari-style

```bash
#!/bin/bash
# run from SafariBrowser/ root
set -e

# Safari-style compass icon: white compass on blue gradient
BG_COLOR="#007AFF"
FG_COLOR="#FFFFFF"

declare -A SIZES=(
  [mipmap-mdpi]=48
  [mipmap-hdpi]=72
  [mipmap-xhdpi]=96
  [mipmap-xxhdpi]=144
  [mipmap-xxxhdpi]=192
)

for DIR in "${!SIZES[@]}"; do
  SIZE=${SIZES[$DIR]}
  DEST="app/src/main/res/$DIR"
  mkdir -p "$DEST"

  # Square icon — rounded rect background + compass letter
  magick -size "${SIZE}x${SIZE}" \
    "radial-gradient:${BG_COLOR}-#005AC2" \
    -fill "$FG_COLOR" \
    -gravity Center \
    -pointsize $((SIZE / 2)) \
    -font "Helvetica-Bold" \
    -annotate 0 "S" \
    -set colorspace sRGB \
    "${DEST}/ic_launcher.png"

  # Round icon — circle mask
  magick -size "${SIZE}x${SIZE}" \
    "radial-gradient:${BG_COLOR}-#005AC2" \
    -fill "$FG_COLOR" \
    -gravity Center \
    -pointsize $((SIZE / 2)) \
    -font "Helvetica-Bold" \
    -annotate 0 "S" \
    \( +clone -threshold 100% -fill white \
       -draw "circle $((SIZE/2)),$((SIZE/2)) $((SIZE/2)),0" \) \
    -alpha off \
    -compose CopyOpacity \
    -composite \
    -set colorspace sRGB \
    "${DEST}/ic_launcher_round.png"

  echo "✅ Created ${SIZE}×${SIZE} in $DEST/"
done

echo ""
echo "All icons generated. Run ./gradlew assembleDebug to verify."
```

Lưu thành `scripts/generate_icons.sh`, rồi:

```bash
chmod +x scripts/generate_icons.sh
./scripts/generate_icons.sh
```

---

## Cách 3 — Công cụ online

Tải lên 1 ảnh PNG 1024×1024, công cụ tự export tất cả kích thước:

| Công cụ | URL |
|---------|-----|
| Android Asset Studio | https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html |
| Appicon.co | https://www.appicon.co |
| MakeAppIcon | https://makeappicon.com |
| Icon Kitchen (by Google) | https://icon.kitchen |

Sau khi tải về, giải nén và copy vào:

```
app/src/main/res/
```

---

## Adaptive Icon XML (API 26+)

Tạo thư mục và các file sau để icon đẹp trên Android 8+:

### `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

### `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

### `app/src/main/res/drawable/ic_launcher_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:type="radial"
        android:gradientRadius="100%"
        android:startColor="#007AFF"
        android:endColor="#005AC2" />
</shape>
```

### `app/src/main/res/drawable/ic_launcher_foreground.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- Safari-style compass icon, safe zone 66×66dp inside 108×108dp canvas -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">

    <!-- Outer circle -->
    <path
        android:pathData="M54,18 A36,36 0 1,1 53.99,18 Z"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="3"
        android:fillColor="#00000000" />

    <!-- Compass needle NE (red tip) -->
    <path
        android:pathData="M54,54 L66,30 L58,54 Z"
        android:fillColor="#FFFFFF" />

    <!-- Compass needle SW (white tip) -->
    <path
        android:pathData="M54,54 L42,78 L50,54 Z"
        android:fillColor="#FFFFFF"
        android:fillAlpha="0.6" />

    <!-- Center dot -->
    <path
        android:pathData="M54,54 m-3,0 a3,3 0 1,0 6,0 a3,3 0 1,0 -6,0"
        android:fillColor="#FFFFFF" />

</vector>
```

---

## Kiểm tra icon đã đúng chưa

```bash
# Phải thấy đủ 10 file PNG (5 thư mục × 2 file)
find app/src/main/res/mipmap-* -name "*.png" | sort

# Build thử — nếu không có lỗi icon là OK
./gradlew assembleDebug 2>&1 | grep -i "icon\|mipmap\|drawable" || echo "No icon errors"
```

---

## Lỗi thường gặp

### `error: file not found: mipmap-xxxhdpi/ic_launcher.png`
→ Thiếu file icon. Chạy script hoặc dùng Image Asset Studio.

### `AAPT: error: failed to find drawable`
→ File XML trong `drawable/` có syntax lỗi. Kiểm tra lại encoding UTF-8.

### Icon hiển thị trắng / bị crop
→ Foreground quá lớn, vượt safe zone. Trong Image Asset Studio, resize về 60–66%.

### Icon vẫn là Android cũ (robot xanh) sau khi deploy
→ Xóa app cũ trên thiết bị rồi install lại — launcher cache icon từ lần install đầu.
