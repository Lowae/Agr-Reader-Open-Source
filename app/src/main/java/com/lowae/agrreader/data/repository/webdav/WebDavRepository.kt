package com.lowae.agrreader.data.repository.webdav

import android.content.Context
import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.lowae.agrreader.data.dao.ArticleDao
import com.lowae.agrreader.data.dao.FeedDao
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.repository.OpmlRepository
import com.lowae.agrreader.data.repository.RssHelper
import com.lowae.agrreader.utils.GsonUtils
import com.lowae.agrreader.utils.ZipUtils
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.put
import com.thegrizzlylabs.sardineandroid.DavResource
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipInputStream
import javax.inject.Inject

class WebDavRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @ApplicationScope
    private val scope: CoroutineScope,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    opmlRepository: OpmlRepository,
    articleDao: ArticleDao,
    feedDao: FeedDao,
    rssHelper: RssHelper,
) {

    private val accountId = CurrentAccountId
    private val backupCollections =
        listOf(
            OpmlBackupRestore(accountId, opmlRepository),
            ArticlesBackupRestore(accountId, articleDao, feedDao, rssHelper)
        )
    private val sardine = OkHttpSardine()

    val configFlow =
        DataStore.data.map { GsonUtils.fromJson<WebDavConfiguration>(it[DataStoreKeys.WebDavConfigurationKey.key]) }

    suspend fun putConfigIfConnected(configuration: WebDavConfiguration): Boolean {
        val connected = tryConnectFolder(configuration)
        if (connected) {
            DataStore.put(DataStoreKeys.WebDavConfigurationKey, GsonUtils.toJson(configuration))
        }
        return connected
    }

    fun backup(configuration: WebDavConfiguration) {
        scope.launch {
            tryConnectFolder(configuration)
            val backupFile = zipBackupFile()
            sardine.put(
                configuration.mainFolder + backupFile.name,
                backupFile,
                "application/zip"
            )
        }
    }

    suspend fun list(configuration: WebDavConfiguration): List<DavResource> {
        tryConnectFolder(configuration)
        return sardine.list(configuration.mainFolder).orEmpty()
    }

    fun restore(path: String, configuration: WebDavConfiguration) {
        scope.launch(ioDispatcher) {
            tryConnectFolder(configuration)
            val url = Uri.parse(configuration.host).buildUpon().path(path).build().toString()
            val inputStream = sardine.get(url)
            ZipUtils.unzip(ZipInputStream(inputStream), File(checkBackupFolderExist()))
            backupCollections.forEach { it.restore() }
        }
    }

    private suspend fun tryConnectFolder(configuration: WebDavConfiguration): Boolean {
        return withContext(ioDispatcher) {
            sardine.setCredentials(configuration.username, configuration.password)
            try {
                val exist = sardine.exists(configuration.mainFolder)
                if (exist.not()) {
                    sardine.createDirectory(configuration.mainFolder)
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private suspend fun zipBackupFile(): File {
        val backupFileName = "AgrReader-${
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(Date())
        }.zip"
        val backupFile = File("${checkBackupFolderExist()}/$backupFileName")
        backupCollections.forEach { it.backup() }
        ZipUtils.zip(backupFile, backupCollections.map { it.file })
        return backupFile
    }

    private fun checkBackupFolderExist() = File("${context.cacheDir.absolutePath}/backup/")
        .run {
            mkdir()
            absolutePath
        }
}

data class WebDavConfiguration(
    @SerializedName("host")
    var host: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
) {
    companion object {
        const val WEBDAV_DEFAULT_FOLDER_NAME = "AgrReader/"
    }

    val mainFolder: String
        get() = "${host}$WEBDAV_DEFAULT_FOLDER_NAME"

    val isValid: Boolean
        get() = host.isNotBlank() && username.isNotBlank() && password.isNotBlank()
}

