package com.lowae.agrreader.ui.page.home.reading

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.data.action.ArticleMarkStarAction
import com.lowae.agrreader.data.dao.ArticleDao
import com.lowae.agrreader.data.dao.ArticleParsedUpdater
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.entities.ArticleParserResult
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.module.CachedOkHttpClient
import com.lowae.agrreader.data.module.MainDispatcher
import com.lowae.agrreader.data.repository.ReadingRepository
import com.lowae.agrreader.data.repository.RssRepository
import com.lowae.agrreader.ui.page.home.reading.webview.ReadingWebView
import com.lowae.agrreader.ui.page.home.reading.webview.ReadingWebViewScrollableState
import com.lowae.agrreader.ui.page.home.reading.webview.WebViewResourceLoader
import com.lowae.agrreader.utils.ext.DataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltViewModel
class ReadingViewModel @Inject constructor(
    private val rssRepository: RssRepository,
    @CachedOkHttpClient
    private val okHttpClient: OkHttpClient,
    private val readingRepository: ReadingRepository,
    @MainDispatcher
    private val mainDispatcher: CoroutineDispatcher,
    private val articleDao: ArticleDao,
) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    val webView = ReadingWebView(AgrReaderApp.application, WebViewResourceLoader(okHttpClient))

    private val _readingUiState =
        MutableStateFlow(ReadingUiState(scrollableState = ReadingWebViewScrollableState(webView)))

    val readingUiState: StateFlow<ReadingUiState> = _readingUiState.asStateFlow()

    init {
        viewModelScope.launch {
            DataStore.data.collectLatest {
                withContext(mainDispatcher) {
                    webView.updateReadingConfiguration(
                        ReadingConfiguration.fromPreference(it)
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webView.destroy()
    }

    fun initData(articleId: String, type: ReadingPageType) {
        viewModelScope.launch {
            val articleWithFeed = rssRepository.get().findArticleById(articleId) ?: return@launch
            _readingUiState.update {
                val newState = it.copy(
                    articleWithFeed = articleWithFeed,
                    content = articleWithFeed.article.fullContent,
                    isLoading = true,
                    pageState = ReadingPageState(articleWithFeed.feed.sourceType == Feed.SOURCE_TYPE_WEB)
                )
                if (newState.articleWithFeed != null) {
                    webView.loadArticle(newState.articleWithFeed, newState.pageState.isWeb)
                }
                newState
            }
            if (type == ReadingPageType.SINGLE) {
                readingRepository.updateCurrentArticle(articleWithFeed)
            }
        }
    }

    fun markStarred(isStarred: Boolean) {
        val articleWithFeed = _readingUiState.value.articleWithFeed ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _readingUiState.update {
                it.copy(
                    articleWithFeed = articleWithFeed.copy(
                        article = articleWithFeed.article.copy(
                            isStarred = isStarred
                        )
                    )
                )
            }
            rssRepository.get()
                .markAsStarred(ArticleMarkStarAction(articleWithFeed.article.id, isStarred))
        }
    }

    fun showLoading() {
        _readingUiState.update {
            it.copy(isLoading = true)
        }
    }

    fun hideLoading() {
        _readingUiState.update {
            it.copy(isLoading = false)
        }
    }

    fun updateArticleParserResult(result: ArticleParserResult) {
        val articleWithFeed = _readingUiState.value.articleWithFeed ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val updater = ArticleParsedUpdater(
                id = articleWithFeed.article.id,
                img = articleWithFeed.article.img
                    .takeIf { it.isNullOrBlank().not() }
                    ?: result.cover,
                fullContent = result.article,
                sourceHtml = ""
            )
            articleDao.updateParsedResult(updater)
        }
    }

    fun translate() {
        viewModelScope.launch {
            _readingUiState.update {
                it.copy(pageState = it.pageState.copy(isTranslated = true))
            }
            webView.translate()
        }
    }

    fun updateArticleContentSource(isWeb: Boolean) {
        viewModelScope.launch {
            val newUiState = _readingUiState.updateAndGet {
                it.copy(pageState = it.pageState.copy(isWeb = isWeb, isTranslated = false))
            }
            if (newUiState.articleWithFeed != null) {
                webView.loadArticle(newUiState.articleWithFeed, newUiState.pageState.isWeb)
            }
        }
    }
}

data class ReadingUiState(
    val articleWithFeed: ArticleWithFeed? = null,
    val content: String? = null,
    val isLoading: Boolean = true,
    val scrollableState: ReadingWebViewScrollableState,
    val pageState: ReadingPageState = ReadingPageState(),
)

data class ReadingPageState(
    val isWeb: Boolean = false,
    val isTranslated: Boolean = false
)