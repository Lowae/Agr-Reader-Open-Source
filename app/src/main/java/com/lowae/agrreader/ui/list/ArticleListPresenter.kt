package com.lowae.agrreader.ui.list

import com.lowae.agrreader.data.model.article.ArticleWithFeed
import kotlinx.coroutines.flow.StateFlow

class ArticleListPresenter(
    val readingArticleStateFlow: StateFlow<ArticleWithFeed?>,
    val onMarkAllRead: (ArticleWithFeed) -> Unit,
    val onLoadMore: () -> Unit,
)