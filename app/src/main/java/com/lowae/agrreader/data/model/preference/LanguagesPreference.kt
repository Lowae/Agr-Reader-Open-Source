package com.lowae.agrreader.data.model.preference

import android.content.Context
import android.os.LocaleList
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.R
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

sealed class LanguagesPreference(val value: Int) : Preference() {
    object UseDeviceLanguages : LanguagesPreference(0)
    object English : LanguagesPreference(1)
    object ChineseSimplified : LanguagesPreference(2)
    object German : LanguagesPreference(3)
    object French : LanguagesPreference(4)
    object Czech : LanguagesPreference(5)
    object Italian : LanguagesPreference(6)
    object Hindi : LanguagesPreference(7)
    object Spanish : LanguagesPreference(8)
    object Polish : LanguagesPreference(9)
    object Russian : LanguagesPreference(10)
    object Basque : LanguagesPreference(11)
    object Indonesian : LanguagesPreference(12)

    object ChineseTraditional : LanguagesPreference(13)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(
                DataStoreKeys.Languages,
                value
            )
            setLocale(AgrReaderApp.application)
        }
    }

    fun toDesc(context: Context): String =
        when (this) {
            UseDeviceLanguages -> context.getString(R.string.use_device_languages)
            English -> context.getString(R.string.english)
            ChineseSimplified -> context.getString(R.string.chinese_simplified)
            German -> context.getString(R.string.german)
            French -> context.getString(R.string.french)
            Czech -> context.getString(R.string.czech)
            Italian -> context.getString(R.string.italian)
            Hindi -> context.getString(R.string.hindi)
            Spanish -> context.getString(R.string.spanish)
            Polish -> context.getString(R.string.polish)
            Russian -> context.getString(R.string.russian)
            Basque -> context.getString(R.string.basque)
            Indonesian -> context.getString(R.string.indonesian)
            ChineseTraditional -> context.getString(R.string.chinese_traditional)
        }

    fun getLocale(): Locale =
        when (this) {
            UseDeviceLanguages -> LocaleList.getDefault().get(0)
            English -> Locale("en", "US")
            ChineseSimplified -> Locale("zh", "CN")
            German -> Locale("de", "DE")
            French -> Locale("fr", "FR")
            Czech -> Locale("cs", "CZ")
            Italian -> Locale("it", "IT")
            Hindi -> Locale("hi", "IN")
            Spanish -> Locale("es", "ES")
            Polish -> Locale("pl", "PL")
            Russian -> Locale("ru", "RU")
            Basque -> Locale("eu", "ES")
            Indonesian -> Locale("in", "ID")
            ChineseTraditional -> Locale("zh", "TW")
        }

    fun setLocale(context: Context) {
        val locale = getLocale()
        val resources = context.resources
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))
        context.createConfigurationContext(configuration)
        resources.updateConfiguration(configuration, metrics)

        val appResources = context.applicationContext.resources
        val appMetrics = appResources.displayMetrics
        val appConfiguration = appResources.configuration
        appConfiguration.setLocale(locale)
        appConfiguration.setLocales(LocaleList(locale))
        context.applicationContext.createConfigurationContext(appConfiguration)
        appResources.updateConfiguration(appConfiguration, appMetrics)
    }

    companion object {

        val default = UseDeviceLanguages
        val values = listOf(UseDeviceLanguages,
            English,
            ChineseSimplified,
            German,
            French,
            Czech,
            Italian,
            Hindi,
            Spanish,
            Polish,
            Russian,
            Basque,
            Indonesian,
            ChineseTraditional,
        )

        fun fromPreferences(preferences: Preferences): LanguagesPreference =
            when (preferences[DataStoreKeys.Languages.key]) {
                0 -> UseDeviceLanguages
                1 -> English
                2 -> ChineseSimplified
                3 -> German
                4 -> French
                5 -> Czech
                6 -> Italian
                7 -> Hindi
                8 -> Spanish
                9 -> Polish
                10 -> Russian
                11 -> Basque 
                12 -> Indonesian
                13 -> ChineseTraditional
                else -> default
            }

        fun fromValue(value: Int): LanguagesPreference =
            when (value) {
                0 -> UseDeviceLanguages
                1 -> English
                2 -> ChineseSimplified
                3 -> German
                4 -> French
                5 -> Czech
                6 -> Italian
                7 -> Hindi
                8 -> Spanish
                9 -> Polish
                10 -> Russian
                11 -> Basque 
                12 -> Indonesian
                13 -> ChineseTraditional
                else -> default
            }
    }
}
