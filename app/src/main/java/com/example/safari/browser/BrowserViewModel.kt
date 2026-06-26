package com.example.safari.browser

import androidx.lifecycle.ViewModel
import com.example.safari.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BrowserViewModel : ViewModel() {

    private val _state = MutableStateFlow(BrowserState())
    val state: StateFlow<BrowserState> = _state.asStateFlow()

    // WebView bridge — set by SafariWebView, cleared on dispose
    var onLoadUrl:    ((String) -> Unit)? = null
    var onGoBack:     (() -> Unit)?       = null
    var onGoForward:  (() -> Unit)?       = null
    var onReload:     (() -> Unit)?       = null
    var onStop:       (() -> Unit)?       = null

    // ── Navigation ────────────────────────────────────────────────────────────

    fun handleNavigation(event: NavigationEvent) {
        when (event) {
            is NavigationEvent.LoadUrl    -> loadUrl(event.url)
            is NavigationEvent.GoBack     -> onGoBack?.invoke()
            is NavigationEvent.GoForward  -> onGoForward?.invoke()
            is NavigationEvent.Reload     -> onReload?.invoke()
            is NavigationEvent.Stop       -> onStop?.invoke()
            is NavigationEvent.NewTab     -> openNewTab(event.isPrivate)
            is NavigationEvent.CloseTab   -> closeTab(event.tabId)
            is NavigationEvent.SwitchTab  -> switchTab(event.tabIndex)
        }
    }

    fun loadUrl(input: String) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return          // FIX: never load empty string
        val url = normalizeUrl(trimmed)
        updateCurrentTab { copy(url = url) }
        onLoadUrl?.invoke(url)
        _state.update { it.copy(currentUrl = url, isLoading = true, progress = 0f) }
    }

    private fun normalizeUrl(input: String): String {
        return when {
            input.startsWith("http://") || input.startsWith("https://") -> input
            // has a dot and no spaces → treat as domain
            input.contains(".") && !input.contains(" ") -> "https://$input"
            // otherwise → google search
            else -> "https://www.google.com/search?q=${input.replace(" ", "+")}"
        }
    }

    // ── WebView callbacks (called from main thread by WebViewClient) ──────────

    fun onPageStarted(url: String) {
        if (url.isEmpty()) return
        _state.update { it.copy(currentUrl = url, isLoading = true, progress = 0.1f) }
        updateCurrentTab { copy(url = url) }
    }

    fun onPageFinished(url: String, title: String) {
        if (url.isEmpty()) return
        val domain = extractDomain(url)
        val safeTitle = title.ifEmpty { domain }
        _state.update { it.copy(
            currentUrl   = url,
            displayUrl   = domain,
            title        = safeTitle,
            isLoading    = false,
            progress     = 1f
        )}
        updateCurrentTab { copy(url = url, title = safeTitle) }
        if (!_state.value.isPrivateMode) addToHistory(url, safeTitle)
    }

    fun onProgressChanged(progress: Int) {
        val clamped = progress.coerceIn(0, 100) / 100f
        _state.update { it.copy(progress = clamped) }
    }

    fun onReceivedTitle(title: String) {
        if (title.isEmpty()) return
        _state.update { it.copy(title = title) }
        updateCurrentTab { copy(title = title) }
    }

    fun onBackForwardStateChanged(canGoBack: Boolean, canGoForward: Boolean) {
        _state.update { it.copy(canGoBack = canGoBack, canGoForward = canGoForward) }
    }

    // ── Tab management ────────────────────────────────────────────────────────

    fun openNewTab(isPrivate: Boolean = false) {
        val newTab = BrowserTab(isPrivate = isPrivate)
        val tabs   = _state.value.tabs + newTab
        _state.update { it.copy(
            tabs           = tabs,
            activeTabIndex = tabs.size - 1,
            isPrivateMode  = isPrivate,
            currentUrl     = "",
            displayUrl     = "",
            title          = "New Tab",
            canGoBack      = false,
            canGoForward   = false,
            isLoading      = false,
            progress       = 0f
        )}
    }

    fun closeTab(tabId: String) {
        val tabs = _state.value.tabs.toMutableList()
        val idx  = tabs.indexOfFirst { it.id == tabId }
        if (idx < 0) return
        tabs.removeAt(idx)
        if (tabs.isEmpty()) tabs.add(BrowserTab())   // always keep ≥1 tab
        val newIndex = (idx - 1).coerceAtLeast(0).coerceAtMost(tabs.size - 1)
        val activeTab = tabs[newIndex]
        _state.update { it.copy(
            tabs           = tabs,
            activeTabIndex = newIndex,
            currentUrl     = activeTab.url,
            displayUrl     = extractDomain(activeTab.url),
            title          = activeTab.title,
            isPrivateMode  = activeTab.isPrivate
        )}
        if (activeTab.url.isNotEmpty()) onLoadUrl?.invoke(activeTab.url)
    }

    fun switchTab(index: Int) {
        val tabs = _state.value.tabs
        if (index !in tabs.indices) return
        val tab = tabs[index]
        _state.update { it.copy(
            activeTabIndex = index,
            currentUrl     = tab.url,
            displayUrl     = extractDomain(tab.url),
            title          = tab.title,
            isPrivateMode  = tab.isPrivate,
            canGoBack      = false,
            canGoForward   = false
        )}
        if (tab.url.isNotEmpty()) onLoadUrl?.invoke(tab.url)
    }

    private fun updateCurrentTab(transform: BrowserTab.() -> BrowserTab) {
        val tabs = _state.value.tabs.toMutableList()
        val idx  = _state.value.activeTabIndex
        if (idx in tabs.indices) {
            tabs[idx] = tabs[idx].transform()
            _state.update { it.copy(tabs = tabs) }
        }
    }

    // ── History ───────────────────────────────────────────────────────────────

    private fun addToHistory(url: String, title: String) {
        if (url.isEmpty() || url.startsWith("about:")) return
        val item = HistoryItem(url = url, title = title.ifEmpty { url })
        _state.update { it.copy(history = (listOf(item) + it.history).take(500)) }
    }

    fun clearHistory() {
        _state.update { it.copy(history = emptyList()) }
    }

    // ── Bookmarks & Favorites ─────────────────────────────────────────────────

    fun addBookmark(url: String, title: String) {
        if (url.isEmpty()) return
        val bookmark = Bookmark(url = url, title = title.ifEmpty { url })
        _state.update { it.copy(bookmarks = it.bookmarks + bookmark) }
    }

    fun removeBookmark(id: String) {
        _state.update { it.copy(bookmarks = it.bookmarks.filter { b -> b.id != id }) }
    }

    // ── Private Mode ──────────────────────────────────────────────────────────

    fun togglePrivateMode() {
        openNewTab(!_state.value.isPrivateMode)
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private fun extractDomain(url: String): String {
        if (url.isEmpty()) return ""
        return try {
            url.removePrefix("https://")
               .removePrefix("http://")
               .removePrefix("www.")
               .substringBefore("/")
               .substringBefore("?")
               .substringBefore("#")
               .trim()
        } catch (_: Exception) { url }
    }

    val activeTab: BrowserTab?
        get() = state.value.tabs.getOrNull(state.value.activeTabIndex)
}
