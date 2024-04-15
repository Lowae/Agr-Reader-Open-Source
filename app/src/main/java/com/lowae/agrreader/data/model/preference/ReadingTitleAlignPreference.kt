package com.lowae.agrreader.data.model.preference

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.style.TextAlign
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.R
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ReadingTitleAlignPreference(val value: Int) : Preference() {
    object Left : ReadingTitleAlignPreference(0)
    object Right : ReadingTitleAlignPreference(1)
    object Center : ReadingTitleAlignPreference(2)
    object Justify : ReadingTitleAlignPreference(3)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(DataStoreKeys.ReadingTitleAlign, value)
        }
    }

    @Stable
    fun toDesc(context: Context): String =
        when (this) {
            Left -> context.getString(R.string.align_left)
            Right -> context.getString(R.string.align_right)
            Center -> context.getString(R.string.center_text)
            Justify -> context.getString(R.string.justify)
        }

    @Stable
    fun toTextAlign(): TextAlign =
        when (this) {
            Left -> TextAlign.Start
            Right -> TextAlign.End
            Center -> TextAlign.Center
            Justify -> TextAlign.Justify
        }

    companion object {

        val default = Left
        val values = listOf(Left, Right, Center, Justify)

        fun fromPreferences(preferences: Preferences): ReadingTitleAlignPreference =
            when (preferences[DataStoreKeys.ReadingTitleAlign.key]) {
                0 -> Left
                1 -> Right
                2 -> Center
                3 -> Justify
                else -> default
            }
    }
}
