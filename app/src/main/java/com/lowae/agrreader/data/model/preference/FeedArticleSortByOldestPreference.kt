package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class FeedArticleSortByOldestPreference(
    val value: Boolean,
) : Preference() {

    data object On : FeedArticleSortByOldestPreference(true)
    data object Off : FeedArticleSortByOldestPreference(false)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            store.put(key, value)
        }
    }

    suspend fun put(): Preferences {
        return store.put(key, value)
    }

    companion object : DataStoreKeys<Boolean> {

        val default = Off

        override val key: Preferences.Key<Boolean> =
            booleanPreferencesKey("FeedArticleSortByOldestPreference")

        fun fromPreferences(preferences: Preferences) =
            when (preferences[key]) {
                true -> On
                else -> default
            }

        operator fun FeedArticleSortByOldestPreference.not(): FeedArticleSortByOldestPreference =
            when (value) {
                true -> Off
                false -> On
            }
    }
}

