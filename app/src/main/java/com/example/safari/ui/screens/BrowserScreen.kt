package com.example.safari.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.safari.browser.BrowserViewModel
import com.example.safari.browser.SafariWebView
import com.example.safari.model.NavigationEvent
import com.example.safari.model.Routes
import com.example.safari.ui.components.toolbar.BrowsingToolbar
import com.example.safari.ui.components.toolbar.SafariToolbar
import com.example.safari.ui.theme.*

// ── Screen State ──────────────────────────────────────────────────────────────

enum class BrowserOverlay {
    None, Search, Tabs, Bookmarks, History, ContextMenu, Customize, Private,
    Share, AddToHomeScreen, Highlights, Extensions
}

// ── Main Browser Screen ───────────────────────────────────────────────────────

@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var overlay by remember { mutableStateOf(BrowserOverlay.None) }
    val isShowingPage = state.currentUrl.isNotEmpty()

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Swipe left = forward, right = back
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 80 && state.canGoBack) {
                        viewModel.handleNavigation(NavigationEvent.GoBack)
                    } else if (dragAmount < -80 && state.canGoForward) {
                        viewModel.handleNavigation(NavigationEvent.GoForward)
                    }
                }
            }
    ) {
        // ── WebView (always rendered, just hidden when on start page) ─────────
        if (isShowingPage) {
            SafariWebView(
                url = state.currentUrl,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp) // toolbar height
            )
        } else {
            // Start Page
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
                    .padding(bottom = 56.dp)
            )
        }

        // ── Bottom Toolbar ─────────────────────────────────────────────────────
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            if (isShowingPage) {
                BrowsingToolbar(
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                )
            } else {
                SafariToolbar(
                    currentUrl = state.currentUrl,
                    displayUrl = state.displayUrl,
                    isLoading = state.isLoading,
                    canGoBack = state.canGoBack,
                    canGoForward = state.canGoForward,
                    isPrivateMode = state.isPrivateMode,
                    progress = state.progress,
                    onBack = { viewModel.handleNavigation(NavigationEvent.GoBack) },
                    onForward = { viewModel.handleNavigation(NavigationEvent.GoForward) },
                    onSearch = { q -> viewModel.loadUrl(q); overlay = BrowserOverlay.None },
                    onSearchFocus = { overlay = BrowserOverlay.Search },
                    onMore = { overlay = BrowserOverlay.ContextMenu },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
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
                onSearch = { q ->
                    viewModel.loadUrl(q)
                    overlay = BrowserOverlay.None
                },
                onDismiss = { overlay = BrowserOverlay.None },
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(
            visible = overlay == BrowserOverlay.Tabs,
            enter = fadeIn() + scaleIn(initialScale = 0.95f),
            exit = fadeOut() + scaleOut(targetScale = 0.95f)
        ) {
            TabSwitcherScreen(
                tabs = state.tabs,
                activeTabIndex = state.activeTabIndex,
                isPrivateMode = state.isPrivateMode,
                onTabSelect = { idx ->
                    viewModel.handleNavigation(NavigationEvent.SwitchTab(idx))
                    overlay = BrowserOverlay.None
                },
                onTabClose = { id ->
                    viewModel.handleNavigation(NavigationEvent.CloseTab(id))
                },
                onNewTab = { isPrivate ->
                    viewModel.handleNavigation(NavigationEvent.NewTab(isPrivate))
                    overlay = BrowserOverlay.None
                },
                onDone = { overlay = BrowserOverlay.None },
                onSwitchMode = {
                    viewModel.togglePrivateMode()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(
            visible = overlay == BrowserOverlay.ContextMenu,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            ContextMenuOverlay(
                isPrivateMode = state.isPrivateMode,
                onDismiss = { overlay = BrowserOverlay.None },
                onNewTab = {
                    viewModel.handleNavigation(NavigationEvent.NewTab(false))
                    overlay = BrowserOverlay.None
                },
                onNewPrivateTab = {
                    viewModel.handleNavigation(NavigationEvent.NewTab(true))
                    overlay = BrowserOverlay.None
                },
                onBookmarks = { overlay = BrowserOverlay.Bookmarks },
                onAllTabs = { overlay = BrowserOverlay.Tabs }
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
                onBookmarkClick = { bm ->
                    viewModel.loadUrl(bm.url)
                    overlay = BrowserOverlay.None
                },
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
                onTurnOn = {
                    viewModel.togglePrivateMode()
                    overlay = BrowserOverlay.None
                },
                onNotNow = { overlay = BrowserOverlay.None },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
