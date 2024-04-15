package com.lowae.agrreader.data.model.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import com.lowae.agrreader.R
import com.lowae.agrreader.data.repository.SyncWorker
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

sealed class SyncIntervalPreference(
    val value: Long,
) : Preference() {

    object Manually : SyncIntervalPreference(0L)
    object Every15Minutes : SyncIntervalPreference(15L)
    object Every30Minutes : SyncIntervalPreference(30L)
    object Every1Hour : SyncIntervalPreference(60L)
    object Every2Hours : SyncIntervalPreference(120L)
    object Every3Hours : SyncIntervalPreference(180L)
    object Every6Hours : SyncIntervalPreference(360L)
    object Every12Hours : SyncIntervalPreference(720L)
    object Every1Day : SyncIntervalPreference(1440L)

    override fun put(scope: CoroutineScope) {
        scope.launch {
            DataStore.put(DataStoreKeys.SyncInterval, value)
        }
    }

    fun toDesc(context: Context): String =
        when (this) {
            Manually -> context.getString(R.string.manually)
            Every15Minutes -> context.getString(R.string.every_15_minutes)
            Every30Minutes -> context.getString(R.string.every_30_minutes)
            Every1Hour -> context.getString(R.string.every_1_hour)
            Every2Hours -> context.getString(R.string.every_2_hours)
            Every3Hours -> context.getString(R.string.every_3_hours)
            Every6Hours -> context.getString(R.string.every_6_hours)
            Every12Hours -> context.getString(R.string.every_12_hours)
            Every1Day -> context.getString(R.string.every_1_day)
        }

    fun toPeriodicWorkRequestBuilder(): PeriodicWorkRequest.Builder =
        PeriodicWorkRequestBuilder<SyncWorker>(value, TimeUnit.MINUTES)

    companion object {

        val default = Every3Hours
        val values = listOf(
            Manually,
            Every15Minutes,
            Every30Minutes,
            Every1Hour,
            Every2Hours,
            Every3Hours,
            Every6Hours,
            Every12Hours,
            Every1Day,
        )

        fun fromPreferences(preferences: Preferences): SyncIntervalPreference {
            val value = preferences[DataStoreKeys.SyncInterval.key]
            return values.find { it.value == value } ?: default
        }
    }
}
