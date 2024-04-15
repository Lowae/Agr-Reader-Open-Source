package com.lowae.agrreader.data.model.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.R
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class SyncOnStartPreference(
    val value: Boolean,
) : Preference() {

    object On : SyncOnStartPreference(true)
    object Off : SyncOnStartPreference(false)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(DataStoreKeys.SyncOnStart, value)
        }
    }

    fun toDesc(context: Context): String =
        when (this) {
            On -> context.getString(R.string.on)
            Off -> context.getString(R.string.off)
        }

    companion object {

        val default = Off
        val values = listOf(On, Off)

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.SyncOnStart.key]) {
                true -> On
                false -> Off
                else -> default
            }
    }
}

operator fun SyncOnStartPreference.not(): SyncOnStartPreference =
    when (value) {
        true -> SyncOnStartPreference.Off
        false -> SyncOnStartPreference.On
    }
