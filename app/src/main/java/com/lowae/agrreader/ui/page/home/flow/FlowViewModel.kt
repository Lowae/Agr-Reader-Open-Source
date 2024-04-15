package com.lowae.agrreader.ui.page.home.flow

import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.action.ArticleMarkStarAction
import com.lowae.agrreader.data.model.article.ArticleFlowItem
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.general.Filter
import com.lowae.agrreader.data.model.general.FilterState
import com.lowae.agrreader.data.model.preference.FeedArticleSortByOldestPreference
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.repository.ReadingRepository
import com.lowae.agrreader.data.repository.RssRepository
import com.lowae.agrreader.ui.page.common.FlowRouter
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.component.base.paging.DatabasePaging
import com.lowae.component.base.paging.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlowViewModel @Inject constructor(
    @ApplicationScope
    private val scope: CoroutineScope,
    private val rssRepository: RssRepository,
    private val readingRepository: ReadingRepository,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val searchAction = MutableStateFlow("")

    private val _flowUiState =
        MutableStateFlow(
            FlowUiState(
                searchAction.asStateFlow(),
                readingRepository.pagingStateFlow,
                readingRepository.articleWithFeedStateFlow,
                readingRepository.readingArticleState,
            )
        )
    val flowUiState = _flowUiState.asStateFlow()

    private val _filterUiState =
        MutableStateFlow(
            savedStateHandle[FlowRouter.ARGUMENT_FLOW_KEY_FILTER_STATE] ?: FilterState()
        )
    val filterUiState = _filterUiState.asStateFlow()

    init {
        viewModelScope.launch {
            filterUiState.combine(searchAction) { filterState, search ->
                if (search.isNotBlank()) {
                    DatabasePaging(
                        coroutineContext = ioDispatcher,
                        onLoad = { limit, offset ->
                            rssRepository.get().searchArticles2(
                                content = search,
                                groupId = filterState.group?.id,
                                feedId = filterState.feed?.id,
                                isStarred = false,
                                isUnread = false,
                                limit,
                                offset
                            )
                        }
                    )
                } else {
                    DatabasePaging(
                        coroutineContext = ioDispatcher,
                        onLoad = { limit, offset ->
                            val sortByOldest =
                                FeedArticleSortByOldestPreference.fromPreferences(
                                    DataStore.data.firstOrNull() ?: emptyPreferences()
                                ).value
                            rssRepository.get().pullArticles2(
                                groupId = filterState.group?.id,
                                feedId = filterState.feed?.id,
                                isStarred = filterState.filter.isStarred(),
                                isUnread = filterState.filter.isUnread(),
                                limit,
                                offset,
                                sortByOldest.not()
                            )
                        }
                    )
                }
            }.collectLatest { paging ->
                readingRepository.initPaging(paging)
                readingRepository.load(true)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
//        readingRepository.clear()
    }

    fun changeFilter(filter: Filter) {
        _filterUiState.update {
            it.copy(
                filter = filter,
            )
        }
    }

    fun changeFilter(filterState: FilterState) {
        viewModelScope.launch {
            _filterUiState.update {
                it.copy(
                    group = filterState.group,
                    feed = filterState.feed,
                    filter = filterState.filter,
                )
            }
        }
    }

    fun inputSearchContent(content: String) {
        viewModelScope.launch {
            searchAction.emit(content)
        }
    }

    fun markAsRead(action: ArticleMarkReadAction) {
        scope.launch(ioDispatcher) {
            rssRepository.get().markAsRead(action)
        }
    }

    fun markAsStarred(
        articleId: String,
        stared: Boolean,
    ) {
        scope.launch(ioDispatcher) {
            rssRepository.get().markAsStarred(ArticleMarkStarAction(articleId, stared))
        }
    }

    fun loadMore() {
        readingRepository.load()
    }

    fun syncFeed(filterUiState: FilterState) {
        scope.launch(ioDispatcher) {
            _flowUiState.update {
                it.copy(isRefreshing = true)
            }
            when {
                filterUiState.feed != null -> {
                    rssRepository.get().sync(feedId = filterUiState.feed.id)
                }

                filterUiState.group != null -> {
                    rssRepository.get().sync(groupId = filterUiState.group.id)
                }

                else -> {
                    rssRepository.get().sync()
                }
            }
            readingRepository.load(true)
        }
    }

    fun updateReadingArticle(articleWithFeed: ArticleWithFeed) {
        readingRepository.updateCurrentArticle(articleWithFeed)
    }

    fun changeArticleSort(preference: FeedArticleSortByOldestPreference) {
        viewModelScope.launch {
            preference.put()
            readingRepository.load(true)
        }
    }
}

data class FlowUiState(
    val searchContent: StateFlow<String>,
    val pagingState: StateFlow<PagingState<ArticleWithFeed>>,
    val articleFlow: StateFlow<List<ArticleFlowItem>>,
    val readingArticleState: StateFlow<ArticleWithFeed?>,
    val isRefreshing: Boolean = false
)
