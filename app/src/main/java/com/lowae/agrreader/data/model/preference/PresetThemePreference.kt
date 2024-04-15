package com.lowae.agrreader.data.model.preference

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.ui.theme.palette.dynamic.PresetColors
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ThemeIndexPreference {


    val default = PresetColors.GREEN

    fun put(scope: CoroutineScope, preset: PresetColors) {
        scope.launch(Dispatchers.IO) {
            DataStore.put(DataStoreKeys.ThemeIndex, preset.toString())
        }
    }

    fun fromPreferences(preferences: Preferences): PresetColors =
        preferences[DataStoreKeys.ThemeIndex.key]
            ?.runCatching { PresetColors.valueOf(this) }
            ?.getOrNull() ?: default
}

object CustomPrimaryColorPreference {

    val DEFAULT = PresetColors.CUSTOM.color

    fun put(scope: CoroutineScope, @ColorInt colorInt: Int) {
        scope.launch {
            DataStore.put(DataStoreKeys.PrimaryColor, colorInt)
        }
    }

    fun fromPreferences(preferences: Preferences): Color =
        preferences[DataStoreKeys.PrimaryColor.key]
            ?.runCatching { Color(this) }
            ?.getOrNull() ?: DEFAULT
}
