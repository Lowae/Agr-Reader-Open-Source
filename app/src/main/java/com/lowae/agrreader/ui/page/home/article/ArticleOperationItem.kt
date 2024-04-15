package com.lowae.agrreader.ui.page.home.article

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.lowae.agrreader.data.model.article.ArticleWithFeed

@Stable
@Immutable
class ArticleOperationItem(
    val icon: ImageVector,
    val title: String,
    val onClick: ((ArticleWithFeed) -> Unit)? = null
)