package com.example.safari.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

// ── WebView Composable ────────────────────────────────────────────────────────

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SafariWebView(
    url: String,
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    var webView by remember { mutableStateOf<WebView?>(null) }

    // Wire up ViewModel callbacks to actual WebView calls
    DisposableEffect(webView) {
        val wv = webView ?: return@DisposableEffect onDispose {}
        viewModel.onLoadUrl = { u -> wv.loadUrl(u) }
        viewModel.onGoBack = { if (wv.canGoBack()) wv.goBack() }
        viewModel.onGoForward = { if (wv.canGoForward()) wv.goForward() }
        viewModel.onReload = { wv.reload() }
        viewModel.onStop = { wv.stopLoading() }
        onDispose {
            viewModel.onLoadUrl = null
            viewModel.onGoBack = null
            viewModel.onGoForward = null
            viewModel.onReload = null
            viewModel.onStop = null
        }
    }

    // Load URL when it changes externally
    LaunchedEffect(url) {
        if (url.isNotEmpty()) {
            webView?.loadUrl(url)
        }
    }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    allowFileAccess = true
                    databaseEnabled = true
                    cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                    mediaPlaybackRequiresUserGesture = false
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        viewModel.onPageStarted(url)
                        viewModel.onBackForwardStateChanged(view.canGoBack(), view.canGoForward())
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)
                        viewModel.onPageFinished(url, view.title ?: "")
                        viewModel.onBackForwardStateChanged(view.canGoBack(), view.canGoForward())
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean {
                        return false // Let WebView handle all navigation
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        viewModel.onProgressChanged(newProgress)
                    }

                    override fun onReceivedTitle(view: WebView, title: String) {
                        viewModel.onReceivedTitle(title)
                        viewModel.onBackForwardStateChanged(view.canGoBack(), view.canGoForward())
                    }
                }

                webView = this

                // Load initial URL
                if (url.isNotEmpty()) {
                    loadUrl(url)
                }
            }
        },
        update = { wv ->
            webView = wv
        },
        modifier = modifier
    )
}

// ── WebView State holder (prevent recreation) ─────────────────────────────────

class WebViewState {
    var webView: WebView? = null
    var lastUrl: String = ""
}

@Composable
fun rememberWebViewState(): WebViewState {
    return remember { WebViewState() }
}
