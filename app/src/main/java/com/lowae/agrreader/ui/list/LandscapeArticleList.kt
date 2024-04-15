package com.lowae.agrreader.ui.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.article.ArticleFlowItem
import com.lowae.agrreader.data.model.preference.VolumePageScrollPreference
import com.lowae.agrreader.ui.component.base.EmptyPlaceHolder
import com.lowae.agrreader.ui.page.home.flow.ArticleList
import com.lowae.agrreader.ui.page.home.flow.MarkReadOnScroll
import com.lowae.agrreader.ui.page.home.reading.VolumeButtonsPageScroller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LandscapeArticleList(
    listState: LazyStaggeredGridState,
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
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
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
        LaunchedEffect(listState) {
            listPresenter.readingArticleStateFlow.collect { reading ->
                val index =
                    items.indexOfFirst { it is ArticleFlowItem.Article && it.articleWithFeed.article.id == reading?.article?.id }
                if (index >= 0) {
                    listState.scrollToItem(index)
                }
            }
        }
        LandscapeArticleListPageScroller(scope, listState)
    }
}

@Composable
private fun LandscapeArticleListPageScroller(
    scope: CoroutineScope,
    listState: LazyStaggeredGridState
) {
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