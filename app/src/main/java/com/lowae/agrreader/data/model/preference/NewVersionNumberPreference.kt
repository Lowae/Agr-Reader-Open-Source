package com.lowae.agrreader.data.model.preference

import androidx.datastore.preferences.core.Preferences
import com.lowae.agrreader.data.model.entities.NewVersionInfo
import com.lowae.agrreader.utils.GsonUtils
import com.lowae.agrreader.utils.compat.PackageManagerCompat
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.get
import com.lowae.agrreader.utils.ext.put

object NewVersionNumberPreference {

    const val CHECK_UPDATE_INTERVAL = 24 * 60 * 60 * 1000

    val default = NewVersionInfo(PackageManagerCompat.versionCode, 0)

    suspend fun put(newVersionInfo: NewVersionInfo) {
        DataStore.put(
            DataStoreKeys.NewVersionNumber,
            GsonUtils.toJson(newVersionInfo)
        )
    }

    fun fromPreferences(preferences: Preferences): NewVersionInfo {
        return GsonUtils.fromJson<NewVersionInfo>(preferences[DataStoreKeys.NewVersionNumber.key])
            ?: default
    }

    fun fromKey(): NewVersionInfo {
        return GsonUtils.fromJson<NewVersionInfo>(DataStore.get(DataStoreKeys.NewVersionNumber))
            ?: default
    }
}
