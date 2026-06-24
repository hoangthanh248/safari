package com.example.safari.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.safari.model.BrowserTab
import com.example.safari.ui.components.*
import com.example.safari.ui.components.glass.*
import com.example.safari.ui.theme.*

// ── Tab Switcher Screen ───────────────────────────────────────────────────────

@Composable
fun TabSwitcherScreen(
    tabs: List<BrowserTab>,
    activeTabIndex: Int,
    isPrivateMode: Boolean,
    onTabSelect: (Int) -> Unit,
    onTabClose: (String) -> Unit,
    onNewTab: (Boolean) -> Unit,
    onDone: () -> Unit,
    onSwitchMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    val bgColor = if (isPrivateMode) {
        Brush.verticalGradient(listOf(Color(0xFF1C1C1E), Color(0xFF2C2C2E)))
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFFD4DDE8), Color(0xFFCDD8E3), Color(0xFFC8E0D8))
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Top more button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 56.dp, start = 16.dp)
        ) {
            ToolbarIconButton(
                icon = CupertinoIcons.Ellipsis,
                tint = if (isPrivateMode) IOSColors.labelDark else IOSColors.label,
                onClick = { showMenu = true },
                isDark = isPrivateMode
            )
        }

        // Tab grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 100.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(tabs) { index, tab ->
                TabCard(
                    tab = tab,
                    isActive = index == activeTabIndex,
                    isPrivateMode = isPrivateMode,
                    onClick = { onTabSelect(index) },
                    onClose = { onTabClose(tab.id) }
                )
            }
        }

        // Bottom bar
        TabSwitcherBottomBar(
            tabCount = tabs.size,
            isPrivateMode = isPrivateMode,
            modifier = Modifier.align(Alignment.BottomCenter),
            onNewTab = { onNewTab(isPrivateMode) },
            onSwitchMode = onSwitchMode,
            onDone = onDone
        )

        // Context menu
        if (showMenu) {
            TabContextMenu(
                isPrivateMode = isPrivateMode,
                onDismiss = { showMenu = false },
                onManageTabGroups = { showMenu = false },
                onSelectTabs = { showMenu = false },
                onArrangeTabs = { showMenu = false }
            )
        }
    }
}

// ── Tab Card ──────────────────────────────────────────────────────────────────

@Composable
private fun TabCard(
    tab: BrowserTab,
    isActive: Boolean,
    isPrivateMode: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tab_card_scale"
    )

    val cardBg = if (isPrivateMode) Color(0xFF2C2C2E) else Color.White
    val activeStroke = if (isPrivateMode) IOSColors.iosBlueDark else IOSColors.iosBlue

    Box(
        modifier = Modifier
            .scale(scale)
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(
                width = if (isActive) 2.dp else 0.5.dp,
                color = if (isActive) activeStroke else Color.Black.copy(alpha = 0.08f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isPrivateMode) Color(0xFF3A3A3C) else Color(0xFFF2F2F7))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = tab.title.ifEmpty { "New Tab" },
                    style = IOSTypography.caption1.copy(
                        color = if (isPrivateMode) IOSColors.labelDark else IOSColors.label
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                // Close button
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPrivateMode) Color(0xFF5A5A5E)
                            else Color(0xFFD1D1D6)
                        )
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    CupertinoIcon(
                        CupertinoIcons.XMark,
                        tint = if (isPrivateMode) Color.White else IOSColors.secondaryLabel,
                        size = 8.dp,
                        strokeWidth = 2f
                    )
                }
            }

            // Content preview
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (tab.url.isEmpty()) {
                            if (isPrivateMode) Color(0xFF1C1C1E) else IOSColors.systemBackground
                        } else {
                            Color.White
                        }
                    )
            ) {
                if (tab.url.isEmpty()) {
                    // Start page preview inside tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(8.dp))
                        BasicText(
                            "☆ Start Page",
                            style = IOSTypography.caption2.copy(
                                color = if (isPrivateMode) IOSColors.secondaryLabelDark
                                else IOSColors.secondaryLabel,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                } else {
                    BasicText(
                        tab.url,
                        modifier = Modifier.padding(8.dp),
                        style = IOSTypography.caption2.copy(
                            color = IOSColors.secondaryLabel
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ── Tab Switcher Bottom Bar ───────────────────────────────────────────────────

@Composable
private fun TabSwitcherBottomBar(
    tabCount: Int,
    isPrivateMode: Boolean,
    modifier: Modifier = Modifier,
    onNewTab: () -> Unit,
    onSwitchMode: () -> Unit,
    onDone: () -> Unit
) {
    val bg = if (isPrivateMode) Color(0xE51C1C1E) else Color(0xD9F2F2F7)
    val textColor = if (isPrivateMode) IOSColors.labelDark else IOSColors.label

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // + New tab
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isPrivateMode) Color(0xFF3A3A3C) else Color(0xFFE5E5EA))
                .clickable(onClick = onNewTab),
            contentAlignment = Alignment.Center
        ) {
            CupertinoIcon(CupertinoIcons.Plus, tint = textColor, size = 16.dp)
        }

        // Private / mode label
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .clickable(onClick = onSwitchMode)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BasicText(
                text = "Private",
                style = IOSTypography.callout.copy(
                    color = if (isPrivateMode) textColor else textColor.copy(alpha = 0.5f)
                )
            )
        }

        // Tab count pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(if (isPrivateMode) Color(0xFF3A3A3C) else Color(0xFFE5E5EA))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BasicText(
                text = if (tabCount == 1) "Start Page" else "$tabCount Tabs",
                style = IOSTypography.callout.copy(color = textColor)
            )
        }

        // Done - blue circle checkmark
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(IOSColors.iosBlue)
                .clickable(onClick = onDone),
            contentAlignment = Alignment.Center
        ) {
            CupertinoIcon(CupertinoIcons.Checkmark, tint = Color.White, size = 16.dp, strokeWidth = 2.5f)
        }
    }
}

// ── Tab Context Menu ──────────────────────────────────────────────────────────

@Composable
private fun TabContextMenu(
    isPrivateMode: Boolean,
    onDismiss: () -> Unit,
    onManageTabGroups: () -> Unit,
    onSelectTabs: () -> Unit,
    onArrangeTabs: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        FrostedCard(
            modifier = Modifier
                .padding(top = 52.dp, start = 12.dp)
                .width(220.dp),
            cornerRadius = 14.dp,
            isDark = isPrivateMode
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                ContextMenuItem(
                    icon = CupertinoIcons.List,
                    text = "Manage Tab Groups",
                    isDark = isPrivateMode,
                    onClick = { onManageTabGroups(); onDismiss() }
                )
                ContextMenuDivider(isDark = isPrivateMode)
                ContextMenuItem(
                    icon = CupertinoIcons.CheckCircle,
                    text = "Select Tabs",
                    isDark = isPrivateMode,
                    onClick = { onSelectTabs(); onDismiss() }
                )
                ContextMenuDivider(isDark = isPrivateMode)
                ContextMenuItem(
                    icon = CupertinoIcons.SortUpDown,
                    text = "Arrange Tabs By",
                    isDark = isPrivateMode,
                    trailing = { CupertinoIcon(CupertinoIcons.ArrowRight, tint = IOSColors.secondaryLabel, size = 12.dp) },
                    onClick = { onArrangeTabs(); onDismiss() }
                )
            }
        }
    }
}

@Composable
private fun ContextMenuItem(
    icon: CupertinoIcons,
    text: String,
    isDark: Boolean,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CupertinoIcon(
            icon,
            tint = if (isDark) IOSColors.labelDark else IOSColors.label,
            size = 20.dp,
            strokeWidth = 1.8f
        )
        BasicText(
            text,
            modifier = Modifier.weight(1f),
            style = IOSTypography.body.copy(
                color = if (isDark) IOSColors.labelDark else IOSColors.label
            )
        )
        trailing?.invoke()
    }
}

@Composable
private fun ContextMenuDivider(isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(if (isDark) Color.White.copy(0.1f) else Color.Black.copy(0.1f))
    )
}
