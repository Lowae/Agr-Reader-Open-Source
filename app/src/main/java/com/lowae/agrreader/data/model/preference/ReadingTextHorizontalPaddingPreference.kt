package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object ReadingTextHorizontalPaddingPreference {

    const val default = 24

    fun put(scope: CoroutineScope, value: Int) {
        scope.launch {
            DataStore.put(DataStoreKeys.ReadingTextHorizontalPadding, value)
        }
    }

    fun fromPreferences(preferences: Preferences) =
        preferences[DataStoreKeys.ReadingTextHorizontalPadding.key] ?: default
}
