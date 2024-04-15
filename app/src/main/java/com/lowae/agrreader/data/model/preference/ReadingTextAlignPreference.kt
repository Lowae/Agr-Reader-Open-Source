package com.lowae.agrreader.data.model.preference

import android.content.Context
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.R
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ReadingTextAlignPreference(val value: Int) : Preference() {
    object Left : ReadingTextAlignPreference(0)
    object Right : ReadingTextAlignPreference(1)
    object Center : ReadingTextAlignPreference(2)
    object Justify : ReadingTextAlignPreference(3)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(
                DataStoreKeys.ReadingTextAlign,
                value
            )
        }
    }

    fun toDesc(context: Context): String =
        when (this) {
            Left -> context.getString(R.string.align_left)
            Right -> context.getString(R.string.align_right)
            Center -> context.getString(R.string.center_text)
            Justify -> context.getString(R.string.justify)
        }

    fun toTextAlign(): TextAlign =
        when (this) {
            Left -> TextAlign.Start
            Right -> TextAlign.End
            Center -> TextAlign.Center
            Justify -> TextAlign.Justify
        }
    fun toAlignment(): Alignment.Horizontal =
        when (this) {
            Left -> Alignment.Start
            Right -> Alignment.End
            Center -> Alignment.CenterHorizontally
            Justify -> Alignment.Start
        }

    companion object {

        val default = Left
        val values = listOf(Left, Right, Center, Justify)

        fun put(scope: CoroutineScope, textAlign: TextAlign) {
            val preference = when (textAlign) {
                TextAlign.End -> Right
                TextAlign.Center -> Center
                TextAlign.Justify -> Justify
                else -> Left
            }
            preference.put(scope)
        }


        fun fromPreferences(preferences: Preferences): ReadingTextAlignPreference =
            when (preferences[DataStoreKeys.ReadingTextAlign.key]) {
                0 -> Left
                1 -> Right
                2 -> Center
                3 -> Justify
                else -> default
            }
    }
}
