package com.lowae.agrreader.ui.page.home.article

import androidx.compose.runtime.Composable
import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.general.MarkAsReadConditions
import com.lowae.agrreader.ui.page.home.flow.FlowViewModel

@Composable
fun FlowArticleOperationBottomDrawer(
    flowViewModel: FlowViewModel,
    articleWithFeed: ArticleWithFeed? = null,
    onDismiss: () -> Unit
) {
    if (articleWithFeed != null) {
        val operations = rememberArticleOperations(
            articleWithFeed = articleWithFeed,
            onMarkRead = {
                flowViewModel.markAsRead(
                    ArticleMarkReadAction(
                        articleId = it.article.id,
                        before = MarkAsReadConditions.All.toDate(),
                        isUnread = it.article.isUnread.not()
                    )
                )
            },
            onMarkFeedRead = {
                flowViewModel.markAsRead(ArticleMarkReadAction(feedId = articleWithFeed.feed.id))
            },
            onMarkStar = {
                flowViewModel.markAsStarred(it.article.id, it.article.isStarred.not())
            }
        )
        ArticleOperationBottomDrawer(
            articleWithFeed,
            onDismissRequest = onDismiss,
            operations = operations
        )
    }
}