package com.lowae.agrreader.data.repository

import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.model.article.ArticleFlowItem
import com.lowae.agrreader.data.model.article.ArticleHistory
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.ui.page.appendPagingData
import com.lowae.agrreader.ui.page.updateByAction
import com.lowae.agrreader.utils.RLog
import com.lowae.component.base.paging.DatabasePaging
import com.lowae.component.base.paging.PagingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadingRepository @Inject constructor(
    @ApplicationScope
    private val scope: CoroutineScope,
    private val rssRepository: RssRepository,
    private val accountRepository: AccountRepository,
) {

    private val _readingArticleState = MutableStateFlow<ArticleWithFeed?>(null)
    val readingArticleState = _readingArticleState.asStateFlow()

    private val pagingFlow: MutableStateFlow<DatabasePaging<ArticleWithFeed>?> =
        MutableStateFlow(null)

    private val _pagingStateFlow =
        MutableStateFlow<PagingState<ArticleWithFeed>>(PagingState.Loading)
    val pagingStateFlow = _pagingStateFlow.asStateFlow()

    private val _articleWithFeedsStateFlow = MutableStateFlow(emptyList<ArticleFlowItem>())
    val articleWithFeedStateFlow = _articleWithFeedsStateFlow.asStateFlow()

    init {
        scope.launch {
            accountRepository.currentAccount
                .flatMapLatest { rssRepository.get().articleUpdateFlow }
                .collect { action ->
                    RLog.d("articleUpdateFlow", "flow $action")
                    _articleWithFeedsStateFlow.update {
                        it.updateByAction(action)
                    }
                }

        }

        scope.launch {
            pagingFlow.filter { it != null }
                .flatMapLatest { paging ->
                    paging!!.pagingState
                }.collectLatest { pagingState ->
                    _pagingStateFlow.emit(pagingState)
                    if (pagingState is PagingState.NotLoading) {
                        _articleWithFeedsStateFlow.update {
                            it.appendPagingData(pagingState.data)
                        }
                    }
                }
        }
    }

    fun initPaging(paging: DatabasePaging<ArticleWithFeed>) {
        pagingFlow.tryEmit(paging)
    }

    fun load(refresh: Boolean = false) {
        scope.launch {
            if (refresh) {
                _articleWithFeedsStateFlow.emit(emptyList())
            }
            pagingFlow.value?.load(refresh)
        }
    }

    fun updateCurrentArticle(articleWithFeed: ArticleWithFeed) {
        scope.launch {
            rssRepository.get().markAsRead(
                ArticleMarkReadAction(
                    articleId = articleWithFeed.article.id,
                    isUnread = false,
                )
            )
            rssRepository.get().insertHistoryRecord(
                ArticleHistory(
                    articleWithFeed.article.id,
                    articleWithFeed.article.accountId,
                    articleWithFeed.feed.id,
                    Date().time
                )
            )
            _readingArticleState.emit(articleWithFeed)
        }
    }

    fun clear() {
        pagingFlow.tryEmit(null)
        _pagingStateFlow.tryEmit(PagingState.Loading)
        _articleWithFeedsStateFlow.tryEmit(emptyList())
        _readingArticleState.tryEmit(null)
    }
}