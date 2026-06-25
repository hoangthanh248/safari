package com.example.safari.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.safari.browser.BrowserViewModel
import com.example.safari.browser.SafariWebView
import com.example.safari.model.NavigationEvent
import com.example.safari.ui.components.glass.*
import com.example.safari.ui.components.toolbar.BrowsingGlassToolbar
import com.example.safari.ui.components.toolbar.SafariGlassToolbar
import com.example.safari.ui.theme.*
import com.kyant.backdrop.Backdrop

// ── Browser Root Screen ───────────────────────────────────────────────────────
// Wraps entire UI in LiquidGlassRoot so all glass components can sample the backdrop.

@Composable
fun BrowserRootScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var overlay by remember { mutableStateOf(BrowserOverlay.None) }
    val isShowingPage = state.currentUrl.isNotEmpty()

    LiquidGlassRoot(modifier = modifier.fillMaxSize()) { backdrop ->
        // ── Background: gradient that the glass samples ────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (state.isPrivateMode) {
                        Brush.radialGradient(
                            listOf(Color(0xFF2C2C2E), Color(0xFF1C1C1E)),
                            radius = 1200f
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(Color(0xFFD4E4F0), Color(0xFFC8DDE8), Color(0xFFCDE0D6))
                        )
                    }
                )
        )

        // ── WebView / Start Page ───────────────────────────────────────────────
        if (isShowingPage) {
            SafariWebView(
                url = state.currentUrl,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp)
            )
        } else {
            StartPageScreen(
                favorites = state.favorites,
                isPrivateMode = state.isPrivateMode,
                trackerCount = 0,
                onFavoriteClick = { fav ->
                    viewModel.handleNavigation(NavigationEvent.LoadUrl(fav.url))
                },
                onCustomize = { overlay = BrowserOverlay.Customize },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp)
            )
        }

        // ── Liquid Glass Bottom Toolbar ───────────────────────────────────────
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            if (isShowingPage) {
                BrowsingGlassToolbar(
                    backdrop = backdrop,
                    displayUrl = state.displayUrl,
                    isLoading = state.isLoading,
                    canGoBack = state.canGoBack,
                    canGoForward = state.canGoForward,
                    isPrivateMode = state.isPrivateMode,
                    progress = state.progress,
                    tabCount = state.tabs.size,
                    onBack = { viewModel.handleNavigation(NavigationEvent.GoBack) },
                    onForward = { viewModel.handleNavigation(NavigationEvent.GoForward) },
                    onUrlClick = { overlay = BrowserOverlay.Search },
                    onTabs = { overlay = BrowserOverlay.Tabs },
                    onMore = { overlay = BrowserOverlay.ContextMenu },
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                )
            } else {
                SafariGlassToolbar(
                    backdrop = backdrop,
                    currentUrl = state.currentUrl,
                    displayUrl = state.displayUrl,
                    isLoading = state.isLoading,
                    canGoBack = state.canGoBack,
                    isPrivateMode = state.isPrivateMode,
                    progress = state.progress,
                    onBack = { viewModel.handleNavigation(NavigationEvent.GoBack) },
                    onSearch = { q -> viewModel.loadUrl(q); overlay = BrowserOverlay.None },
                    onSearchFocus = { overlay = BrowserOverlay.Search },
                    onMore = { overlay = BrowserOverlay.ContextMenu },
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                )
            }
        }

        // ── Overlays ───────────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = overlay == BrowserOverlay.Search,
            enter = fadeIn() + slideInVertically { it / 3 },
            exit = fadeOut() + slideOutVertically { it / 3 }
        ) {
            SearchScreen(
                initialText = state.currentUrl,
                recentHistory = state.history,
                isPrivateMode = state.isPrivateMode,
                onSearch = { q -> viewModel.loadUrl(q); overlay = BrowserOverlay.None },
                onDismiss = { overlay = BrowserOverlay.None },
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(
            visible = overlay == BrowserOverlay.Tabs,
            enter = fadeIn() + scaleIn(initialScale = 0.96f),
            exit = fadeOut() + scaleOut(targetScale = 0.96f)
        ) {
            TabSwitcherScreen(
                tabs = state.tabs,
                activeTabIndex = state.activeTabIndex,
                isPrivateMode = state.isPrivateMode,
                onTabSelect = { idx ->
                    viewModel.handleNavigation(NavigationEvent.SwitchTab(idx))
                    overlay = BrowserOverlay.None
                },
                onTabClose = { id -> viewModel.handleNavigation(NavigationEvent.CloseTab(id)) },
                onNewTab = { isPrivate ->
                    viewModel.handleNavigation(NavigationEvent.NewTab(isPrivate))
                    overlay = BrowserOverlay.None
                },
                onDone = { overlay = BrowserOverlay.None },
                onSwitchMode = { viewModel.togglePrivateMode() },
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(
            visible = overlay == BrowserOverlay.ContextMenu,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            GlassContextMenuOverlay(
                backdrop = backdrop,
                isPrivateMode = state.isPrivateMode,
                onDismiss = { overlay = BrowserOverlay.None },
                onNewTab = { viewModel.handleNavigation(NavigationEvent.NewTab(false)); overlay = BrowserOverlay.None },
                onNewPrivateTab = { viewModel.handleNavigation(NavigationEvent.NewTab(true)); overlay = BrowserOverlay.None },
                onBookmarks = { overlay = BrowserOverlay.Bookmarks },
                onAllTabs = { overlay = BrowserOverlay.Tabs },
                onShare = { overlay = BrowserOverlay.Share },
                onHistory = { overlay = BrowserOverlay.History },
                onExtensions = { overlay = BrowserOverlay.Extensions }
            )
        }

        AnimatedVisibility(
            visible = overlay == BrowserOverlay.Bookmarks,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            BookmarksScreen(
                bookmarks = state.bookmarks,
                onClose = { overlay = BrowserOverlay.None },
                onBookmarkClick = { bm -> viewModel.loadUrl(bm.url); overlay = BrowserOverlay.None },
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(
            visible = overlay == BrowserOverlay.Customize,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            CustomizeStartPageSheet(
                onClose = { overlay = BrowserOverlay.None },
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(
            visible = overlay == BrowserOverlay.Private,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PrivateModeScreen(
                onTurnOn = { viewModel.togglePrivateMode(); overlay = BrowserOverlay.None },
                onNotNow = { overlay = BrowserOverlay.None },
                modifier = Modifier.fillMaxSize()
            )
        }

        // ── Share Sheet ────────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = overlay == BrowserOverlay.Share,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            ShareSheet(
                backdrop = backdrop,
                pageTitle = state.title.ifEmpty { state.displayUrl },
                pageUrl = state.displayUrl,
                onDismiss = { overlay = BrowserOverlay.None },
                onCopy = { overlay = BrowserOverlay.None },
                onAddToFavorites = { viewModel.addBookmark(state.currentUrl, state.title); overlay = BrowserOverlay.None },
                onAddToReadingList = { overlay = BrowserOverlay.None },
                onAddBookmark = { viewModel.addBookmark(state.currentUrl, state.title); overlay = BrowserOverlay.None },
                onAddToHomeScreen = { overlay = BrowserOverlay.AddToHomeScreen },
                onFindOnPage = { overlay = BrowserOverlay.None },
                modifier = Modifier.fillMaxSize()
            )
        }

        // ── Add to Home Screen ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = overlay == BrowserOverlay.AddToHomeScreen,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            AddToHomeScreenSheet(
                backdrop = backdrop,
                pageTitle = state.title.ifEmpty { state.displayUrl },
                pageUrl = state.currentUrl,
                onAdd = { overlay = BrowserOverlay.None },
                onDismiss = { overlay = BrowserOverlay.None },
                modifier = Modifier.fillMaxSize()
            )
        }

        // ── Highlights Sheet ───────────────────────────────────────────────────
        AnimatedVisibility(
            visible = overlay == BrowserOverlay.Highlights,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            HighlightsSheet(
                backdrop = backdrop,
                onNotNow = { overlay = BrowserOverlay.None },
                onTurnOn = { overlay = BrowserOverlay.None },
                modifier = Modifier.fillMaxSize()
            )
        }

        // ── Browse Extensions ──────────────────────────────────────────────────
        AnimatedVisibility(
            visible = overlay == BrowserOverlay.Extensions,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            BrowseExtensionsSheet(
                backdrop = backdrop,
                onClose = { overlay = BrowserOverlay.None },
                modifier = Modifier.fillMaxSize()
            )
        }

        // ── History ────────────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = overlay == BrowserOverlay.History,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            HistoryScreen(
                backdrop = backdrop,
                history = state.history,
                onClose = { overlay = BrowserOverlay.None },
                onItemClick = { item -> viewModel.loadUrl(item.url); overlay = BrowserOverlay.None },
                onClearHistory = { viewModel.clearHistory() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ── Glass Context Menu Overlay ────────────────────────────────────────────────
// Uses GlassContextMenu with real backdrop instead of FrostedCard.

@Composable
fun GlassContextMenuOverlay(
    backdrop: Backdrop,
    isPrivateMode: Boolean,
    onDismiss: () -> Unit,
    onNewTab: () -> Unit,
    onNewPrivateTab: () -> Unit,
    onBookmarks: () -> Unit,
    onAllTabs: () -> Unit,
    onShare: () -> Unit = {},
    onHistory: () -> Unit = {},
    onExtensions: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        GlassContextMenu(
            backdrop = backdrop,
            isDark = isPrivateMode,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 80.dp)
        ) {
            // Share
            GlassContextMenuIconRow(
                icon = com.example.safari.ui.components.CupertinoIcons.Share,
                text = "Share",
                isDark = isPrivateMode,
                onClick = { onShare(); onDismiss() }
            )
            GlassContextMenuDivider(isDark = isPrivateMode)
            // Add to Favorites
            GlassContextMenuIconRow(
                icon = com.example.safari.ui.components.CupertinoIcons.Star,
                text = "Add to Favorites",
                isDark = isPrivateMode,
                onClick = { onDismiss() }
            )
            GlassContextMenuDivider(isDark = isPrivateMode)
            // Add Bookmark
            GlassContextMenuIconRow(
                icon = com.example.safari.ui.components.CupertinoIcons.Bookmark,
                text = "Add Bookmark to...",
                isDark = isPrivateMode,
                onClick = { onDismiss() }
            )
            GlassContextMenuDivider(isDark = isPrivateMode)
            // New Tab
            GlassContextMenuIconRow(
                icon = com.example.safari.ui.components.CupertinoIcons.Plus,
                text = "New Tab",
                isDark = isPrivateMode,
                onClick = { onNewTab(); onDismiss() }
            )
            GlassContextMenuDivider(isDark = isPrivateMode)
            // New Private Tab
            GlassContextMenuIconRow(
                icon = com.example.safari.ui.components.CupertinoIcons.Hand,
                text = "New Private Tab",
                isDark = isPrivateMode,
                onClick = { onNewPrivateTab(); onDismiss() }
            )
            GlassContextMenuDivider(isDark = isPrivateMode)

            // Bottom row: Bookmarks | All Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GlassBottomContextButton(
                    icon = com.example.safari.ui.components.CupertinoIcons.Bookmark,
                    label = "Bookmarks",
                    isDark = isPrivateMode,
                    onClick = { onBookmarks(); onDismiss() }
                )
                GlassBottomContextButton(
                    icon = com.example.safari.ui.components.CupertinoIcons.Tabs,
                    label = "All Tabs",
                    isDark = isPrivateMode,
                    onClick = { onAllTabs(); onDismiss() }
                )
            }
        }
    }
}

@Composable
private fun GlassContextMenuIconRow(
    icon: com.example.safari.ui.components.CupertinoIcons,
    text: String,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isDark) IOSColors.labelDark else IOSColors.label
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        com.example.safari.ui.components.CupertinoIcon(
            icon = icon, tint = textColor, size = 20.dp, strokeWidth = 1.8f
        )
        androidx.compose.foundation.text.BasicText(
            text,
            style = IOSTypography.body.copy(color = textColor)
        )
    }
}

@Composable
private fun GlassContextMenuRow(text: String, isDark: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.text.BasicText(
            text,
            style = IOSTypography.body.copy(color = if (isDark) IOSColors.labelDark else IOSColors.label)
        )
    }
}

@Composable
private fun GlassContextMenuDivider(isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(if (isDark) Color.White.copy(0.1f) else Color.Black.copy(0.08f))
    )
}

@Composable
private fun GlassBottomContextButton(
    icon: com.example.safari.ui.components.CupertinoIcons,
    label: String,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isDark) IOSColors.labelDark else IOSColors.label
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        com.example.safari.ui.components.CupertinoIcon(icon = icon, tint = tint, size = 22.dp)
        androidx.compose.foundation.text.BasicText(
            label,
            style = IOSTypography.caption1.copy(color = tint)
        )
    }
}
