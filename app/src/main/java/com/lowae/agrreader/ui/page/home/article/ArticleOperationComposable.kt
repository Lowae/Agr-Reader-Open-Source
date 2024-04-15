package com.lowae.agrreader.ui.page.home.article

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PlaylistAddCheck
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.utils.ext.getString
import com.lowae.agrreader.utils.ext.share
import com.lowae.agrreader.utils.ext.toast

@Composable
fun rememberArticleOperations(
    articleWithFeed: ArticleWithFeed,
    onMarkRead: (ArticleWithFeed) -> Unit,
    onMarkFeedRead: (ArticleWithFeed) -> Unit,
    onMarkStar: (ArticleWithFeed) -> Unit
): List<ArticleOperationItem> {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val operations = remember {
        listOf(
            ArticleOperationItem(
                if (articleWithFeed.article.isUnread) Icons.Rounded.CheckCircle else Icons.Outlined.CheckCircle,
                if (articleWithFeed.article.isUnread) getString(R.string.mark_as_read) else getString(
                    R.string.mark_as_unread
                )
            ) {
                onMarkRead(articleWithFeed)
            },
            ArticleOperationItem(
                Icons.Rounded.PlaylistAddCheck,
                getString(R.string.mark_feed_as_read)
            ) {
                onMarkFeedRead(articleWithFeed)
            },
            ArticleOperationItem(
                if (articleWithFeed.article.isStarred) Icons.Filled.StarOutline else Icons.Rounded.Star,
                if (articleWithFeed.article.isStarred) getString(R.string.mark_as_unstar) else getString(
                    R.string.mark_as_starred
                )
            ) {
                onMarkStar(articleWithFeed)
            },
            ArticleOperationItem(
                Icons.Rounded.Share,
                getString(R.string.share)
            ) {
                context.share(articleWithFeed.article.title, articleWithFeed.article.link)
            },
            ArticleOperationItem(
                Icons.Rounded.Link,
                getString(R.string.Copy_article_link)
            ) {
                clipboardManager.setText(AnnotatedString(articleWithFeed.article.link))
                toast(R.string.Copied_article_link)
            },
        )
    }
    return operations
}