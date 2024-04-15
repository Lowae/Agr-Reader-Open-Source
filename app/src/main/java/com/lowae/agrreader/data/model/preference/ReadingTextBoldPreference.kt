package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ReadingTextBoldPreference(val value: Boolean) : Preference() {
    object ON : ReadingTextBoldPreference(true)
    object OFF : ReadingTextBoldPreference(false)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(DataStoreKeys.ReadingTextBold, value)
        }
    }

    companion object {

        val default = OFF
        val values = listOf(ON, OFF)

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.ReadingTextBold.key]) {
                true -> ON
                false -> OFF
                else -> default
            }
    }
}

operator fun ReadingTextBoldPreference.not(): ReadingTextBoldPreference =
    when (value) {
        true -> ReadingTextBoldPreference.OFF
        false -> ReadingTextBoldPreference.ON
    }
