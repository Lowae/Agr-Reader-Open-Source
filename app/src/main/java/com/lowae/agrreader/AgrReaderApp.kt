package com.lowae.agrreader

import android.app.Application
import android.os.StrictMode
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import coil.ImageLoader
import com.lowae.agrreader.data.model.preference.SyncIntervalPreference
import com.lowae.agrreader.data.model.preference.SyncOnStartPreference
import com.lowae.agrreader.data.model.preference.SyncOnlyOnWiFiPreference
import com.lowae.agrreader.data.model.preference.SyncOnlyWhenChargingPreference
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.module.NoCachedOkHttpClient
import com.lowae.agrreader.data.repository.AccountRepository
import com.lowae.agrreader.data.repository.NotificationHelper
import com.lowae.agrreader.data.repository.OpmlRepository
import com.lowae.agrreader.data.repository.RYRepository
import com.lowae.agrreader.data.repository.RssHelper
import com.lowae.agrreader.data.repository.RssRepository
import com.lowae.agrreader.data.repository.StringsRepository
import com.lowae.agrreader.data.repository.SyncWorker
import com.lowae.agrreader.data.source.OPMLDataSource
import com.lowae.agrreader.data.source.RYDatabase
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.DataStore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * The Application class, where the Dagger components is generated.
 */
@HiltAndroidApp
class AgrReaderApp : Application(), Configuration.Provider {

    companion object {
        var application by Delegates.notNull<Application>()
            private set
    }

    @Inject
    lateinit var ryDatabase: RYDatabase

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var OPMLDataSource: OPMLDataSource

    @Inject
    lateinit var rssHelper: RssHelper

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var ryRepository: RYRepository

    @Inject
    lateinit var stringsRepository: StringsRepository

    @Inject
    lateinit var accountRepository: AccountRepository

    @Inject
    lateinit var opmlRepository: OpmlRepository

    @Inject
    lateinit var rssRepository: RssRepository

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    @IODispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    @NoCachedOkHttpClient
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var imageLoader: ImageLoader

    init {
        application = this
    }

    /**
     * When the application startup.
     *
     * 1. Set the uncaught exception handler
     * 2. Initialize the default account if there is none
     * 3. Synchronize once
     * 4. Check for new version
     */
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }
        application = this
        CrashHandler(this)
        applicationScope.launch(ioDispatcher) {
            RLog.d("onCreate", "accountInit")
            accountInit()
            RLog.d("onCreate", "workerInit")
            workerInit()
            RLog.d("onCreate", "initialize")
            ryRepository.initialize()
        }
    }

    /**
     * Override the [Configuration.Builder] to provide the [HiltWorkerFactory].
     */
    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    private suspend fun accountInit() {
        if (accountRepository.isNoAccount()) {
            accountRepository.addDefaultAccount()
        }
    }

    private suspend fun workerInit() {
        val preference = DataStore.data.first()
        val syncInterval = SyncIntervalPreference.fromPreferences(preference)
        val syncOnStart = SyncOnStartPreference.fromPreferences(preference)
        val syncOnlyOnWiFi = SyncOnlyOnWiFiPreference.fromPreferences(preference)
        val syncOnlyWhenCharging = SyncOnlyWhenChargingPreference.fromPreferences(preference)
        if (syncOnStart.value) {
            rssRepository.get().sync()
        }
        if (syncInterval != SyncIntervalPreference.Manually) {
            SyncWorker.enqueuePeriodicWork(
                workManager = workManager,
                syncInterval = syncInterval,
                syncOnlyWhenCharging = syncOnlyWhenCharging,
                syncOnlyOnWiFi = syncOnlyOnWiFi,
            )
        }
    }
}
