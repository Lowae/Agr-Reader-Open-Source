package com.lowae.agrreader.data.model.preference.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.lowae.agrreader.data.model.preference.Preference
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class FeedSyncLimitCountPreference(val value: Int) : Preference(SingleDataStore.store) {

    data object Limit50 : FeedSyncLimitCountPreference(50)
    data object Limit100 : FeedSyncLimitCountPreference(100)
    data object Limit200 : FeedSyncLimitCountPreference(200)


    override fun put(scope: CoroutineScope) {
        scope.launch { store.put(key, value) }
    }

    fun toDesc(): String =
        when (this) {
            Limit100 -> value.toString()
            Limit200 -> value.toString()
            Limit50 -> value.toString()
        }


    companion object : DataStoreKeys<Int> {
        val default = Limit50
        val values = listOf(Limit50, Limit100, Limit200)

        fun fromPreferences(preferences: Preferences): FeedSyncLimitCountPreference {
            val value = preferences[key]
            return values.find { it.value == value } ?: default
        }

        override val key: Preferences.Key<Int> = intPreferencesKey("setting_sync_limit_count")
    }

}