package com.example.safari.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.BasicText
import com.example.safari.model.Favorite
import com.example.safari.ui.components.*
import com.example.safari.ui.components.glass.*
import com.example.safari.ui.theme.*

// ── Start Page ────────────────────────────────────────────────────────────────

@Composable
fun StartPageScreen(
    favorites: List<Favorite>,
    isPrivateMode: Boolean,
    trackerCount: Int = 0,
    onFavoriteClick: (Favorite) -> Unit,
    onCustomize: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isPrivateMode) IOSColors.privateBackground else IOSColors.systemBackground

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = IOSSpacing.md, vertical = IOSSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(IOSSpacing.xl)
        ) {
            // Private mode header card
            if (isPrivateMode) {
                PrivateModeHeaderCard()
            } else {
                // Start Page customize card
                StartPageCard(onCustomize = onCustomize)
            }

            // Favorites section
            FavoritesSection(
                favorites = favorites,
                isPrivateMode = isPrivateMode,
                onFavoriteClick = onFavoriteClick
            )

            // Privacy Report
            PrivacyReportCard(
                trackerCount = trackerCount,
                isPrivateMode = isPrivateMode
            )

            // Bottom edit button
            EditButton(isPrivateMode = isPrivateMode)

            Spacer(Modifier.height(100.dp))
        }
    }
}

// ── Start Page Customize Card ─────────────────────────────────────────────────

@Composable
private fun StartPageCard(onCustomize: () -> Unit) {
    FrostedCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = IOSShapes.cardRounded
    ) {
        Column(
            modifier = Modifier.padding(IOSSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(IOSSpacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = "Start Page",
                    style = IOSTypography.headline.copy(color = IOSColors.label)
                )
                // X button
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD1D1D6)),
                    contentAlignment = Alignment.Center
                ) {
                    CupertinoIcon(
                        CupertinoIcons.XMark,
                        tint = IOSColors.secondaryLabel,
                        size = 12.dp,
                        strokeWidth = 2f
                    )
                }
            }

            // Preview illustration
            StartPagePreviewIllustration()

            BasicText(
                text = "Customize your wallpaper and sections that appear when creating new tabs.",
                style = IOSTypography.subheadline.copy(
                    color = IOSColors.secondaryLabel,
                    textAlign = TextAlign.Center
                )
            )

            // Customize button
            CustomizeButton(onClick = onCustomize)
        }
    }
}

// ── Start Page Preview Illustration ──────────────────────────────────────────

@Composable
private fun StartPagePreviewIllustration() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(14.dp))
    ) {
        // Colorful wallpaper background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFFE8A87C), // warm orange
                            Color(0xFFD4A5A5), // pink
                            Color(0xFF85A98F)  // green
                        )
                    )
                )
        )

        // Center iOS-style phone preview
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(90.dp)
                .fillMaxHeight()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xBBB8C4D8))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BasicText(
                    "Favorites",
                    style = IOSTypography.caption2.copy(color = Color.White)
                )
                // Grid dots
                repeat(2) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(4) {
                            Box(
                                Modifier
                                    .size(14.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.White.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                BasicText(
                    "Suggestions",
                    style = IOSTypography.caption2.copy(color = Color.White)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) {
                        Box(
                            Modifier
                                .height(18.dp)
                                .width(20.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(IOSColors.iosBlue.copy(alpha = 0.6f))
                        )
                    }
                }
            }
        }
    }
}

// ── Customize Button ──────────────────────────────────────────────────────────

@Composable
private fun CustomizeButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "btn_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(IOSColors.iosBlue)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = "Customize Start Page",
            style = IOSTypography.headline.copy(color = Color.White)
        )
    }
}

// ── Private Mode Header ───────────────────────────────────────────────────────

@Composable
private fun PrivateModeHeaderCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(IOSShapes.cardRounded))
            .background(Color(0xFF2C2C2E))
            .padding(IOSSpacing.lg)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(IOSSpacing.sm)
        ) {
            BasicText(
                text = "Private Browsing",
                style = IOSTypography.title2.copy(color = Color.White)
            )
            BasicText(
                text = "Safari is designed with privacy in mind by preventing tracking by default. Private Browsing adds additional privacy protections for all your private tabs. After you close a tab, Safari won't remember the pages you visited, your search history, or your AutoFill information.",
                style = IOSTypography.subheadline.copy(
                    color = IOSColors.secondaryLabelDark,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

// ── Favorites Section ─────────────────────────────────────────────────────────

@Composable
private fun FavoritesSection(
    favorites: List<Favorite>,
    isPrivateMode: Boolean,
    onFavoriteClick: (Favorite) -> Unit
) {
    val labelColor = if (isPrivateMode) IOSColors.labelDark else IOSColors.label

    Column(verticalArrangement = Arrangement.spacedBy(IOSSpacing.md)) {
        // Section title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CupertinoIcon(
                CupertinoIcons.Person,
                tint = labelColor,
                size = 20.dp,
                strokeWidth = 2f
            )
            BasicText(
                text = "Favorites",
                style = IOSTypography.title2.copy(color = labelColor)
            )
        }

        // Grid of favorites
        val columns = 4
        val rows = (favorites.size + columns - 1) / columns
        Column(verticalArrangement = Arrangement.spacedBy(IOSSpacing.md)) {
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(IOSSpacing.sm)
                ) {
                    for (col in 0 until columns) {
                        val idx = row * columns + col
                        if (idx < favorites.size) {
                            FavoriteCard(
                                favorite = favorites[idx],
                                isPrivateMode = isPrivateMode,
                                modifier = Modifier.weight(1f),
                                onClick = { onFavoriteClick(favorites[idx]) }
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// ── Favorite Card ─────────────────────────────────────────────────────────────

@Composable
private fun FavoriteCard(
    favorite: Favorite,
    isPrivateMode: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fav_scale"
    )

    Column(
        modifier = modifier.scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Icon box
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(IOSShapes.iconRounded))
                .background(getFavoriteBg(favorite.iconKey))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            FaviconContent(favorite)
        }

        // Title
        BasicText(
            text = favorite.title,
            style = IOSTypography.caption1.copy(
                color = if (isPrivateMode) IOSColors.labelDark else IOSColors.label,
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FaviconContent(favorite: Favorite) {
    when (favorite.iconKey) {
        "apple" -> {
            // Apple logo approximation
            BasicText(
                "",
                style = IOSTypography.title2.copy(color = Color(0xFF555555))
            )
        }
        "bing" -> {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    "B",
                    style = IOSTypography.title3.copy(color = Color(0xFF0078D4))
                )
            }
        }
        "google" -> {
            BasicText(
                "G",
                style = IOSTypography.title3.copy(color = Color(0xFF4285F4))
            )
        }
        "yahoo" -> {
            BasicText(
                "Y!",
                style = IOSTypography.title3.copy(color = Color.White)
            )
        }
        else -> {
            BasicText(
                favorite.title.firstOrNull()?.toString() ?: "?",
                style = IOSTypography.title3.copy(color = Color.White)
            )
        }
    }
}

private fun getFavoriteBg(iconKey: String): Color = when (iconKey) {
    "apple" -> Color(0xFFF5F5F5)
    "bing" -> Color(0xFFFFFFFF)
    "google" -> Color(0xFFFFFFFF)
    "yahoo" -> Color(0xFF7B0099)
    else -> Color(0xFF007AFF)
}

// ── Privacy Report Card ───────────────────────────────────────────────────────

@Composable
private fun PrivacyReportCard(
    trackerCount: Int,
    isPrivateMode: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(IOSSpacing.md)) {
        BasicText(
            text = "Privacy Report",
            style = IOSTypography.title2.copy(
                color = if (isPrivateMode) IOSColors.labelDark else IOSColors.label
            )
        )

        FrostedCard(
            modifier = Modifier.fillMaxWidth(),
            isDark = isPrivateMode,
            cornerRadius = IOSShapes.largeRounded
        ) {
            Row(
                modifier = Modifier.padding(IOSSpacing.md),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(IOSSpacing.md)
            ) {
                CupertinoIcon(
                    CupertinoIcons.Shield,
                    tint = if (isPrivateMode) IOSColors.labelDark else IOSColors.label,
                    size = 24.dp,
                    strokeWidth = 1.8f
                )
                BasicText(
                    text = if (isPrivateMode) {
                        "Safari has not blocked any connections yet in Private Browsing."
                    } else {
                        "In the last seven days, Safari has prevented $trackerCount trackers from profiling you."
                    },
                    style = IOSTypography.subheadline.copy(
                        color = if (isPrivateMode) IOSColors.secondaryLabelDark else IOSColors.label
                    )
                )
            }
        }
    }
}

// ── Edit Button ───────────────────────────────────────────────────────────────

@Composable
private fun EditButton(isPrivateMode: Boolean) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (isPrivateMode) Color(0xFF3A3A3C) else Color(0xFFE5E5EA))
                .clickable {}
                .padding(horizontal = 32.dp, vertical = 10.dp)
        ) {
            BasicText(
                "Edit",
                style = IOSTypography.callout.copy(
                    color = if (isPrivateMode) IOSColors.labelDark else IOSColors.label
                )
            )
        }
    }
}
