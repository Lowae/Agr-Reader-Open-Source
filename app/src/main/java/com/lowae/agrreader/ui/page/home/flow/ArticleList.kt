package com.lowae.agrreader.ui.page.home.flow

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import com.lowae.agrreader.data.model.article.ArticleFlowItem
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.preference.ArticleSwipeOperation

@Suppress("FunctionName")
fun LazyListScope.ArticleList(
    items: List<ArticleFlowItem>,
    leftSwipe: ArticleSwipeOperation,
    rightSwipe: ArticleSwipeOperation,
    onLeftSwipe: (ArticleSwipeOperation, ArticleWithFeed) -> Unit,
    onRightSwipe: (ArticleSwipeOperation, ArticleWithFeed) -> Unit,
    onClick: (ArticleWithFeed) -> Unit = {},
    onLongClick: (ArticleWithFeed) -> Unit = {},
    isSelected: (ArticleWithFeed) -> Boolean = { false },
) {
    items(items,
        key = { item ->
            when (item) {
                is ArticleFlowItem.Article -> item.articleWithFeed.article.key
                is ArticleFlowItem.Date -> item.date.time
            }
        }, contentType = {
            it::class.java
        }
    ) { item ->
        when (item) {
            is ArticleFlowItem.Article -> ArticleItem(
                item.articleWithFeed,
                leftSwipe,
                rightSwipe,
                onLeftSwipe,
                onRightSwipe,
                onClick,
                onLongClick,
                isSelected,
            )

            is ArticleFlowItem.Date -> StickyHeader(item)
        }
    }
}

@Suppress("FunctionName")
fun LazyStaggeredGridScope.ArticleList(
    items: List<ArticleFlowItem>,
    leftSwipe: ArticleSwipeOperation,
    rightSwipe: ArticleSwipeOperation,
    onLeftSwipe: (ArticleSwipeOperation, ArticleWithFeed) -> Unit,
    onRightSwipe: (ArticleSwipeOperation, ArticleWithFeed) -> Unit,
    onClick: (ArticleWithFeed) -> Unit = {},
    onLongClick: (ArticleWithFeed) -> Unit = {},
    isSelected: (ArticleWithFeed) -> Boolean = { false },
) {
    items(items,
        key = { item ->
            when (item) {
                is ArticleFlowItem.Article -> item.articleWithFeed.article.key
                is ArticleFlowItem.Date -> item.date.time
            }
        },
        contentType = {
            it::class.java
        },
        span = { item ->
            when (item) {
                is ArticleFlowItem.Article -> StaggeredGridItemSpan.SingleLane
                is ArticleFlowItem.Date -> StaggeredGridItemSpan.FullLine
            }
        }
    ) { item ->
        when (item) {
            is ArticleFlowItem.Article -> ArticleItem(
                item.articleWithFeed,
                leftSwipe,
                rightSwipe,
                onLeftSwipe,
                onRightSwipe,
                onClick,
                onLongClick,
                isSelected,
            )

            is ArticleFlowItem.Date -> StickyHeader(item)
        }
    }
}

@Suppress("FunctionName")
fun LazyListScope.ArticleListItem(
    item: ArticleFlowItem,
    leftSwipe: ArticleSwipeOperation,
    rightSwipe: ArticleSwipeOperation,
    onLeftSwipe: (ArticleSwipeOperation, ArticleWithFeed) -> Unit,
    onRightSwipe: (ArticleSwipeOperation, ArticleWithFeed) -> Unit,
    onClick: (ArticleWithFeed) -> Unit = {},
    onLongClick: (ArticleWithFeed) -> Unit = {},
    isSelected: (ArticleWithFeed) -> Boolean = { false },
) {
    when (item) {
        is ArticleFlowItem.Article -> {
            item(
                key = item.articleWithFeed.article.key,
                contentType = ArticleFlowItem.Article::class.java
            ) {
                ArticleItem(
                    item.articleWithFeed,
                    leftSwipe,
                    rightSwipe,
                    onLeftSwipe,
                    onRightSwipe,
                    onClick,
                    onLongClick,
                    isSelected,
                )
            }
        }

        is ArticleFlowItem.Date -> {
            item(key = item.date.time, contentType = ArticleFlowItem.Date::class.java) {
                StickyHeader(item)
            }
        }
    }
}