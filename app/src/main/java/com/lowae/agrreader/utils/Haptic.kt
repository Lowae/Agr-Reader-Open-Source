package com.lowae.agrreader.utils

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.runtime.Stable
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.lowae.agrreader.data.model.preference.ClickVibrationPreference
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.get

@Stable
val HapticTapType = HapticFeedbackType(HapticFeedbackConstants.KEYBOARD_TAP)

private val CurrentHapticEnable: Boolean =
    DataStore.get(DataStoreKeys.ClickVibration)
        .let { if (it == null) ClickVibrationPreference.default.value else it == ClickVibrationPreference.ON.value }

fun HapticFeedback.tap() = performHapticFeedback(HapticTapType)

fun View.tap(isSound: Boolean = true) {
    if (CurrentHapticEnable.not()) return
    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    if (isSound) {
        playSoundEffect(SoundEffectConstants.CLICK)
    }
}