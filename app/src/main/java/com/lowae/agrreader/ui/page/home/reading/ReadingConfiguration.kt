package com.lowae.agrreader.ui.page.home.reading

import androidx.compose.ui.text.style.TextAlign
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.data.model.preference.BasicFontsPreference
import com.lowae.agrreader.data.model.preference.ReadingLetterSpacingPreference
import com.lowae.agrreader.data.model.preference.ReadingLineHeightPreference
import com.lowae.agrreader.data.model.preference.ReadingTextAlignPreference
import com.lowae.agrreader.data.model.preference.ReadingTextFontSizePreference
import com.lowae.agrreader.data.model.preference.ReadingTextFontWeightPreference

data class ReadingConfiguration(
    val web: ReadingWebConfiguration = ReadingWebConfiguration(),
    val style: ReadingStylesConfiguration = ReadingStylesConfiguration()
) {
    companion object {
        fun fromPreference(preferences: Preferences): ReadingConfiguration {
            val style = ReadingStylesConfiguration(
                BasicFontsPreference.fromPreferences(preferences).value,
                ReadingTextAlignPreference.fromPreferences(preferences).toTextAlign(),
                ReadingTextFontSizePreference.fromPreferences(preferences),
                ReadingTextFontWeightPreference.fromPreferences(preferences),
                ReadingLetterSpacingPreference.fromPreferences(preferences),
                ReadingLineHeightPreference.fromPreferences(preferences),
            )
            val web = ReadingWebConfiguration(BasicFontsPreference.fromPreferences(preferences),)
            return ReadingConfiguration(web, style)
        }
    }
}

data class ReadingStylesConfiguration(
    val font: Int = BasicFontsPreference.default.value,
    val textAlign: TextAlign = ReadingTextAlignPreference.default.toTextAlign(),
    val fontSize: Int = ReadingTextFontSizePreference.default,
    val fontWeight: Int = ReadingTextFontWeightPreference.default,
    val letterSpacing: Double = ReadingLetterSpacingPreference.default,
    val lineHeight: Double = ReadingLineHeightPreference.default
)

data class ReadingWebConfiguration(
    val basicFont: BasicFontsPreference = BasicFontsPreference.default,
)