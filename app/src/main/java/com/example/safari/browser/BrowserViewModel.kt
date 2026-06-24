package com.example.safari.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safari.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrowserViewModel : ViewModel() {

    private val _state = MutableStateFlow(BrowserState())
    val state: StateFlow<BrowserState> = _state.asStateFlow()

    // WebView interaction (set externally)
    var onLoadUrl: ((String) -> Unit)? = null
    var onGoBack: (() -> Unit)? = null
    var onGoForward: (() -> Unit)? = null
    var onReload: (() -> Unit)? = null
    var onStop: (() -> Unit)? = null

    // ── Navigation ────────────────────────────────────────────────────────────

    fun handleNavigation(event: NavigationEvent) {
        when (event) {
            is NavigationEvent.LoadUrl -> loadUrl(event.url)
            is NavigationEvent.GoBack -> onGoBack?.invoke()
            is NavigationEvent.GoForward -> onGoForward?.invoke()
            is NavigationEvent.Reload -> onReload?.invoke()
            is NavigationEvent.Stop -> onStop?.invoke()
            is NavigationEvent.NewTab -> openNewTab(event.isPrivate)
            is NavigationEvent.CloseTab -> closeTab(event.tabId)
            is NavigationEvent.SwitchTab -> switchTab(event.tabIndex)
        }
    }

    fun loadUrl(input: String) {
        val url = normalizeUrl(input)
        updateCurrentTab { copy(url = url) }
        onLoadUrl?.invoke(url)
        _state.update { it.copy(currentUrl = url, isLoading = true, progress = 0f) }
    }

    private fun normalizeUrl(input: String): String {
        val trimmed = input.trim()
        return when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> "https://www.google.com/search?q=${trimmed.replace(" ", "+")}"
        }
    }

    // ── WebView Callbacks ─────────────────────────────────────────────────────

    fun onPageStarted(url: String) {
        _state.update { it.copy(currentUrl = url, isLoading = true, progress = 0.1f) }
        updateCurrentTab { copy(url = url) }
    }

    fun onPageFinished(url: String, title: String) {
        _state.update { it.copy(
            currentUrl = url,
            displayUrl = extractDomain(url),
            title = title,
            isLoading = false,
            progress = 1f
        )}
        updateCurrentTab { copy(url = url, title = title.ifEmpty { url }) }
        // Add to history (normal tabs only)
        if (!_state.value.isPrivateMode) {
            addToHistory(url, title)
        }
    }

    fun onProgressChanged(progress: Int) {
        _state.update { it.copy(progress = progress / 100f) }
    }

    fun onReceivedTitle(title: String) {
        _state.update { it.copy(title = title) }
        updateCurrentTab { copy(title = title) }
    }

    fun onBackForwardStateChanged(canGoBack: Boolean, canGoForward: Boolean) {
        _state.update { it.copy(canGoBack = canGoBack, canGoForward = canGoForward) }
    }

    // ── Tab Management ────────────────────────────────────────────────────────

    fun openNewTab(isPrivate: Boolean = false) {
        val newTab = BrowserTab(isPrivate = isPrivate)
        val tabs = _state.value.tabs + newTab
        _state.update { it.copy(
            tabs = tabs,
            activeTabIndex = tabs.size - 1,
            isPrivateMode = isPrivate,
            currentUrl = "",
            title = "New Tab",
            canGoBack = false,
            canGoForward = false
        )}
    }

    fun closeTab(tabId: String) {
        val tabs = _state.value.tabs.toMutableList()
        val idx = tabs.indexOfFirst { it.id == tabId }
        if (idx < 0) return
        tabs.removeAt(idx)
        if (tabs.isEmpty()) {
            tabs.add(BrowserTab())
        }
        val newIndex = minOf(idx, tabs.size - 1)
        _state.update { it.copy(
            tabs = tabs,
            activeTabIndex = newIndex
        )}
    }

    fun switchTab(index: Int) {
        if (index < 0 || index >= _state.value.tabs.size) return
        val tab = _state.value.tabs[index]
        _state.update { it.copy(
            activeTabIndex = index,
            currentUrl = tab.url,
            title = tab.title,
            isPrivateMode = tab.isPrivate
        )}
        if (tab.url.isNotEmpty()) {
            onLoadUrl?.invoke(tab.url)
        }
    }

    private fun updateCurrentTab(transform: BrowserTab.() -> BrowserTab) {
        val tabs = _state.value.tabs.toMutableList()
        val idx = _state.value.activeTabIndex
        if (idx < tabs.size) {
            tabs[idx] = tabs[idx].transform()
            _state.update { it.copy(tabs = tabs) }
        }
    }

    // ── History ───────────────────────────────────────────────────────────────

    private fun addToHistory(url: String, title: String) {
        if (url.isEmpty()) return
        val item = HistoryItem(url = url, title = title.ifEmpty { url })
        _state.update { it.copy(
            history = (listOf(item) + it.history).take(200)
        )}
    }

    // ── Bookmarks & Favorites ─────────────────────────────────────────────────

    fun addBookmark(url: String, title: String) {
        val bookmark = Bookmark(url = url, title = title)
        _state.update { it.copy(bookmarks = it.bookmarks + bookmark) }
    }

    fun removeBookmark(id: String) {
        _state.update { it.copy(bookmarks = it.bookmarks.filter { b -> b.id != id }) }
    }

    // ── Private Mode ──────────────────────────────────────────────────────────

    fun togglePrivateMode() {
        val isPrivate = !_state.value.isPrivateMode
        openNewTab(isPrivate)
    }

    fun setPrivateMode(enabled: Boolean) {
        _state.update { it.copy(isPrivateMode = enabled) }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private fun extractDomain(url: String): String {
        return try {
            url.removePrefix("https://")
                .removePrefix("http://")
                .removePrefix("www.")
                .split("/").first()
                .split("?").first()
        } catch (e: Exception) { url }
    }

    val activeTab: BrowserTab?
        get() = state.value.tabs.getOrNull(state.value.activeTabIndex)
}
