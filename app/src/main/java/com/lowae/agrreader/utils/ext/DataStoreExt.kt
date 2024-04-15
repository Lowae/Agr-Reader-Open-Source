package com.lowae.agrreader.utils.ext

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.data.model.account.AccountType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException

val DataStore: DataStore<Preferences> by lazy {
    PreferenceDataStoreFactory.create(ReplaceFileCorruptionHandler {
        emptyPreferences()
    }) {
        AgrReaderApp.application.preferencesDataStoreFile("settings")
    }
}

val KvDataStore: DataStore<Preferences> by lazy {
    PreferenceDataStoreFactory.create(ReplaceFileCorruptionHandler {
        emptyPreferences()
    }) {
        AgrReaderApp.application.preferencesDataStoreFile("key-value")
    }
}

val IsFirstLaunch: Boolean
    get() = DataStore.get(DataStoreKeys.IsFirstLaunch) ?: true
val CurrentAccountId: Int
    get() = DataStore.get(DataStoreKeys.CurrentAccountId) ?: 1
val CurrentAccountType: Int
    get() = DataStore.get(DataStoreKeys.CurrentAccountType) ?: AccountType.Local.id

val InitialPage: Int
    get() = DataStore.get(DataStoreKeys.InitialPage) ?: 0
val InitialFilter: Int
    get() = DataStore.get(DataStoreKeys.InitialFilter) ?: 1

val Languages: Int
    get() = DataStore.get(DataStoreKeys.Languages) ?: 0

suspend fun <T> DataStore<Preferences>.put(dataStoreKeys: DataStoreKeys<T>, value: T) {
    this.edit {
        withContext(Dispatchers.IO) {
            it[dataStoreKeys.key] = value
        }
    }
}

suspend fun <T> DataStore<Preferences>.put(dataStoreKeys: Preferences.Key<T>, value: T): Preferences {
    return this.edit {
        withContext(Dispatchers.IO) {
            it[dataStoreKeys] = value
        }
    }
}

fun <T> DataStore<Preferences>.putBlocking(dataStoreKeys: DataStoreKeys<T>, value: T) {
    runBlocking {
        this@putBlocking.edit {
            it[dataStoreKeys.key] = value
        }
    }
}

fun <T> DataStore<Preferences>.putBlocking(key: Preferences.Key<T>, value: T) {
    runBlocking {
        this@putBlocking.edit {
            it[key] = value
        }
    }
}

fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? {
    return runBlocking {
        this@get.data.catch { exception ->
            if (exception is IOException) {
                Log.e("RLog", "Get data store error $exception")
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.firstOrNull()?.get(key)
    }
}

fun <T> DataStore<Preferences>.get(dataStoreKeys: DataStoreKeys<T>): T? {
    return runBlocking {
        this@get.data.catch { exception ->
            if (exception is IOException) {
                Log.e("RLog", "Get data store error $exception")
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            it[dataStoreKeys.key]
        }.first()
    }
}

interface DataStoreKeys<T> {

    val key: Preferences.Key<T>

    companion object {

        val ArticleLeftSwipeKey = intPreferencesKey("ArticleLeftSwipeKey")
        val ArticleRightSwipeKey = intPreferencesKey("ArticleRightSwipeKey")

    }

    data object SyncInterval : DataStoreKeys<Long> {
        override val key: Preferences.Key<Long> = longPreferencesKey("SyncInterval")
    }

    data object SyncOnStart : DataStoreKeys<Boolean> {
        override val key: Preferences.Key<Boolean> = booleanPreferencesKey("SyncOnStart")
    }

    data object SyncOnlyOnWiFi : DataStoreKeys<Boolean> {
        override val key: Preferences.Key<Boolean> = booleanPreferencesKey("SyncOnlyOnWiFi")
    }

    data object SyncOnlyWhenCharging : DataStoreKeys<Boolean> {
        override val key: Preferences.Key<Boolean> = booleanPreferencesKey("SyncOnlyWhenCharging")
    }

    data object KeepArchived : DataStoreKeys<Long> {
        override val key: Preferences.Key<Long> = longPreferencesKey("KeepArchived")
    }

    // Version
    object IsFirstLaunch : DataStoreKeys<Boolean> {

        override val key: Preferences.Key<Boolean> = booleanPreferencesKey("isFirstLaunch")
    }

    object NewVersionNumber : DataStoreKeys<String> {

        override val key: Preferences.Key<String> = stringPreferencesKey("newVersionNumber")
    }

    object CurrentAccountId : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("currentAccountId")
    }

    object CurrentAccountType : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("currentAccountType")
    }

    object ThemeIndex : DataStoreKeys<String> {

        override val key: Preferences.Key<String> = stringPreferencesKey("themeIndex")
    }

    object PrimaryColor : DataStoreKeys<Int> {
        override val key: Preferences.Key<Int> = intPreferencesKey("primaryColor")
    }

    object DarkTheme : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("darkTheme")
    }

    object AmoledDarkTheme : DataStoreKeys<Boolean> {

        override val key: Preferences.Key<Boolean> = booleanPreferencesKey("amoledDarkTheme")
    }

    object DynamicColorTheme : DataStoreKeys<Boolean> {

        override val key: Preferences.Key<Boolean> = booleanPreferencesKey("dynamicColorTheme")
    }

    object BasicFonts : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("basicFonts")
    }

    data object FeedGroupExpandState : DataStoreKeys<Boolean> {
        override val key: Preferences.Key<Boolean> = booleanPreferencesKey("feedGroupExpandState")
    }

    data object ArticleItemStyle : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("articleItemStyle")
    }

    data object ReadingToolbarAutoHide : DataStoreKeys<Boolean> {
        override val key: Preferences.Key<Boolean> = booleanPreferencesKey("readingToolbarAutoHide")
    }

    object ReadingTextFontSize : DataStoreKeys<Int> {
        override val key: Preferences.Key<Int> = intPreferencesKey("readingTextFontSize")
    }

    object ReadingTextFontWeight : DataStoreKeys<Int> {
        override val key: Preferences.Key<Int> = intPreferencesKey("readingTextFontWeight")
    }

    object ReadingLetterSpacing : DataStoreKeys<Double> {
        override val key: Preferences.Key<Double> = doublePreferencesKey("readingLetterSpacing")
    }

    object ReadingLineHeight : DataStoreKeys<Double> {
        override val key: Preferences.Key<Double> = doublePreferencesKey("readingLineHeight")
    }

    object ReadingTextHorizontalPadding : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("readingTextHorizontalPadding")
    }

    object ReadingTextBold : DataStoreKeys<Boolean> {

        override val key: Preferences.Key<Boolean> = booleanPreferencesKey("readingTextBold")
    }

    object ReadingTextAlign : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("readingTextAlign")
    }

    object ReadingTitleAlign : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("readingTitleAlign")
    }

    object ReadingFonts : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("readingFonts")
    }

    // Interaction
    object InitialPage : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("initialPage")
    }

    object InitialFilter : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("initialFilter")
    }

    data object ClickVibration : DataStoreKeys<Boolean> {
        override val key: Preferences.Key<Boolean> = booleanPreferencesKey("clickVibration")
    }

    data object AutoMarkReadOnScroll : DataStoreKeys<Boolean> {
        override val key: Preferences.Key<Boolean> = booleanPreferencesKey("AutoMarkReadOnScroll")
    }

    // Languages
    object Languages : DataStoreKeys<Int> {

        override val key: Preferences.Key<Int> = intPreferencesKey("languages")
    }

    data object ProActiveCode : DataStoreKeys<String> {
        override val key: Preferences.Key<String> = stringPreferencesKey("proActiveCode")
    }

    data object WebDavConfigurationKey : DataStoreKeys<String> {
        override val key: Preferences.Key<String> = stringPreferencesKey(this.toString())
    }
}
