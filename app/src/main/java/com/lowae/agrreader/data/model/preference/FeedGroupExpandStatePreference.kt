package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class FeedGroupExpandStatePreference(val value: Boolean) : Preference() {
    data object ON : FeedGroupExpandStatePreference(true)
    data object OFF : FeedGroupExpandStatePreference(false)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(DataStoreKeys.FeedGroupExpandState, value)
        }
    }

    companion object {

        val default = ON

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.FeedGroupExpandState.key]) {
                false -> OFF
                else -> ON
            }
    }
}

operator fun FeedGroupExpandStatePreference.not(): FeedGroupExpandStatePreference =
    when (this) {
        FeedGroupExpandStatePreference.ON -> FeedGroupExpandStatePreference.OFF
        FeedGroupExpandStatePreference.OFF -> FeedGroupExpandStatePreference.ON
    }
