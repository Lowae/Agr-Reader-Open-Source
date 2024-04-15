package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class FeedLandscapeModePreference(val value: Boolean) : Preference() {
    data object ON : FeedLandscapeModePreference(true)
    data object OFF : FeedLandscapeModePreference(false)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            store.put(key, value)
        }
    }

    companion object : DataStoreKeys<Boolean> {

        val default = OFF

        override val key: Preferences.Key<Boolean> =
            booleanPreferencesKey("FeedLandscapeModePreference")

        fun fromPreferences(preferences: Preferences) =
            when (preferences[key]) {
                true -> ON
                else -> OFF
            }

    }
}

operator fun FeedLandscapeModePreference.not(): FeedLandscapeModePreference =
    when (this) {
        FeedLandscapeModePreference.ON -> FeedLandscapeModePreference.OFF
        FeedLandscapeModePreference.OFF -> FeedLandscapeModePreference.ON
    }
