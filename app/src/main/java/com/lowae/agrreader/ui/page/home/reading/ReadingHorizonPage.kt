package com.lowae.agrreader.ui.page.home.reading

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.data.model.preference.LocalFeedLandscapeMode
import com.lowae.agrreader.utils.ext.collectAsStateValue
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReadingHorizonPage(
    navController: NavHostController,
    articleId: String,
    readingHorizonPageViewModel: ReadingHorizonPageViewModel = hiltViewModel()
) {
    val isLandscapeMode = LocalFeedLandscapeMode.current.value
    val scope = rememberCoroutineScope()
    val uiState = readingHorizonPageViewModel.readingHorizonPageUiState.collectAsStateValue()
    val pagerState =
        rememberPagerState(initialPage = uiState.articles.indexOfFirst { it.article.id == articleId }) { uiState.articles.size }
    if (isLandscapeMode) {
        LaunchedEffect(articleId) {
            pagerState.scrollToPage(uiState.articles.indexOfFirst { it.article.id == articleId })
        }
    }
    HorizontalPager(
        state = pagerState,
        key = {
            uiState.articles[it].article.id
        }
    ) {
        val articleWithFeed = uiState.articles[it]
        ReadingPage(
            navController = navController,
            articleId = articleWithFeed.article.id,
            type = ReadingPageType.MULTIPLE,
            current = it == pagerState.currentPage
        ) { pre ->
            scope.launch {
                if (pre) {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                } else {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        }
    }

    LaunchedEffect(uiState, pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .debounce(300)
            .collect { currentPage ->
                val article = uiState.articles.getOrNull(currentPage) ?: return@collect
                readingHorizonPageViewModel.updateCurrentArticle(article)
            }
    }
}