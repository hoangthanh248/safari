package com.example.safari.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── iOS Color System ─────────────────────────────────────────────────────────

object IOSColors {
    // Backgrounds
    val systemBackground = Color(0xFFF2F2F7)
    val secondaryBackground = Color(0xFFFFFFFF)
    val tertiaryBackground = Color(0xFFEFEFF4)

    // Glass variants
    val glassWhite = Color(0xCCFFFFFF)         // 80% white
    val glassWhiteLight = Color(0xB3FFFFFF)    // 70% white
    val glassDark = Color(0xCC1C1C1E)          // 80% dark
    val glassDarkMedium = Color(0xFF2C2C2E)    // solid dark
    val glassStroke = Color(0x33FFFFFF)        // glass border

    // Private mode
    val privateBackground = Color(0xFF1C1C1E)
    val privateCard = Color(0xFF2C2C2E)
    val privateSecondary = Color(0xFF3A3A3C)

    // iOS Blue
    val iosBlue = Color(0xFF007AFF)
    val iosBlueDark = Color(0xFF0A84FF)
    val iosBlueLight = Color(0xFF5AC8FA)

    // Text
    val label = Color(0xFF000000)
    val secondaryLabel = Color(0xFF8E8E93)
    val tertiaryLabel = Color(0xFFC7C7CC)
    val placeholderText = Color(0xFFC7C7CC)

    // Text on dark
    val labelDark = Color(0xFFFFFFFF)
    val secondaryLabelDark = Color(0xFF8E8E93)

    // Separators
    val separator = Color(0xFFC6C6C8)
    val opaqueSeparator = Color(0xFFD1D1D6)

    // System fills
    val systemFill = Color(0x3378788C)
    val secondaryFill = Color(0x2878788C)
    val tertiaryFill = Color(0x1E767680)

    // Status colors
    val systemGreen = Color(0xFF34C759)
    val systemRed = Color(0xFFFF3B30)
    val systemOrange = Color(0xFFFF9500)
}

// ── iOS Typography ────────────────────────────────────────────────────────────

object IOSTypography {
    val largeTitle = TextStyle(
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.37.sp,
        lineHeight = 41.sp
    )

    val title1 = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.34.sp,
        lineHeight = 34.sp
    )

    val title2 = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.35.sp,
        lineHeight = 28.sp
    )

    val title3 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.38.sp,
        lineHeight = 25.sp
    )

    val headline = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.41).sp,
        lineHeight = 22.sp
    )

    val body = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.41).sp,
        lineHeight = 22.sp
    )

    val callout = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.32).sp,
        lineHeight = 21.sp
    )

    val subheadline = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.24).sp,
        lineHeight = 20.sp
    )

    val footnote = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.08).sp,
        lineHeight = 18.sp
    )

    val caption1 = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        lineHeight = 16.sp
    )

    val caption2 = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.06.sp,
        lineHeight = 13.sp
    )
}

// ── iOS Shape Tokens ──────────────────────────────────────────────────────────

object IOSShapes {
    val capsuleRadius: Dp = 100.dp
    val largeRounded: Dp = 20.dp
    val mediumRounded: Dp = 14.dp
    val smallRounded: Dp = 10.dp
    val iconRounded: Dp = 16.dp
    val cardRounded: Dp = 24.dp
}

// ── iOS Spacing ───────────────────────────────────────────────────────────────

object IOSSpacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 16.dp
    val lg: Dp = 20.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
    val toolbarHeight: Dp = 56.dp
    val safeArea: Dp = 44.dp
}
