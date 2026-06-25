package com.example.safari.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.safari.ui.components.CupertinoIcon
import com.example.safari.ui.components.CupertinoIcons
import com.example.safari.ui.components.glass.GlassContextMenu
import com.example.safari.ui.components.glass.LiquidGlassSheet
import com.example.safari.ui.theme.*
import com.kyant.backdrop.Backdrop

// ── Share Sheet ───────────────────────────────────────────────────────────────
// Matches Image 14 & 17 — iOS-style share sheet with page header,
// app row, quick actions, and action list sections.

@Composable
fun ShareSheet(
    backdrop: Backdrop,
    pageTitle: String,
    pageUrl: String,
    onDismiss: () -> Unit,
    onCopy: () -> Unit,
    onAddToFavorites: () -> Unit,
    onAddToReadingList: () -> Unit,
    onAddBookmark: () -> Unit,
    onAddToHomeScreen: () -> Unit,
    onFindOnPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Scrim
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.30f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )

        // Sheet anchored to bottom
        LiquidGlassSheet(
            backdrop = backdrop,
            isDark = false,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp)
            ) {
                // ── Drag handle ───────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 4.dp)
                        .width(36.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(IOSColors.tertiaryLabel)
                        .align(Alignment.CenterHorizontally)
                )

                // ── Page header ───────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Favicon placeholder
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0F0F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            pageTitle.firstOrNull()?.toString() ?: "G",
                            style = IOSTypography.title3.copy(color = IOSColors.iosBlue)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        BasicText(
                            pageTitle,
                            style = IOSTypography.headline.copy(color = IOSColors.label),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        BasicText(
                            pageUrl,
                            style = IOSTypography.subheadline.copy(color = IOSColors.secondaryLabel),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE5E5EA))
                                .clickable {}
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                BasicText(
                                    "Options",
                                    style = IOSTypography.footnote.copy(color = IOSColors.label)
                                )
                                CupertinoIcon(
                                    CupertinoIcons.ArrowRight,
                                    tint = IOSColors.secondaryLabel,
                                    size = 10.dp
                                )
                            }
                        }
                    }
                    // X close
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E5EA))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        CupertinoIcon(CupertinoIcons.XMark, tint = IOSColors.secondaryLabel, size = 12.dp)
                    }
                }

                SheetDivider()

                // ── App row (Reminders, More) ─────────────────────────────────
                val appItems = listOf(
                    Triple("Reminders", CupertinoIcons.List, IOSColors.systemRed),
                    Triple("More", CupertinoIcons.Ellipsis, IOSColors.secondaryLabel)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(appItems) { (label, icon, tint) ->
                        AppIconItem(
                            label = label,
                            icon = icon,
                            iconTint = tint,
                            onClick = {}
                        )
                    }
                }

                SheetDivider()

                // ── Quick action circles (Copy, Add to Favorites, etc.) ───────
                val quickActions = listOf(
                    Triple("Copy", CupertinoIcons.Tabs, onCopy),
                    Triple("Add to\nFavorites", CupertinoIcons.Star, onAddToFavorites),
                    Triple("Add to\nReading List", CupertinoIcons.Glasses, onAddToReadingList),
                    Triple("Add\nBookmark to...", CupertinoIcons.Bookmark, onAddBookmark)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(quickActions) { (label, icon, action) ->
                        CircleActionItem(label = label, icon = icon, onClick = { action(); onDismiss() })
                    }
                }

                SheetDivider()

                // ── Action list group 1 ───────────────────────────────────────
                Spacer(Modifier.height(8.dp))
                ShareActionSection(
                    items = listOf(
                        Triple("Add to Favorites", CupertinoIcons.Star, onAddToFavorites),
                        Triple("Find on Page", CupertinoIcons.Search, onFindOnPage),
                        Triple("Add to Home Screen", CupertinoIcons.Plus, onAddToHomeScreen)
                    ),
                    onDismiss = onDismiss
                )

                Spacer(Modifier.height(8.dp))

                // ── Action list group 2 ───────────────────────────────────────
                ShareActionSection(
                    items = listOf(
                        Triple("Markup", CupertinoIcons.Share, {}),
                        Triple("Print", CupertinoIcons.Share, {}),
                        Triple("Use as Ringtone", CupertinoIcons.Microphone, {})
                    ),
                    onDismiss = onDismiss
                )

                Spacer(Modifier.height(8.dp))

                // ── Edit Actions ──────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onDismiss)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        "Edit Actions",
                        style = IOSTypography.callout.copy(color = IOSColors.iosBlue)
                    )
                }
            }
        }
    }
}

// ── Share Action Section (grouped list) ───────────────────────────────────────

@Composable
private fun ShareActionSection(
    items: List<Triple<String, CupertinoIcons, () -> Unit>>,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFEEEEF0))
    ) {
        items.forEachIndexed { i, (label, icon, action) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { action(); onDismiss() }
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CupertinoIcon(icon, tint = IOSColors.label, size = 22.dp, strokeWidth = 1.8f)
                BasicText(label, style = IOSTypography.body.copy(color = IOSColors.label))
            }
            if (i < items.size - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 52.dp)
                        .height(0.5.dp)
                        .background(IOSColors.separator)
                )
            }
        }
    }
}

// ── App Icon Item (square + label) ────────────────────────────────────────────

@Composable
private fun AppIconItem(
    label: String,
    icon: CupertinoIcons,
    iconTint: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "app_icon_scale"
    )

    Column(
        modifier = Modifier.scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEEEEF0))
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            CupertinoIcon(icon, tint = iconTint, size = 26.dp, strokeWidth = 1.8f)
        }
        BasicText(
            label,
            style = IOSTypography.caption1.copy(
                color = IOSColors.label,
                textAlign = TextAlign.Center
            )
        )
    }
}

// ── Circle Action Item (round + label) ────────────────────────────────────────

@Composable
private fun CircleActionItem(
    label: String,
    icon: CupertinoIcons,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "circle_action_scale"
    )

    Column(
        modifier = Modifier.scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEEEF0))
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            CupertinoIcon(icon, tint = IOSColors.label, size = 22.dp, strokeWidth = 1.8f)
        }
        BasicText(
            label,
            style = IOSTypography.caption1.copy(
                color = IOSColors.label,
                textAlign = TextAlign.Center
            ),
            maxLines = 2
        )
    }
}

@Composable
private fun SheetDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(IOSColors.separator)
    )
}
