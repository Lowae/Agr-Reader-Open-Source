package com.lowae.agrreader.ui.list

import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.preference.ArticleSwipeOperation

class ArticleItemPresenter(
    val leftSwipe: ArticleSwipeOperation,
    val rightSwipe: ArticleSwipeOperation,
    val onLeftSwipe: (ArticleSwipeOperation, ArticleWithFeed) -> Unit,
    val onRightSwipe: (ArticleSwipeOperation, ArticleWithFeed) -> Unit,
    val onClick: (ArticleWithFeed) -> Unit = {},
    val onLongClick: (ArticleWithFeed) -> Unit = {},
    val isSelected: (ArticleWithFeed) -> Boolean = { false },
)