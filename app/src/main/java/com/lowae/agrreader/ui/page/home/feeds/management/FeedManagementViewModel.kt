package com.lowae.agrreader.ui.page.home.feeds.management

import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.data.model.account.AccountType
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.model.group.GroupWithFeed
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.provider.RssOperation
import com.lowae.agrreader.data.repository.RssRepository
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.GroupIdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedManagementViewModel @Inject constructor(
    @ApplicationScope
    private val scope: CoroutineScope,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val rssRepository: RssRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FeedManagementUiState(
            rssRepository.get().pullFeeds()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        )
    )

    val uiState = _uiState.asStateFlow()

    fun sortGroup(sorted: List<Group>) {
        viewModelScope.launch {
            RLog.d("FeedManagementViewModel", "sortGroup: ${sorted.map { it.name }}")
            sorted.forEachIndexed { index, group ->
                group.priority = index
            }
            rssRepository.get().updateGroup(*sorted.toTypedArray())
        }
    }

    fun selectFeed(selected: Boolean, feed: Feed) {
        _uiState.value.selectedFeedSet[feed] = selected
    }

    fun unSubscribeFeeds(accountType: Int) {
        if (AccountType.checkOperation(accountType, RssOperation.UN_SUBSCRIBE)) {
            scope.launch(ioDispatcher) {
                val feeds = _uiState.value.selectedFeedSet.keys
                feeds.forEach {
                    rssRepository.get().deleteFeed(it)
                }
                _uiState.value.selectedFeedSet.clear()
            }
        }
    }

    fun moveFeeds(accountType: Int, group: Group) {
        if (AccountType.checkOperation(accountType, RssOperation.GROUP)) {
            scope.launch(ioDispatcher) {
                val feeds = _uiState.value.selectedFeedSet.keys
                feeds.forEach {
                    it.groupId = group.id
                    rssRepository.get().updateFeed(it)
                }
                _uiState.value.selectedFeedSet.clear()
            }
        }
    }

    fun groupRename(accountType: Int, group: Group, newName: String) {
        if (AccountType.checkOperation(accountType, RssOperation.GROUP)) {
            scope.launch {
                group.name = newName
                rssRepository.get().updateGroup(group)
            }
        }
    }

    fun groupDelete(accountType: Int, group: Group, withFeeds: Boolean = false) {
        if (AccountType.checkOperation(accountType, RssOperation.GROUP)) {
            scope.launch(ioDispatcher) {
                if (withFeeds) {
                    _uiState.value.groupFeeds.value.find { it.group.id == group.id }?.feeds?.forEach {
                        rssRepository.get().deleteFeed(it)
                    }
                } else {
                    rssRepository.get()
                        .groupMoveToTargetGroup(group.id, GroupIdGenerator.DEFAULT_ID)
                }
                rssRepository.get().deleteGroup(group.id)
                _uiState.value.selectedFeedSet.clear()
            }
        }
    }

    fun groupCreate(accountType: Int, name: String) {
        if (AccountType.checkOperation(accountType, RssOperation.GROUP)) {
            scope.launch {
                rssRepository.get().addGroup(name)
            }
        }
    }

}

data class FeedManagementUiState(
    val groupFeeds: StateFlow<List<GroupWithFeed>>,
    val selectedFeedSet: SnapshotStateMap<Feed, Boolean> = SnapshotStateMap()
)