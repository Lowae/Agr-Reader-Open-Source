package com.lowae.agrreader.data.model.preference

import android.content.Context
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.theme.SystemTypography
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.ExternalFonts
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class BasicFontsPreference(val value: Int) : Preference() {
    object System : BasicFontsPreference(0)
    object External : BasicFontsPreference(5)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(DataStoreKeys.BasicFonts, value)
        }
    }

    fun toDesc(context: Context): String =
        when (this) {
            System -> context.getString(R.string.system_default)
            External -> context.getString(R.string.external_fonts)
        }

    fun asFontFamily(context: Context): FontFamily =
        when (this) {
            System -> FontFamily.Default
            External -> ExternalFonts.loadBasicTypography(context).displayLarge.fontFamily ?: FontFamily.Default
        }

    fun asTypography(context: Context): Typography =
        when (this) {
            System -> SystemTypography
            External -> ExternalFonts.loadBasicTypography(context)
        }

    companion object {

        val default = System
        val values = listOf(System, External)

        fun fromPreferences(preferences: Preferences): BasicFontsPreference =
            when (preferences[DataStoreKeys.BasicFonts.key]) {
                0 -> System
                5 -> External
                else -> default
            }
    }
}
