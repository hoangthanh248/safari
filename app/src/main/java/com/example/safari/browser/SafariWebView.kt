package com.example.safari.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.webkit.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

// All WebView callbacks fire on the main thread in AOSP, but some OEM ROMs
// dispatch onProgressChanged from a binder thread. We post everything to main
// explicitly to be safe and avoid "Only the original thread that created a view
// hierarchy can touch its views." crashes.
private val mainHandler = Handler(Looper.getMainLooper())
private inline fun onMain(crossinline block: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) block()
    else mainHandler.post { block() }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SafariWebView(
    url: String,
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    // Keep a stable reference so DisposableEffect fires only when the WebView
    // instance itself changes, not on every recomposition.
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    // Wire ViewModel ↔ WebView bridge
    DisposableEffect(webViewRef.value) {
        val wv = webViewRef.value ?: return@DisposableEffect onDispose {}
        viewModel.onLoadUrl   = { u -> onMain { wv.loadUrl(u) } }
        viewModel.onGoBack    = { onMain { if (wv.canGoBack()) wv.goBack() } }
        viewModel.onGoForward = { onMain { if (wv.canGoForward()) wv.goForward() } }
        viewModel.onReload    = { onMain { wv.reload() } }
        viewModel.onStop      = { onMain { wv.stopLoading() } }
        onDispose {
            viewModel.onLoadUrl   = null
            viewModel.onGoBack    = null
            viewModel.onGoForward = null
            viewModel.onReload    = null
            viewModel.onStop      = null
        }
    }

    AndroidView(
        factory = { context ->
            WebView(context).also { wv ->
                wv.settings.apply {
                    javaScriptEnabled        = true
                    domStorageEnabled        = true
                    loadWithOverviewMode     = true
                    useWideViewPort          = true
                    setSupportZoom(true)
                    builtInZoomControls      = true
                    displayZoomControls      = false
                    allowFileAccess          = false          // security: no file:// access
                    databaseEnabled          = true
                    cacheMode                = WebSettings.LOAD_DEFAULT
                    mediaPlaybackRequiresUserGesture = true  // security: don't autoplay
                    mixedContentMode         = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    @Suppress("DEPRECATION")
                    saveFormData             = false         // privacy
                }

                wv.webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        if (!url.isNullOrEmpty()) {
                            onMain {
                                viewModel.onPageStarted(url)
                                viewModel.onBackForwardStateChanged(view.canGoBack(), view.canGoForward())
                            }
                        }
                    }

                    override fun onPageFinished(view: WebView, url: String?) {
                        super.onPageFinished(view, url)
                        if (!url.isNullOrEmpty()) {
                            onMain {
                                viewModel.onPageFinished(url, view.title ?: "")
                                viewModel.onBackForwardStateChanged(view.canGoBack(), view.canGoForward())
                            }
                        }
                    }

                    // Never override loading — let WebView handle all navigation
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean = false

                    override fun onReceivedError(
                        view: WebView,
                        request: WebResourceRequest,
                        error: WebResourceError
                    ) {
                        // Only surface main-frame errors to avoid noise from sub-resources
                        if (request.isForMainFrame) {
                            onMain {
                                viewModel.onPageFinished(
                                    request.url?.toString() ?: "",
                                    "Error loading page"
                                )
                            }
                        }
                    }
                }

                wv.webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        onMain { viewModel.onProgressChanged(newProgress) }
                    }
                    override fun onReceivedTitle(view: WebView, title: String?) {
                        if (!title.isNullOrEmpty()) {
                            onMain {
                                viewModel.onReceivedTitle(title)
                                viewModel.onBackForwardStateChanged(view.canGoBack(), view.canGoForward())
                            }
                        }
                    }
                }

                // Register instance
                webViewRef.value = wv

                // Load initial URL if provided
                if (url.isNotEmpty()) wv.loadUrl(url)
            }
        },
        update = { wv ->
            // AndroidView calls update() on every recomposition if the factory
            // already ran. Do NOT call loadUrl() here — the ViewModel bridge
            // handles URL changes via onLoadUrl callback.
            if (webViewRef.value !== wv) webViewRef.value = wv
        },
        onRelease = { wv ->
            // Prevent WebView from leaking when the composable leaves composition
            wv.stopLoading()
            wv.destroy()
        },
        modifier = modifier
    )
}
