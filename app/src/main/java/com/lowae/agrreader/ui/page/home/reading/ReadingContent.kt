package com.lowae.agrreader.ui.page.home.reading

import android.view.ViewGroup
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.lowae.agrreader.ui.page.home.reading.webview.ReadingWebView
import com.lowae.agrreader.ui.page.home.reading.webview.ReadingWebViewScrollableState
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlin.math.abs

private const val MIN_SCROLL_THRESHOLD = 3

@Composable
fun ReadingContent(
    modifier: Modifier,
    scrollState: ReadingWebViewScrollableState,
    webView: ReadingWebView,
    isScrollingUp: (Boolean) -> Unit
) {
    AndroidView(
        modifier = modifier.scrollable(
            scrollState,
            Orientation.Vertical
        ),
        factory = {
            if (webView.parent != null) {
                (webView.parent as ViewGroup).removeView(webView)
            }
            webView
        },
    )

    LaunchedEffect(webView) {
        var previousScroll = scrollState.value
        snapshotFlow { scrollState.value }
            .onEach {
                webView.scrollTo(0, it)
            }
            .filter { scrollState.isScrollInProgress && abs(it - previousScroll) >= MIN_SCROLL_THRESHOLD }
            .collect { scroll ->
                isScrollingUp((scroll - previousScroll) <= 0)
                previousScroll = scroll
            }
    }
}

