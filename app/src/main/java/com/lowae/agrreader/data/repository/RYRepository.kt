package com.lowae.agrreader.data.repository

import android.content.Context
import com.lowae.agrreader.data.model.entities.NewVersionInfo
import com.lowae.agrreader.data.model.preference.NewVersionNumberPreference
import com.lowae.agrreader.data.model.preference.datastore.SingleDataStore
import com.lowae.agrreader.data.model.service.VersionInfo
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.module.MainDispatcher
import com.lowae.agrreader.data.module.NoCachedOkHttpClient
import com.lowae.agrreader.utils.BuildFlavor
import com.lowae.agrreader.utils.EnvUtils
import com.lowae.agrreader.utils.GsonUtils
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.compat.PackageManagerCompat
import com.lowae.agrreader.utils.ext.put
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.executeAsync
import javax.inject.Inject

class RYRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @ApplicationScope
    private val applicationScope: CoroutineScope,
    @NoCachedOkHttpClient
    private val okHttpClient: OkHttpClient,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher
    private val mainDispatcher: CoroutineDispatcher,
) {

    companion object {
        private const val TAG = "RYRepository"
    }

    suspend fun initialize() {
        if (EnvUtils.flavor != BuildFlavor.OFFICIAL) return
        try {
            checkUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun checkUpdate() {
        val lastNewVersionInfo = NewVersionNumberPreference.fromKey()
        if (System.currentTimeMillis() - lastNewVersionInfo.lastCheckTime < NewVersionNumberPreference.CHECK_UPDATE_INTERVAL) {
            RLog.d(TAG, "lastVersionInfo: $lastNewVersionInfo")
            return
        }
        val responseString = ""
        val versionInfo = GsonUtils.fromJson<VersionInfo>(responseString)
        val currentVersionCode = PackageManagerCompat.versionCode
        val newVersionCode = versionInfo?.versionCode ?: currentVersionCode
        RLog.d(TAG, "newVersionInfo: $newVersionCode")
        NewVersionNumberPreference.put(NewVersionInfo(newVersionCode, System.currentTimeMillis()))
        if (newVersionCode > currentVersionCode) {
            SingleDataStore.store.put(SingleDataStore.Keys.VERSION_INFO, responseString)
        }
    }
}
