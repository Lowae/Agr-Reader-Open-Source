package com.lowae.agrreader.data.model.preference.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.lowae.agrreader.utils.ext.KvDataStore
import com.lowae.agrreader.utils.ext.get

object SingleDataStore {

    object Keys {
        val VERSION_INFO = stringPreferencesKey("version_info")
    }

    val store = KvDataStore

    fun <T> get(key: Preferences.Key<T>, default: T): T = KvDataStore.get(key) ?: default

}