package com.goldmine.uncc.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors

/**
 * Hardened WebView wrapper — the Android counterpart of the iOS `WKWebView` bridge.
 *
 * Adds a load indicator, hardware back-button history navigation and a branded offline state,
 * none of which the iOS version had.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GoldMineWebView(
    url: String,
    modifier: Modifier = Modifier,
    zoomScale: Float = 1f,
    onCannotGoBack: (() -> Unit)? = null,
) {
    val extras = LocalGoldMineColors.current

    var progress by remember { mutableFloatStateOf(0f) }
    var isLoading by remember { mutableStateOf(true) }
    var failed by remember { mutableStateOf(false) }
    var webView by remember { mutableStateOf<WebView?>(null) }

    BackHandler(enabled = onCannotGoBack != null) {
        val view = webView
        if (view != null && view.canGoBack()) view.goBack() else onCannotGoBack?.invoke()
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                // Keep the native view out of the way (and un-clickable) while the
                // branded error state is showing.
                .alpha(if (failed) 0f else 1f),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        mediaPlaybackRequiresUserGesture = false
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            progress = newProgress / 100f
                        }
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                            if (zoomScale != 1f) {
                                view?.evaluateJavascript(zoomScript(zoomScale), null)
                            }
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?,
                        ) {
                            // Sub-resource failures are ignored; only a failed main frame
                            // should surface the offline state.
                            if (request?.isForMainFrame == true) {
                                isLoading = false
                                failed = true
                            }
                        }
                    }
                    loadUrl(url)
                    webView = this
                }
            },
            update = { view ->
                webView = view
                if (view.url == null) view.loadUrl(url)
            },
            onRelease = { view ->
                view.stopLoading()
                view.destroy()
                webView = null
            },
        )

        if (isLoading && progress < 1f && !failed) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
                color = GoldMineColors.CharlotteGreen,
            )
        }

        if (failed) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(extras.screenBackground)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            ) {
                Icon(
                    imageVector = Icons.Filled.CloudOff,
                    contentDescription = null,
                    tint = extras.secondaryText,
                    modifier = Modifier.size(48.dp),
                )
                Text(
                    text = "Couldn't load this page",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Check your connection and try again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extras.secondaryText,
                    textAlign = TextAlign.Center,
                )
                Button(
                    onClick = {
                        failed = false
                        isLoading = true
                        progress = 0f
                        webView?.loadUrl(url)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldMineColors.CharlotteGreen,
                        contentColor = androidx.compose.ui.graphics.Color.White,
                    ),
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

/** Matches the `ZoomableWebView` script the iOS app injects for the UREC occupancy widget. */
private fun zoomScript(scale: Float): String = """
    document.body.style.zoom = '$scale';
    document.body.style.webkitTransform = 'scale($scale)';
    document.body.style.webkitTransformOrigin = '0 0';
""".trimIndent()
