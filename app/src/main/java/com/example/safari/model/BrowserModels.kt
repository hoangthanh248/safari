package com.example.safari.model

import androidx.compose.runtime.Stable
import java.util.UUID

// ── Tab Model ─────────────────────────────────────────────────────────────────

@Stable
data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val url: String = "",
    val title: String = "New Tab",
    val isPrivate: Boolean = false,
    val thumbnailPath: String? = null,
    val favicon: String? = null
)

// ── Browser State ─────────────────────────────────────────────────────────────

@Stable
data class BrowserState(
    val currentUrl: String = "",
    val displayUrl: String = "",
    val title: String = "",
    val progress: Float = 0f,
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isPrivateMode: Boolean = false,
    val tabs: List<BrowserTab> = listOf(BrowserTab()),
    val activeTabIndex: Int = 0,
    val history: List<HistoryItem> = emptyList(),
    val bookmarks: List<Bookmark> = emptyList(),
    val favorites: List<Favorite> = defaultFavorites()
)

fun defaultFavorites() = listOf(
    Favorite("Apple", "https://apple.com", "apple"),
    Favorite("Bing", "https://bing.com", "bing"),
    Favorite("Google", "https://google.com", "google"),
    Favorite("Yahoo", "https://yahoo.com", "yahoo")
)

// ── History ───────────────────────────────────────────────────────────────────

data class HistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String,
    val visitedAt: Long = System.currentTimeMillis()
)

// ── Bookmark ──────────────────────────────────────────────────────────────────

data class Bookmark(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String,
    val folder: String = "Bookmarks"
)

// ── Favorite ──────────────────────────────────────────────────────────────────

data class Favorite(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val iconKey: String = ""
)

// ── Navigation Events ─────────────────────────────────────────────────────────

sealed class NavigationEvent {
    data class LoadUrl(val url: String) : NavigationEvent()
    object GoBack : NavigationEvent()
    object GoForward : NavigationEvent()
    object Reload : NavigationEvent()
    object Stop : NavigationEvent()
    data class NewTab(val isPrivate: Boolean = false) : NavigationEvent()
    data class CloseTab(val tabId: String) : NavigationEvent()
    data class SwitchTab(val tabIndex: Int) : NavigationEvent()
}

// ── Screen Routes ─────────────────────────────────────────────────────────────

object Routes {
    const val BROWSER = "browser"
    const val TAB_SWITCHER = "tab_switcher"
    const val PRIVATE_MODE = "private_mode"
    const val BOOKMARKS = "bookmarks"
    const val HISTORY = "history"
    const val SEARCH = "search"
    const val CUSTOMIZE = "customize"
}
