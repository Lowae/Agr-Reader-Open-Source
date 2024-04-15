package com.lowae.agrreader.ui.page.today

import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.action.ArticleMarkStarAction
import com.lowae.agrreader.data.model.article.ArticleFlowItem
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.preference.FeedArticleSortByOldestPreference
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.repository.ReadingRepository
import com.lowae.agrreader.data.repository.RssRepository
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.getEndOfDay
import com.lowae.agrreader.utils.ext.getStartOfDay
import com.lowae.component.base.paging.DatabasePaging
import com.lowae.component.base.paging.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodayUnreadViewModel @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val rssRepository: RssRepository,
    private val readingRepository: ReadingRepository,
) : ViewModel() {
    private val _todayUnreadUIState = MutableStateFlow(
        TodayUnreadUiState(
            readingRepository.pagingStateFlow,
            readingRepository.articleWithFeedStateFlow,
            readingRepository.readingArticleState,
        )
    )
    val todayUnreadUIState: StateFlow<TodayUnreadUiState> = _todayUnreadUIState.asStateFlow()

    init {
        readingRepository.initPaging(
            DatabasePaging(
                coroutineContext = ioDispatcher,
                onLoad = { limit, offset ->
                    val sortByOldest =
                        FeedArticleSortByOldestPreference.fromPreferences(
                            DataStore.data.firstOrNull() ?: emptyPreferences()
                        ).value
                    rssRepository.get().queryTimeRangeUnreadArticlesPaging(
                        startTime = getStartOfDay().time,
                        endTime = getEndOfDay().time,
                        isStarred = false,
                        isUnread = true,
                        limit = limit,
                        offset = offset,
                        desc = sortByOldest.not()
                    )
                })
        )
    }

    val currentArticleId: String
        get() = readingRepository.readingArticleState.value?.article?.id.orEmpty()

    override fun onCleared() {
        super.onCleared()
//        readingRepository.clear()
    }

    fun markAsRead(action: ArticleMarkReadAction) {
        viewModelScope.launch(ioDispatcher) {
            rssRepository.get().markAsRead(action)
        }
    }

    fun markAsStarred(
        articleId: String,
        stared: Boolean,
    ) {
        viewModelScope.launch(ioDispatcher) {
            rssRepository.get().markAsStarred(ArticleMarkStarAction(articleId, stared))
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            readingRepository.load()
        }
    }

    fun updateReadingArticle(articleWithFeed: ArticleWithFeed) {
        readingRepository.updateCurrentArticle(articleWithFeed)
    }
}

data class TodayUnreadUiState(
    val pagingState: StateFlow<PagingState<ArticleWithFeed>>,
    val articleFlow: StateFlow<List<ArticleFlowItem>>,
    val readingArticleState: StateFlow<ArticleWithFeed?>,
)