package com.example.safari.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.safari.ui.components.CupertinoIcon
import com.example.safari.ui.components.CupertinoIcons
import com.example.safari.ui.components.glass.LiquidGlassSheet
import com.example.safari.ui.theme.*
import com.kyant.backdrop.Backdrop

// ── Add to Home Screen Sheet ──────────────────────────────────────────────────
// Matches Image 15 — title editable, URL shown below, "Open as Web App" toggle

@Composable
fun AddToHomeScreenSheet(
    backdrop: Backdrop,
    pageTitle: String,
    pageUrl: String,
    onAdd: (title: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editableTitle by remember { mutableStateOf(pageTitle) }
    var openAsWebApp by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    Box(modifier = modifier.fillMaxSize()) {
        // Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
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
            ) {
                // ── Header bar ────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IOSColors.systemBackground)
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    // X close
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E5EA))
                            .clickable(onClick = onDismiss)
                            .align(Alignment.CenterStart),
                        contentAlignment = Alignment.Center
                    ) {
                        CupertinoIcon(CupertinoIcons.XMark, tint = IOSColors.secondaryLabel, size = 12.dp)
                    }
                    // Title
                    BasicText(
                        "Add to Home Screen",
                        modifier = Modifier.align(Alignment.Center),
                        style = IOSTypography.headline.copy(color = IOSColors.label)
                    )
                    // Add button
                    AddButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = { onAdd(editableTitle); onDismiss() }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(IOSColors.separator)
                )

                // ── Icon + editable title + URL ───────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IOSColors.secondaryBackground)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Favicon square
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            editableTitle.firstOrNull()?.toString() ?: "G",
                            style = IOSTypography.title2.copy(color = IOSColors.iosBlue)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        // Editable name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BasicTextField(
                                value = editableTitle,
                                onValueChange = { editableTitle = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                singleLine = true,
                                textStyle = IOSTypography.body.copy(color = IOSColors.label),
                                cursorBrush = SolidColor(IOSColors.iosBlue)
                            )
                            if (editableTitle.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(IOSColors.secondaryLabel)
                                        .clickable { editableTitle = "" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    CupertinoIcon(
                                        CupertinoIcons.XMark,
                                        tint = Color.White,
                                        size = 8.dp
                                    )
                                }
                            }
                        }
                        // Hairline under title
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .height(0.5.dp)
                                .background(IOSColors.separator)
                        )
                        Spacer(Modifier.height(4.dp))
                        // URL (non-editable)
                        BasicText(
                            pageUrl,
                            style = IOSTypography.footnote.copy(color = IOSColors.secondaryLabel),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(IOSColors.separator)
                )

                // ── Open as Web App toggle ────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IOSColors.secondaryBackground)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BasicText(
                        "Open as Web App",
                        style = IOSTypography.body.copy(color = IOSColors.label)
                    )
                    IOSToggleSwitch(
                        checked = openAsWebApp,
                        onCheckedChange = { openAsWebApp = it }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(IOSColors.separator)
                )

                // ── Description ───────────────────────────────────────────────
                BasicText(
                    "An icon will be added to your Home Screen so you can quickly access this website.",
                    modifier = Modifier
                        .background(IOSColors.systemBackground)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    style = IOSTypography.footnote.copy(color = IOSColors.secondaryLabel)
                )

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Blue "Add" button ─────────────────────────────────────────────────────────

@Composable
private fun AddButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "add_btn_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(CircleShape)
            .background(IOSColors.iosBlue)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicText("Add", style = IOSTypography.callout.copy(color = Color.White))
    }
}

// ── Reusable iOS toggle switch ────────────────────────────────────────────────

@Composable
fun IOSToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) IOSColors.systemGreen else Color(0xFFE5E5EA),
        animationSpec = tween(200),
        label = "toggle_track"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 2.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "toggle_thumb"
    )

    Box(
        modifier = modifier
            .width(51.dp)
            .height(31.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .size(27.dp)
                .offset(x = thumbOffset, y = 2.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
