package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class AutoMarkReadOnScroll(val value: Boolean) : Preference() {
    data object ON : AutoMarkReadOnScroll(true)
    data object OFF : AutoMarkReadOnScroll(false)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(
                DataStoreKeys.AutoMarkReadOnScroll,
                value
            )
        }
    }

    companion object {

        val default = OFF
        val values = listOf(ON, OFF)

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.AutoMarkReadOnScroll.key]) {
                true -> ON
                false -> OFF
                else -> default
            }
    }
}

operator fun AutoMarkReadOnScroll.not(): AutoMarkReadOnScroll =
    when (value) {
        true -> AutoMarkReadOnScroll.OFF
        false -> AutoMarkReadOnScroll.ON
    }
