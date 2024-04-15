package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object ReadingLineHeightPreference {

    const val default = 1.5

    fun put(scope: CoroutineScope, value: Double) {
        scope.launch {
            DataStore.put(DataStoreKeys.ReadingLineHeight, value)
        }
    }

    fun fromPreferences(preferences: Preferences) =
        preferences[DataStoreKeys.ReadingLineHeight.key] ?: default
}
