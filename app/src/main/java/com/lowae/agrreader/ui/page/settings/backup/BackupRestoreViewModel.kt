package com.lowae.agrreader.ui.page.settings.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.module.MainDispatcher
import com.lowae.agrreader.data.repository.webdav.WebDavConfiguration
import com.lowae.agrreader.data.repository.webdav.WebDavRepository
import com.thegrizzlylabs.sardineandroid.DavResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BackupRestoreViewModel @Inject constructor(
    private val webDavRepository: WebDavRepository,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher
    private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _backupRestoreUiState = MutableStateFlow(BackupRestoreState())
    val backupRestoreUiState: StateFlow<BackupRestoreState> = _backupRestoreUiState.asStateFlow()

    init {
        viewModelScope.launch {
            webDavRepository.configFlow.collectLatest { configuration ->
                configuration ?: return@collectLatest
                _backupRestoreUiState.update {
                    it.copy(webDavConfiguration = configuration)
                }
            }
        }
    }

    fun putConfigIfSuccess(
        configuration: WebDavConfiguration,
        onResult: (Boolean) -> Unit,
    ) {
        viewModelScope.launch(ioDispatcher) {
            val success = try {
                if (configuration.host.last() != '/') {
                    configuration.host += "/"
                }
                webDavRepository.putConfigIfConnected(configuration)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            withContext(mainDispatcher) {
                onResult(success)
            }
        }
    }

    fun backup(onResult: (Boolean) -> Unit) {
        val configuration = _backupRestoreUiState.value.webDavConfiguration
        if (configuration == null) {
            onResult(false)
            return
        }
        viewModelScope.launch(ioDispatcher) {
            val success = try {
                webDavRepository.backup(configuration)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            withContext(mainDispatcher) {
                onResult(success)
            }
        }
    }

    fun listBackupFiles() {
        val configuration = _backupRestoreUiState.value.webDavConfiguration ?: return
        viewModelScope.launch(ioDispatcher) {
            val backupFiles = webDavRepository.list(configuration).filter { it.isDirectory.not() }
            _backupRestoreUiState.update {
                it.copy(backupFiles = backupFiles)
            }
        }
    }

    fun restore(path: String, onResult: (Boolean) -> Unit) {
        val configuration = _backupRestoreUiState.value.webDavConfiguration
        if (configuration == null) {
            onResult(false)
            return
        }
        viewModelScope.launch(ioDispatcher) {
            val success = try {
                webDavRepository.restore(path, configuration)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            withContext(mainDispatcher) {
                onResult(success)
            }
        }
    }

}

data class BackupRestoreState(
    val webDavConfiguration: WebDavConfiguration? = null,
    val backupFiles: List<DavResource> = emptyList()
)