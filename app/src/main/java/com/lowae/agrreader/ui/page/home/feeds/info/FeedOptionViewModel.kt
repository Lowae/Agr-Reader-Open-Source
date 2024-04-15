package com.lowae.agrreader.ui.page.home.feeds.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.module.MainDispatcher
import com.lowae.agrreader.data.provider.RssServiceException
import com.lowae.agrreader.data.repository.RssRepository
import com.lowae.agrreader.utils.ext.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FeedOptionViewModel @Inject constructor(
    val rssRepository: RssRepository,
    @MainDispatcher
    private val mainDispatcher: CoroutineDispatcher,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val feedId = savedStateHandle.get<String>("feed_id").orEmpty()
    private val _feedOptionUiState = MutableStateFlow(FeedOptionUiState())
    val feedOptionUiState: StateFlow<FeedOptionUiState> = _feedOptionUiState.asStateFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            fetchFeed(feedId)
        }
    }

    private fun fetchFeed(feedId: String) {
        viewModelScope.launch(ioDispatcher) {
            val feed = rssRepository.get().findFeedById(feedId)
            _feedOptionUiState.update {
                it.copy(
                    feed = feed,
                )
            }
        }
    }

    fun changeTranslationLanguage(languagePair: Pair<String, String>?) {
        viewModelScope.launch(ioDispatcher) {
            _feedOptionUiState.value.feed?.let {
                rssRepository.get()
                    .updateFeed(it.copy(translationLanguage = languagePair))
                fetchFeed(it.id)
            }
        }
    }

    fun changeParseFullContentPreset(sourceType: Int) {
        viewModelScope.launch(ioDispatcher) {
            _feedOptionUiState.value.feed?.let {
                rssRepository.get()
                    .updateFeed(it.copy(sourceType = sourceType))
                fetchFeed(it.id)
            }
        }
    }

    fun changeAllowNotificationPreset() {
        viewModelScope.launch(ioDispatcher) {
            _feedOptionUiState.value.feed?.let {
                rssRepository.get().updateFeed(it.copy(isNotification = !it.isNotification))
                fetchFeed(it.id)
            }
        }
    }

    fun changeInterceptionResource() {
        viewModelScope.launch(ioDispatcher) {
            _feedOptionUiState.value.feed?.let {
                rssRepository.get()
                    .updateFeed(it.copy(interceptionResource = !it.interceptionResource))
                fetchFeed(it.id)
            }
        }
    }

    fun delete(callback: () -> Unit = {}) {
        _feedOptionUiState.value.feed?.let {
            viewModelScope.launch {
                try {
                    withContext(ioDispatcher) {
                        rssRepository.get().deleteFeed(it)
                    }
                } catch (e: RssServiceException) {
                    toast(e.message)
                }
                callback()
            }
        }
    }

    fun hideDeleteDialog() {
        _feedOptionUiState.update { it.copy(deleteDialogVisible = false) }
    }

    fun showDeleteDialog() {
        _feedOptionUiState.update { it.copy(deleteDialogVisible = true) }
    }

    fun showClearDialog() {
        _feedOptionUiState.update { it.copy(clearDialogVisible = true) }
    }

    fun hideClearDialog() {
        _feedOptionUiState.update { it.copy(clearDialogVisible = false) }
    }

    fun clearFeed(callback: () -> Unit = {}) {
        _feedOptionUiState.value.feed?.let {
            viewModelScope.launch(ioDispatcher) {
                rssRepository.get().deleteArticles(feed = it)
                withContext(mainDispatcher) {
                    callback()
                }
            }
        }
    }

    fun renameFeed(newName: String) {
        viewModelScope.launch {
            val newFeed = _feedOptionUiState.value.feed?.copy(name = newName) ?: return@launch
            _feedOptionUiState.update {
                it.copy(feed = newFeed)
            }
            rssRepository.get().updateFeed(newFeed)
        }
    }

    fun showFeedUrlDialog() {
        _feedOptionUiState.update {
            it.copy(
                changeUrlDialogVisible = true,
                newUrl = _feedOptionUiState.value.feed?.url ?: "",
            )
        }
    }

    fun hideFeedUrlDialog() {
        _feedOptionUiState.update {
            it.copy(
                changeUrlDialogVisible = false,
                newUrl = "",
            )
        }
    }

    fun changeFeedUrl(url: String) {
        _feedOptionUiState.value.feed?.let {
            viewModelScope.launch {
                _feedOptionUiState.updateAndGet {
                    it.copy(
                        newUrl = url,
                        changeUrlDialogVisible = false
                    )
                }
                rssRepository.get().updateFeed(it.copy(url = url))
                fetchFeed(feedId)
            }
        }
    }
}

data class FeedOptionUiState(
    val feed: Feed? = null,
    val deleteDialogVisible: Boolean = false,
    val clearDialogVisible: Boolean = false,
    val newUrl: String = "",
    val changeUrlDialogVisible: Boolean = false,
)
