package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.general.Filter
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.getString
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class InitialFilterPreference(val value: Int) : Preference() {
    object Starred : InitialFilterPreference(0)
    object Unread : InitialFilterPreference(1)
    object All : InitialFilterPreference(2)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(
                DataStoreKeys.InitialFilter,
                value
            )
        }
    }

    fun toDesc(): String =
        when (this) {
            Starred -> getString(R.string.starred)
            Unread -> getString(R.string.unread)
            All -> getString(R.string.all)
        }

    fun toFilter(): Filter =
        when (this) {
            All -> Filter.All
            Starred -> Filter.Starred
            Unread -> Filter.Unread
        }

    companion object {

        val default = Unread
        val values = listOf(Starred, Unread, All)

        fun fromPreferences(preferences: Preferences) =
            fromValue(preferences[DataStoreKeys.InitialFilter.key])

        fun fromValue(value: Int?) =
            when (value) {
                0 -> Starred
                1 -> Unread
                2 -> All
                else -> default
            }
    }
}
