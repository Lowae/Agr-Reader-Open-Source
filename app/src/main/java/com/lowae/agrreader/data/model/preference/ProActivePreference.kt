package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ProActivePreference {

    const val DEFAULT = ""

    fun put(scope: CoroutineScope, activeCode: String) {
        scope.launch(Dispatchers.IO) {
            put(activeCode)
        }
    }

    suspend fun put(activeCode: String) {
        DataStore.put(DataStoreKeys.ProActiveCode, activeCode)
    }

    fun fromPreferences(preferences: Preferences): String =
        preferences[DataStoreKeys.ProActiveCode.key] ?: DEFAULT

}