package com.example.safari.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.BasicText
import com.example.safari.model.Bookmark
import com.example.safari.ui.components.*
import com.example.safari.ui.components.glass.*
import com.example.safari.ui.theme.*

// ── Context Menu (long press toolbar) ────────────────────────────────────────

@Composable
fun ContextMenuOverlay(
    isPrivateMode: Boolean,
    onDismiss: () -> Unit,
    onNewTab: () -> Unit,
    onNewPrivateTab: () -> Unit,
    onBookmarks: () -> Unit,
    onAllTabs: () -> Unit
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
        // Menu positioned above bottom toolbar
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            FrostedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 80.dp),
                cornerRadius = 14.dp,
                isDark = isPrivateMode
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    ContextMenuRow(
                        icon = CupertinoIcons.Plus,
                        text = "New Tab",
                        isDark = isPrivateMode,
                        onClick = { onNewTab(); onDismiss() }
                    )
                    ContextMenuDividerLine(isPrivateMode)
                    ContextMenuRow(
                        icon = CupertinoIcons.Hand,
                        text = "New Private Tab",
                        isDark = isPrivateMode,
                        onClick = { onNewPrivateTab(); onDismiss() }
                    )
                    ContextMenuDividerLine(isPrivateMode)

                    Spacer(Modifier.height(8.dp))
                    ContextMenuDividerLine(isPrivateMode)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        BottomContextButton(
                            icon = CupertinoIcons.Bookmark,
                            label = "Bookmarks",
                            isDark = isPrivateMode,
                            onClick = { onBookmarks(); onDismiss() }
                        )
                        BottomContextButton(
                            icon = CupertinoIcons.Tabs,
                            label = "All Tabs",
                            isDark = isPrivateMode,
                            onClick = { onAllTabs(); onDismiss() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContextMenuRow(
    icon: CupertinoIcons,
    text: String,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        CupertinoIcon(icon, tint = if (isDark) IOSColors.labelDark else IOSColors.label, size = 20.dp)
        BasicText(text, style = IOSTypography.body.copy(color = if (isDark) IOSColors.labelDark else IOSColors.label))
    }
}

@Composable
private fun BottomContextButton(
    icon: CupertinoIcons,
    label: String,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(IOSSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CupertinoIcon(icon, tint = if (isDark) IOSColors.labelDark else IOSColors.label, size = 22.dp)
        BasicText(label, style = IOSTypography.caption1.copy(color = if (isDark) IOSColors.labelDark else IOSColors.label))
    }
}

@Composable
private fun ContextMenuDividerLine(isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(if (isDark) Color.White.copy(0.1f) else Color.Black.copy(0.08f))
    )
}

// ── Bookmarks Screen ──────────────────────────────────────────────────────────

@Composable
fun BookmarksScreen(
    bookmarks: List<Bookmark>,
    onClose: () -> Unit,
    onBookmarkClick: (Bookmark) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IOSColors.systemBackground)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, end = 16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color(0xFFE5E5EA))
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    CupertinoIcon(CupertinoIcons.Ellipsis, tint = IOSColors.label, size = 16.dp)
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color(0xFFE5E5EA))
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    CupertinoIcon(CupertinoIcons.XMark, tint = IOSColors.label, size = 14.dp)
                }
            }
        }

        BasicText(
            "Recently Saved >",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = IOSTypography.headline.copy(color = IOSColors.secondaryLabel)
        )

        // Tab bar: Bookmarks | Reading | History
        BookmarkTabBar(
            selectedTab = selectedTab,
            onTabSelect = { selectedTab = it },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Content
        when (selectedTab) {
            0 -> BookmarkListContent(bookmarks, onBookmarkClick)
            1 -> BasicText("Reading List", modifier = Modifier.padding(16.dp), style = IOSTypography.body)
            2 -> BasicText("History", modifier = Modifier.padding(16.dp), style = IOSTypography.body)
        }
    }
}

@Composable
private fun BookmarkTabBar(
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFE5E5EA)),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf(
            CupertinoIcons.Bookmark to "Bookmarks",
            CupertinoIcons.Glasses to "Reading",
            CupertinoIcons.Clock to "History"
        ).forEachIndexed { index, (icon, label) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedTab == index) Color.White else Color.Transparent)
                    .clickable { onTabSelect(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                CupertinoIcon(
                    icon,
                    tint = if (selectedTab == index) IOSColors.label else IOSColors.secondaryLabel,
                    size = 20.dp
                )
            }
        }
    }
}

@Composable
private fun BookmarkListContent(
    bookmarks: List<Bookmark>,
    onBookmarkClick: (Bookmark) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        BasicText("Folders", style = IOSTypography.subheadline.copy(color = IOSColors.secondaryLabel), modifier = Modifier.padding(bottom = 8.dp))

        FrostedCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 14.dp) {
            Column {
                BookmarkFolderRow("Favorites", 4, true) {}
                Box(Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFD1D1D6)))
                BookmarkFolderRow("Tab Group Favorites", null, true) {}
            }
        }

        if (bookmarks.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            BasicText("Bookmarks", style = IOSTypography.subheadline.copy(color = IOSColors.secondaryLabel), modifier = Modifier.padding(bottom = 8.dp))
            FrostedCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 14.dp) {
                Column {
                    bookmarks.forEachIndexed { i, bookmark ->
                        BookmarkItemRow(bookmark, onClick = { onBookmarkClick(bookmark) })
                        if (i < bookmarks.size - 1) {
                            Box(Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFD1D1D6)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkFolderRow(title: String, count: Int?, showArrow: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(modifier = Modifier.size(30.dp).clip(RoundedCornerShape(7.dp)).background(IOSColors.iosBlue.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
            CupertinoIcon(CupertinoIcons.Star, tint = IOSColors.iosBlue, size = 16.dp)
        }
        BasicText(title, modifier = Modifier.weight(1f), style = IOSTypography.body.copy(color = IOSColors.label))
        if (count != null) {
            BasicText("$count", style = IOSTypography.body.copy(color = IOSColors.secondaryLabel))
        }
        if (showArrow) {
            CupertinoIcon(CupertinoIcons.ArrowRight, tint = IOSColors.tertiaryLabel, size = 12.dp)
        }
    }
}

@Composable
private fun BookmarkItemRow(bookmark: Bookmark, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(modifier = Modifier.size(30.dp).clip(RoundedCornerShape(7.dp)).background(Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
            BasicText(bookmark.title.firstOrNull()?.toString() ?: "?", style = IOSTypography.footnote.copy(color = IOSColors.secondaryLabel))
        }
        BasicText(bookmark.title, modifier = Modifier.weight(1f), style = IOSTypography.body.copy(color = IOSColors.label), maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
    }
}

// ── Customize Start Page Sheet ────────────────────────────────────────────────

@Composable
fun CustomizeStartPageSheet(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFavorites by remember { mutableStateOf(true) }
    var showPrivacyReport by remember { mutableStateOf(true) }
    var showReadingList by remember { mutableStateOf(true) }
    var showRecentlyClosed by remember { mutableStateOf(true) }
    var showFrequentlyVisited by remember { mutableStateOf(true) }
    var showBackgroundImage by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
            .background(IOSColors.systemBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText("Customize Start Page", style = IOSTypography.headline)
            Box(
                modifier = Modifier.size(30.dp).clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color(0xFFE5E5EA)).clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                CupertinoIcon(CupertinoIcons.XMark, tint = IOSColors.label, size = 12.dp)
            }
        }

        // Toggle rows
        FrostedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), cornerRadius = 14.dp) {
            Column {
                ToggleRow("Favorites", CupertinoIcons.Star, showFavorites) { showFavorites = it }
                DividerLine()
                ToggleRow("Privacy Report", CupertinoIcons.Shield, showPrivacyReport) { showPrivacyReport = it }
                DividerLine()
                ToggleRow("Reading List", CupertinoIcons.Glasses, showReadingList) { showReadingList = it }
                DividerLine()
                ToggleRow("Recently Closed Tabs", CupertinoIcons.Tabs, showRecentlyClosed) { showRecentlyClosed = it }
            }
        }

        Spacer(Modifier.height(16.dp))

        BasicText("Show in Suggestions", style = IOSTypography.subheadline.copy(color = IOSColors.iosBlue), modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

        FrostedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), cornerRadius = 14.dp) {
            ToggleRow("Frequently Visited", CupertinoIcons.Clock, showFrequentlyVisited) { showFrequentlyVisited = it }
        }

        Spacer(Modifier.height(16.dp))

        FrostedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), cornerRadius = 14.dp) {
            Column {
                ToggleRow("Background Image", CupertinoIcons.Globe, showBackgroundImage) { showBackgroundImage = it }
            }
        }

        if (showBackgroundImage) {
            Spacer(Modifier.height(12.dp))
            WallpaperGrid(modifier = Modifier.padding(horizontal = 16.dp))
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ToggleRow(label: String, icon: CupertinoIcons, value: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CupertinoIcon(icon, tint = IOSColors.label, size = 22.dp, strokeWidth = 1.8f)
        BasicText(label, modifier = Modifier.weight(1f), style = IOSTypography.body.copy(color = IOSColors.label))
        BookmarkToggle(checked = value, onCheckedChange = onToggle)
        CupertinoIcon(CupertinoIcons.List, tint = IOSColors.tertiaryLabel, size = 18.dp)
    }
}

@Composable
private fun BookmarkToggle(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
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
        modifier = Modifier
            .width(50.dp).height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .offset(x = thumbOffset, y = 2.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(start = 52.dp).height(0.5.dp).background(Color(0xFFD1D1D6))
    )
}

@Composable
private fun WallpaperGrid(modifier: Modifier = Modifier) {
    val colors = listOf(
        Color(0xFFE8C4A0), Color(0xFFD4A5A5), Color(0xFFA5B8D4),
        Color(0xFFB8D4A5), Color(0xFFD4C4A5), Color(0xFF85A98F),
        Color(0xFF5B8FB9), Color(0xFF9B59B6), Color(0xFFE74C3C)
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Add button
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE5E5EA)),
                contentAlignment = Alignment.Center
            ) {
                CupertinoIcon(CupertinoIcons.Plus, tint = IOSColors.secondaryLabel, size = 24.dp)
            }
            colors.take(2).forEach { c ->
                Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(10.dp)).background(c))
            }
        }
        colors.drop(2).chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { c ->
                    Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(10.dp)).background(c))
                }
            }
        }
    }
}
