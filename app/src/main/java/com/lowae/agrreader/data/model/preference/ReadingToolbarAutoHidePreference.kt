package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ReadingToolbarAutoHidePreference(val value: Boolean) : Preference() {

    companion object {
        val default = ON

        operator fun ReadingToolbarAutoHidePreference.not(): ReadingToolbarAutoHidePreference =
            when (value) {
                true -> OFF
                false -> ON
            }

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.ReadingToolbarAutoHide.key]) {
                true -> ON
                false -> OFF
                else -> default
            }
    }

    data object ON : ReadingToolbarAutoHidePreference(true)
    data object OFF : ReadingToolbarAutoHidePreference(false)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(DataStoreKeys.ReadingToolbarAutoHide, value)
        }
    }

    fun fromPreferences(preferences: Preferences) =
        preferences[DataStoreKeys.ReadingTextFontWeight.key]
            ?: ReadingTextFontWeightPreference.default

}