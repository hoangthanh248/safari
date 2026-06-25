package com.example.safari.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import com.example.safari.ui.components.CupertinoIcon
import com.example.safari.ui.components.CupertinoIcons
import com.example.safari.ui.components.glass.GlassContextMenu
import com.example.safari.ui.components.glass.LiquidGlassSheet
import com.example.safari.ui.theme.*
import com.kyant.backdrop.Backdrop

// ── Highlights Sheet ──────────────────────────────────────────────────────────
// Matches Image 13 — "Highlights" popup with Not Now / Turn On buttons

@Composable
fun HighlightsSheet(
    backdrop: Backdrop,
    onNotNow: () -> Unit,
    onTurnOn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.15f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onNotNow
                )
        )

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
                    .padding(horizontal = 16.dp)
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 12.dp)
                        .width(36.dp).height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(IOSColors.tertiaryLabel)
                        .align(Alignment.CenterHorizontally)
                )

                // Title
                BasicText(
                    "Highlights",
                    style = IOSTypography.title3.copy(color = IOSColors.label)
                )

                Spacer(Modifier.height(8.dp))

                // Description
                BasicText(
                    "Safari intelligently displays summaries, previews, and suggestions from the web, nearby locations, and more.",
                    style = IOSTypography.subheadline.copy(color = IOSColors.label)
                )

                Spacer(Modifier.height(12.dp))

                BasicText(
                    "You can adjust this in Settings.",
                    style = IOSTypography.subheadline.copy(color = IOSColors.label)
                )

                // "Learn more..." link
                BasicText(
                    "Learn more...",
                    style = IOSTypography.subheadline.copy(color = IOSColors.iosBlue),
                    modifier = Modifier.clickable {}
                )

                Spacer(Modifier.height(20.dp))

                // Not Now / Turn On buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Not Now — secondary grey
                    HighlightsSecondaryButton(
                        text = "Not Now",
                        modifier = Modifier.weight(1f),
                        onClick = onNotNow
                    )
                    // Turn On — blue primary
                    HighlightsPrimaryButton(
                        text = "Turn On",
                        modifier = Modifier.weight(1f),
                        onClick = onTurnOn
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(IOSColors.separator)
                )

                // Hide Distracting Items row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {}
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE5E5EA)),
                        contentAlignment = Alignment.Center
                    ) {
                        CupertinoIcon(CupertinoIcons.XMark, tint = IOSColors.secondaryLabel, size = 14.dp)
                    }
                    BasicText(
                        "Hide Distracting Items",
                        style = IOSTypography.callout.copy(color = IOSColors.label)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(IOSColors.separator)
                )

                // Reader / font toolbar (bottom reader controls)
                PageReaderToolbar(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
private fun HighlightsSecondaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, spring(stiffness = Spring.StiffnessHigh), label = "btn_s")
    Box(
        modifier = modifier
            .scale(scale)
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE5E5EA))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        BasicText(text, style = IOSTypography.callout.copy(color = IOSColors.label))
    }
}

@Composable
private fun HighlightsPrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, spring(stiffness = Spring.StiffnessHigh), label = "btn_p")
    Box(
        modifier = modifier
            .scale(scale)
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(IOSColors.iosBlue)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        BasicText(text, style = IOSTypography.callout.copy(color = Color.White))
    }
}

// ── Page Reader Controls (bottom of Image 13) ─────────────────────────────────
// Reader icon | A- | A+ | ···

@Composable
fun PageReaderToolbar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reader view icon
        ReaderControlButton(modifier = Modifier.size(44.dp)) {
            CupertinoIcon(CupertinoIcons.Search, tint = IOSColors.label, size = 18.dp)
        }
        // A- (smaller font)
        ReaderControlButton(modifier = Modifier.weight(1f).height(44.dp)) {
            BasicText("A", style = IOSTypography.subheadline.copy(color = IOSColors.label))
        }
        // A+ (larger font)
        ReaderControlButton(modifier = Modifier.weight(1f).height(44.dp)) {
            BasicText("A", style = IOSTypography.title3.copy(color = IOSColors.label))
        }
        // ···
        ReaderControlButton(modifier = Modifier.size(44.dp)) {
            CupertinoIcon(CupertinoIcons.Ellipsis, tint = IOSColors.label, size = 18.dp)
        }
    }
}

@Composable
private fun ReaderControlButton(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFE5E5EA))
            .clickable {},
        contentAlignment = Alignment.Center
    ) { content() }
}

// ── Browse Extensions Sheet (Image 10) ────────────────────────────────────────
// "Browse Extensions" screen with search bar and CTA button

@Composable
fun BrowseExtensionsSheet(
    backdrop: Backdrop,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    LiquidGlassSheet(
        backdrop = backdrop,
        isDark = false,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                BasicText(
                    "Browse Extensions",
                    modifier = Modifier.align(Alignment.Center),
                    style = IOSTypography.headline.copy(color = IOSColors.label)
                )
                // Done (blue circle checkmark)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(IOSColors.iosBlue)
                        .clickable(onClick = onClose)
                        .align(Alignment.CenterEnd),
                    contentAlignment = Alignment.Center
                ) {
                    CupertinoIcon(CupertinoIcons.Checkmark, tint = Color.White, size = 16.dp, strokeWidth = 2.5f)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(IOSColors.separator)
            )

            Spacer(Modifier.height(16.dp))

            // Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE5E5EA))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CupertinoIcon(CupertinoIcons.Search, tint = IOSColors.secondaryLabel, size = 16.dp)
                BasicText(
                    "Search",
                    style = IOSTypography.body.copy(color = IOSColors.secondaryLabel)
                )
                Spacer(Modifier.weight(1f))
                CupertinoIcon(CupertinoIcons.Microphone, tint = IOSColors.secondaryLabel, size = 16.dp)
            }

            Spacer(Modifier.height(16.dp))

            // "Browse Extensions" blue CTA button
            ExtensionsCTAButton(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                onClick = {}
            )

            // Empty state below
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun ExtensionsCTAButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, spring(stiffness = Spring.StiffnessMediumLow), label = "cta")
    Box(
        modifier = modifier
            .scale(scale)
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(IOSColors.iosBlue)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CupertinoIcon(CupertinoIcons.Plus, tint = Color.White, size = 18.dp)
            BasicText("Browse Extensions", style = IOSTypography.callout.copy(color = Color.White))
        }
    }
}
