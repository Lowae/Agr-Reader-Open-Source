package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ClickVibrationPreference(val value: Boolean) : Preference() {
    data object ON : ClickVibrationPreference(true)
    data object OFF : ClickVibrationPreference(false)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(
                DataStoreKeys.ClickVibration,
                value
            )
        }
    }

    companion object {

        val default = ON
        val values = listOf(ON, OFF)

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.ClickVibration.key]) {
                true -> ON
                false -> OFF
                else -> default
            }
    }
}

operator fun ClickVibrationPreference.not(): ClickVibrationPreference =
    when (value) {
        true -> ClickVibrationPreference.OFF
        false -> ClickVibrationPreference.ON
    }
