package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object ReadingLetterSpacingPreference {

    const val default = 0.0

    fun put(scope: CoroutineScope, value: Double) {
        scope.launch {
            DataStore.put(DataStoreKeys.ReadingLetterSpacing, value)
        }
    }

    fun fromPreferences(preferences: Preferences) =
        preferences[DataStoreKeys.ReadingLetterSpacing.key] ?: default
}
