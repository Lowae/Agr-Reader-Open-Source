package com.lowae.agrreader.data.model.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.R
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class SyncOnlyOnWiFiPreference(
    val value: Boolean,
) : Preference() {

    object On : SyncOnlyOnWiFiPreference(true)
    object Off : SyncOnlyOnWiFiPreference(false)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(DataStoreKeys.SyncOnlyOnWiFi, value)
        }
    }

    fun toDesc(context: Context): String =
        when (this) {
            On -> context.getString(R.string.on)
            Off -> context.getString(R.string.off)
        }

    companion object {

        val default = On
        val values = listOf(On, Off)
        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.SyncOnlyOnWiFi.key]) {
                true -> On
                false -> Off
                else -> default
            }
    }
}

operator fun SyncOnlyOnWiFiPreference.not(): SyncOnlyOnWiFiPreference =
    when (value) {
        true -> SyncOnlyOnWiFiPreference.Off
        false -> SyncOnlyOnWiFiPreference.On
    }
