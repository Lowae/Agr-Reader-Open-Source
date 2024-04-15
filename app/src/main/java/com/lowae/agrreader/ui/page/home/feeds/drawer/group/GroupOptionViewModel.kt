package com.lowae.agrreader.ui.page.home.feeds.drawer.group

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.data.dao.FeedDao
import com.lowae.agrreader.data.dao.GroupDao
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.feed.FeedSourceType
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.module.MainDispatcher
import com.lowae.agrreader.data.repository.RssRepository
import com.lowae.agrreader.utils.ext.CurrentAccountId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GroupOptionViewModel @Inject constructor(
    val rssRepository: RssRepository,
    @MainDispatcher
    private val mainDispatcher: CoroutineDispatcher,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val groupDao: GroupDao,
    private val feedDao: FeedDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId = savedStateHandle.get<String>("group_id").orEmpty()

    private val _groupOptionUiState = MutableStateFlow(GroupOptionUiState(groupId))
    val groupOptionUiState: StateFlow<GroupOptionUiState> = _groupOptionUiState.asStateFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            val group = groupDao.queryById(groupId)
            _groupOptionUiState.update {
                it.copy(groupId = groupId, group = group)
            }
            val feeds = feedDao.queryAllByGroupId(groupId)
            var allAllowNotification = true
            var allAllowInterceptedResource = true
            val allSourceTypeSet = mutableSetOf(Feed.SOURCE_TYPE_FULL_CONTENT)
            feeds.forEach {
                if (it.isNotification.not()) {
                    allAllowNotification = false
                }
                if (it.interceptionResource.not()) {
                    allAllowInterceptedResource = false
                }
                allSourceTypeSet.add(it.sourceType)
            }
            _groupOptionUiState.update {
                it.copy(
                    allAllowNotification = allAllowNotification,
                    allAllowInterceptedResource = allAllowInterceptedResource,
                    allSourceType = allSourceTypeSet.first()
                )
            }
        }
    }

    fun changeAllAllowNotification() {
        viewModelScope.launch {
            val enable = _groupOptionUiState.value.allAllowNotification.not()
            feedDao.updateIsNotificationByGroupId(CurrentAccountId, groupId, enable)
            _groupOptionUiState.update {
                it.copy(allAllowNotification = enable)
            }
        }
    }

    fun changeAllInterceptResource() {
        viewModelScope.launch {
            val enable = _groupOptionUiState.value.allAllowInterceptedResource.not()
            feedDao.updateInterceptionResourceByGroupId(CurrentAccountId, groupId, enable)
            _groupOptionUiState.update {
                it.copy(allAllowInterceptedResource = enable)
            }
        }
    }

    fun changeAllSourceType(sourceType: @FeedSourceType Int) {
        viewModelScope.launch {
            feedDao.updateIsFullContentByGroupId(CurrentAccountId, groupId, sourceType)
            _groupOptionUiState.update {
                it.copy(allSourceType = sourceType)
            }
        }
    }

    fun clear(callback: () -> Unit = {}) {
        _groupOptionUiState.value.group?.let {
            viewModelScope.launch(ioDispatcher) {
                rssRepository.get().deleteArticles(group = it)
                withContext(mainDispatcher) {
                    callback()
                }
            }
        }
    }

    fun rename(newName: String) {
        _groupOptionUiState.value.group?.let {
            viewModelScope.launch {
                rssRepository.get().updateGroup(it.copy(name = newName))
                _groupOptionUiState.update { it.copy(renameDialogVisible = false) }
            }
        }
    }

    fun showRenameDialog() {
        if (rssRepository.get().groupOperation.not()) return
        _groupOptionUiState.update {
            it.copy(
                renameDialogVisible = true,
            )
        }
    }

    fun hideRenameDialog() {
        _groupOptionUiState.update {
            it.copy(
                renameDialogVisible = false,
            )
        }
    }
}

data class GroupOptionUiState(
    val groupId: String,
    val group: Group? = null,
    val allAllowNotification: Boolean = false,
    val allAllowInterceptedResource: Boolean = false,
    val allSourceType: @FeedSourceType Int = Feed.SOURCE_TYPE_WEB,
    val renameDialogVisible: Boolean = false,
)
