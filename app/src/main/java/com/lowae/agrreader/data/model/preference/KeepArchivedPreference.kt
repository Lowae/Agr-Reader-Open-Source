package com.lowae.agrreader.data.model.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.R
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class KeepArchivedPreference(
    val value: Long,
) : Preference() {

    object Always : KeepArchivedPreference(0L)
    object For3Days : KeepArchivedPreference(259200000L)
    object For1Week : KeepArchivedPreference(604800000L)
    object For2Weeks : KeepArchivedPreference(1209600000L)
    object For1Month : KeepArchivedPreference(2592000000L)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(DataStoreKeys.KeepArchived, value)
        }
    }

    fun toDesc(context: Context): String =
        when (this) {
            Always -> context.getString(R.string.forever)
            For3Days -> context.getString(R.string.for_3_days)
            For1Week -> context.getString(R.string.for_1_week)
            For2Weeks -> context.getString(R.string.for_2_weeks)
            For1Month -> context.getString(R.string.for_1_month)
        }

    companion object {

        val default = For1Month
        val values = listOf(
            Always,
            For3Days,
            For1Week,
            For2Weeks,
            For1Month,
        )

        fun fromPreferences(preferences: Preferences): KeepArchivedPreference {
            val value = preferences[DataStoreKeys.KeepArchived.key]
            return values.find { it.value == value } ?: default
        }
    }
}
