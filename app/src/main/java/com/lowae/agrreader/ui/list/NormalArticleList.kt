package com.lowae.agrreader.ui.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.article.ArticleFlowItem
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.preference.VolumePageScrollPreference
import com.lowae.agrreader.ui.component.base.EmptyPlaceHolder
import com.lowae.agrreader.ui.page.home.flow.ArticleList
import com.lowae.agrreader.ui.page.home.flow.MarkReadOnScroll
import com.lowae.agrreader.ui.page.home.reading.VolumeButtonsPageScroller
import com.lowae.agrreader.utils.ext.collectAsStateValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NormalArticleList(
    listState: LazyListState,
    items: List<ArticleFlowItem>,
    itemPresenter: ArticleItemPresenter,
    listPresenter: ArticleListPresenter
) {
    val scope = rememberCoroutineScope()
    if (items.isEmpty()) {
        EmptyPlaceHolder(
            modifier = Modifier.fillMaxSize(),
            tips = stringResource(R.string.placeholder_empty_articles)
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            ArticleList(
                items,
                leftSwipe = itemPresenter.leftSwipe,
                rightSwipe = itemPresenter.rightSwipe,
                onLeftSwipe = itemPresenter.onLeftSwipe,
                onRightSwipe = itemPresenter.onRightSwipe,
                onClick = itemPresenter.onClick,
                onLongClick = itemPresenter.onLongClick,
                isSelected = itemPresenter.isSelected
            )
        }
        MarkReadOnScroll(
            key = arrayOf(listState),
            firstVisibleItemIndexFlow = snapshotFlow { listState.firstVisibleItemIndex },
            pagingItems = items,
            onMarkAllRead = listPresenter.onMarkAllRead
        )
        AnchorReadingArticle(
            readingArticle = listPresenter.readingArticleStateFlow.collectAsStateValue(),
            items = items,
            listState = listState
        )
        ArticleListPageScroller(scope, listState)
    }
}

@Composable
private fun ArticleListPageScroller(scope: CoroutineScope, listState: LazyListState) {
    VolumeButtonsPageScroller(key = listState, onPageUp = {
        scope.launch {
            val scrollToIndex =
                (listState.firstVisibleItemIndex - (listState.layoutInfo.visibleItemsInfo.size - 1))
                    .coerceAtLeast(0)
            if (it == VolumePageScrollPreference.ANIMATION) {
                listState.animateScrollToItem(scrollToIndex)
            } else {
                listState.scrollToItem(scrollToIndex)
            }
        }
    }, onPageDown = {
        scope.launch {
            val scrollToIndex =
                (listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size - 1)
                    .coerceAtMost(listState.layoutInfo.totalItemsCount)
            if (it == VolumePageScrollPreference.ANIMATION) {
                listState.animateScrollToItem(scrollToIndex)
            } else {
                listState.scrollToItem(scrollToIndex)
            }
        }
    })
}

@Composable
private fun AnchorReadingArticle(
    readingArticle: ArticleWithFeed?,
    items: List<ArticleFlowItem>,
    listState: LazyListState
) {
    val readingArticleIndex by remember(readingArticle, items) {
        derivedStateOf {
            items.indexOfFirst { it is ArticleFlowItem.Article && it.articleWithFeed.article.id == readingArticle?.article?.id }
        }
    }
    LaunchedEffect(readingArticleIndex) {
        val (firstVisibleItemIndex, lastVisibleItemIndex) = listState.layoutInfo.visibleItemsInfo.let {
            (it.firstOrNull()?.index ?: 0) to (it.lastOrNull()?.index ?: 0)
        }
        if (readingArticleIndex >= 0 && (readingArticleIndex < firstVisibleItemIndex || readingArticleIndex > lastVisibleItemIndex)) {
            listState.scrollToItem(readingArticleIndex)
        }
    }
}