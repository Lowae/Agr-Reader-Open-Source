package com.lowae.agrreader.ui.page.home.article

import androidx.compose.runtime.Composable
import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.general.MarkAsReadConditions
import com.lowae.agrreader.ui.page.today.TodayUnreadViewModel

@Composable
fun TodayOfUnreadArticleOperationBottomDrawer(
    todayUnreadViewModel: TodayUnreadViewModel,
    articleWithFeed: ArticleWithFeed? = null,
    onDismiss: () -> Unit
) {
    if (articleWithFeed != null) {
        val operations = rememberArticleOperations(
            articleWithFeed = articleWithFeed,
            onMarkRead = {
                todayUnreadViewModel.markAsRead(
                    ArticleMarkReadAction(
                        articleId = it.article.id,
                        before = MarkAsReadConditions.All.toDate(),
                        isUnread = it.article.isUnread.not()
                    )
                )
            },
            onMarkFeedRead = {
                todayUnreadViewModel.markAsRead(ArticleMarkReadAction(feedId = articleWithFeed.feed.id))
            },
            onMarkStar = {
                todayUnreadViewModel.markAsStarred(it.article.id, it.article.isStarred.not())
            }
        )
        ArticleOperationBottomDrawer(
            articleWithFeed,
            onDismissRequest = onDismiss,
            operations = operations
        )
    }
}