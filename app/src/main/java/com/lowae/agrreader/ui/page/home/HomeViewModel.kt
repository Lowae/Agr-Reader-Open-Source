package com.lowae.agrreader.ui.page.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.repository.ReadingRepository
import com.lowae.agrreader.data.repository.RssRepository
import com.lowae.agrreader.data.repository.SyncProgress
import com.lowae.agrreader.data.repository.isSyncing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationScope
    private val scope: CoroutineScope,
    private val application: Application,
    private val rssRepository: RssRepository,
    private val readingRepository: ReadingRepository,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
) : AndroidViewModel(application) {

    val syncProgressState: StateFlow<SyncProgress>
        get() = rssRepository.get().syncProgress

    fun sync() {
        if (syncProgressState.value.isSyncing) return
        scope.launch(ioDispatcher) {
            rssRepository.get().sync()
        }
    }

    fun cancelSync() {
        rssRepository.get().cancelSync()
    }

    fun clear() {
        readingRepository.clear()
    }
}