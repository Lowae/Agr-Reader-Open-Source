package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class DynamicColorPreference(val value: Boolean) : Preference() {
    object ON : DynamicColorPreference(true)
    object OFF : DynamicColorPreference(false)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(DataStoreKeys.DynamicColorTheme, value)
        }
    }

    companion object {

        val default = OFF
        val values = listOf(ON, OFF)

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.DynamicColorTheme.key]) {
                true -> ON
                false -> OFF
                else -> default
            }
    }
}

operator fun DynamicColorPreference.not(): DynamicColorPreference =
    when (value) {
        true -> DynamicColorPreference.OFF
        false -> DynamicColorPreference.ON
    }
