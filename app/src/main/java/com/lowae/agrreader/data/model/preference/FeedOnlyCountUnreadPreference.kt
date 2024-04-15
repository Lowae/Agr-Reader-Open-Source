package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FeedOnlyCountUnreadPreference(val value: Boolean) : Preference() {

    override fun put(scope: CoroutineScope) {
        scope.launch {
            store.put(key, value)
        }
    }

    companion object : DataStoreKeys<Boolean> {

        val default = FeedOnlyCountUnreadPreference(false)

        fun fromPreferences(preferences: Preferences) =
            preferences[key]?.let {
                FeedOnlyCountUnreadPreference(it)
            } ?: default

        override val key: Preferences.Key<Boolean> =
            booleanPreferencesKey("FeedOnlyCountUnreadPreference")
    }
}
