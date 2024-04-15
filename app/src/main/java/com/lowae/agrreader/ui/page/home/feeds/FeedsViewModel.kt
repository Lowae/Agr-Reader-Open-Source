package com.lowae.agrreader.ui.page.home.feeds

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.data.model.account.Account
import com.lowae.agrreader.data.model.feed.StatusCount
import com.lowae.agrreader.data.model.general.Filter
import com.lowae.agrreader.data.model.general.FilterState
import com.lowae.agrreader.data.model.group.GroupWithFeed
import com.lowae.agrreader.data.model.preference.InitialFilterPreference
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.repository.AccountRepository
import com.lowae.agrreader.data.repository.RssRepository
import com.lowae.agrreader.data.source.databaseInvalidationTrackerFlow
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.InitialFilter
import com.lowae.agrreader.utils.ext.getEndOfDay
import com.lowae.agrreader.utils.ext.getStartOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val rssRepository: RssRepository,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _filterUiState = MutableStateFlow(FilterState())
    val filterUiState = _filterUiState.asStateFlow()

    private val todayUnreadCountStateFlow = MutableStateFlow(0)
    private val notifyFeedsUpdateFlow = MutableSharedFlow<Unit>(1)

    private val _feedsUiState: MutableStateFlow<FeedsUiState>
    val feedsUiState: StateFlow<FeedsUiState>

    init {
        RLog.d("FeedsViewModel", "pullFeeds init")
        var initial = true
        changeFilter(InitialFilterPreference.fromValue(InitialFilter).toFilter())
        val totalStatusCountState = MutableStateFlow(StatusCount.DEFAULT)
        val groupWithFeedsStateV2 =
            merge(filterUiState, notifyFeedsUpdateFlow, databaseInvalidationTrackerFlow()).map {
                filterUiState.value
            }.debounce {
                if (initial) {
                    initial = false
                    0
                } else {
                    100
                }
            }.flatMapLatest { filterState ->
                RLog.d("FeedsViewModel", "pullFeeds start")
                val isStarred = filterState.filter.isStarred()
                val isUnread = filterState.filter.isUnread()
                rssRepository.get().pullFeeds().map { groupWithFeedList ->
                    var totalAll = 0
                    var totalUnread = 0
                    var totalStarred = 0
                    groupWithFeedList.forEach {
                        it.feeds.forEach { feed ->
                            feed.count =
                                rssRepository.get().fastCountArticle(feed.accountId, feed.id)
                        }
                        it.group.count = it.calculateGroupStatusCount()
                        totalAll += it.group.count.all
                        totalUnread += it.group.count.unread
                        totalStarred += it.group.count.starred
                    }
                    totalStatusCountState.emit(
                        StatusCount.from(totalAll, totalUnread, totalStarred)
                    )
                    val groupIterator = groupWithFeedList.iterator()
                    while (groupIterator.hasNext()) {
                        val groupWithFeed = groupIterator.next()
                        val feedIterator = groupWithFeed.feeds.iterator()
                        while (feedIterator.hasNext()) {
                            val feed = feedIterator.next()
                            val feedImportant =
                                if (isStarred) feed.count.starred else if (isUnread) feed.count.unread else feed.count.all
                            if ((isStarred || isUnread) && feedImportant == 0) {
                                feedIterator.remove()
                                continue
                            }
                        }
                        val groupImportant =
                            if (isStarred) groupWithFeed.group.count.starred else if (isUnread) groupWithFeed.group.count.unread else groupWithFeed.group.count.all
                        if ((isStarred || isUnread) && groupImportant == 0) {
                            groupIterator.remove()
                        }
                    }
                    groupWithFeedList.toMutableList()
                }
            }.flowOn(ioDispatcher).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        _feedsUiState = MutableStateFlow(
            FeedsUiState(
                totalStatusCountState = totalStatusCountState.asStateFlow(),
                todayUnreadCount = todayUnreadCountStateFlow.asStateFlow(),
                groupWithFeedList = groupWithFeedsStateV2
            )
        )
        feedsUiState = _feedsUiState.asStateFlow()

        viewModelScope.launch {
            accountRepository.currentAccount
                .flowOn(ioDispatcher)
                .collect { account ->
                    _feedsUiState.update {
                        it.copy(account = account)
                    }
                    notifyFeedsUpdateFlow.emit(Unit)
                }
        }

        viewModelScope.launch {
            notifyFeedsUpdateFlow.flatMapLatest {
                rssRepository.get()
                    .countTimeRangeUnreadFlow(true, getStartOfDay().time, getEndOfDay().time)
            }.collect(todayUnreadCountStateFlow)
        }
    }

    fun changeFilter(filter: Filter) {
        _filterUiState.update {
            it.copy(
                filter = filter,
            )
        }
    }
}

data class FeedsUiState(
    val account: Account? = null,
    val totalStatusCountState: StateFlow<StatusCount>,
    val todayUnreadCount: StateFlow<Int>,
    val groupWithFeedList: StateFlow<List<GroupWithFeed>>,
    val listState: LazyListState = LazyListState(),
    val groupsVisible: SnapshotStateMap<String, Boolean> = mutableStateMapOf(),
)

sealed class GroupFeedsView {
    class Group(val group: com.lowae.agrreader.data.model.group.Group) : GroupFeedsView()
    class Feed(val feed: com.lowae.agrreader.data.model.feed.Feed) : GroupFeedsView()
}
