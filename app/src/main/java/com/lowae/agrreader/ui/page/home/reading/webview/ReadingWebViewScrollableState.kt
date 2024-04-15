package com.lowae.agrreader.ui.page.home.reading.webview

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

class ReadingWebViewScrollableState(private val webView: ReadingWebView) : ScrollableState {

    /**
     * current scroll position value in pixels
     */
    var value: Int by mutableIntStateOf(webView.scrollY)
        private set

    /**
     * maximum bound for [value], or [Int.MAX_VALUE] if still unknown
     */
    val maxValue: Int = 0
        get() = if (field == 0) webView.verticalScrollRange else field

    private var accumulator: Float = 0f

    private val scrollableState = ScrollableState {
        val delta = -it
        val absolute = (value + delta + accumulator)
        val newValue = absolute.coerceIn(0f, maxValue.toFloat())
        val changed = absolute != newValue
        val consumed = newValue - value
        val consumedInt = consumed.roundToInt()
        value += consumedInt
        accumulator = consumed - consumedInt

        // Avoid floating-point rounding error
        if (changed) consumed else it
    }

    override val isScrollInProgress: Boolean
        get() = scrollableState.isScrollInProgress

    override val canScrollForward: Boolean
        get() = value < maxValue

    override val canScrollBackward: Boolean
        get() = value > 0

    override fun dispatchRawDelta(delta: Float) = scrollableState.dispatchRawDelta(delta)

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) = scrollableState.scroll(scrollPriority, block)
}