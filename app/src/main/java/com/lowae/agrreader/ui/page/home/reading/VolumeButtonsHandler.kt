package com.lowae.agrreader.ui.page.home.reading

import android.view.KeyEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import com.lowae.agrreader.data.model.preference.LocalVolumePageScroll
import com.lowae.agrreader.data.model.preference.VolumePageScrollPreference

@Composable
fun VolumeButtonsPageScroller(
    key: Any?,
    enable: Boolean = true,
    onPageUp: (VolumePageScrollPreference) -> Unit,
    onPageDown: (VolumePageScrollPreference) -> Unit
) {
    val volumePageScroll = LocalVolumePageScroll.current
    if (enable.not() || volumePageScroll.isEnable.not()) return
    VolumeButtonsHandler(
        key,
        onVolumeUp = { onPageUp(volumePageScroll) },
        onVolumeDown = { onPageDown(volumePageScroll) }
    )
}

@Composable
private fun VolumeButtonsHandler(
    key: Any?,
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current

    DisposableEffect(key, context) {
        val keyEventDispatcher = ViewCompat.OnUnhandledKeyEventListenerCompat { _, event ->
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    if (event.action == KeyEvent.ACTION_UP) {
                        onVolumeUp()
                    }
                    true
                }

                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    if (event.action == KeyEvent.ACTION_UP) {
                        onVolumeDown()
                    }
                    true
                }

                else -> {
                    false
                }
            }
        }
        ViewCompat.addOnUnhandledKeyEventListener(view, keyEventDispatcher)

        onDispose {
            ViewCompat.removeOnUnhandledKeyEventListener(view, keyEventDispatcher)
        }
    }
}