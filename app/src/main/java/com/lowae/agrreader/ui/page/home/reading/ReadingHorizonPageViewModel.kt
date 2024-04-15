package com.lowae.agrreader.ui.page.home.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.data.model.article.ArticleFlowItem
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.repository.ReadingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingHorizonPageViewModel @Inject constructor(
    private val readingRepository: ReadingRepository,
) : ViewModel() {

    private val _readingHorizonPageUiState =
        MutableStateFlow(ReadingHorizonPageUiState(emptyList()))
    val readingHorizonPageUiState = _readingHorizonPageUiState.asStateFlow()

    init {
        viewModelScope.launch {
            readingRepository.articleWithFeedStateFlow.collectLatest { flowArticles ->
                _readingHorizonPageUiState.update {
                    it.copy(articles = flowArticles.mapNotNull { item ->
                        (item as? ArticleFlowItem.Article)?.articleWithFeed
                    })
                }
            }
        }
    }

    fun updateCurrentArticle(articleWithFeed: ArticleWithFeed) {
        readingRepository.updateCurrentArticle(articleWithFeed)
    }

}

data class ReadingHorizonPageUiState(
    val articles: List<ArticleWithFeed>,
)
