package com.lowae.agrreader.ui.page.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.action.ArticleMarkStarAction
import com.lowae.agrreader.data.dao.ArticleHistoryDao
import com.lowae.agrreader.data.model.article.ArticleFlowItem
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.repository.RssRepository
import com.lowae.agrreader.data.repository.StringsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val articleHistoryDao: ArticleHistoryDao,
    @ApplicationScope
    private val scope: CoroutineScope,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val rssRepository: RssRepository,
    private val stringsRepository: StringsRepository
) : ViewModel() {

    private val _historyUIState: MutableStateFlow<HistoryUiState>
    val historyUIState: StateFlow<HistoryUiState>

    init {
        val historyStateFlow = articleHistoryDao.queryAllFlow()
            .map { articleWithFeeds ->
                articleWithFeeds.map {
                    ArticleFlowItem.Article(it.apply {
                        article.dateString = stringsRepository.formatAsString(article.date, true)
                    })
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        _historyUIState = MutableStateFlow(HistoryUiState(historyStateFlow))
        historyUIState = _historyUIState.asStateFlow()
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

}

data class HistoryUiState(
    val totalHistories: StateFlow<List<ArticleFlowItem>>
)