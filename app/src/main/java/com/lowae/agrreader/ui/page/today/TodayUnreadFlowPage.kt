package com.lowae.agrreader.ui.page.today

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.general.MarkAsReadConditions
import com.lowae.agrreader.data.model.preference.ArticleSwipeOperation
import com.lowae.agrreader.data.model.preference.LocalArticleLeftSwipeOperation
import com.lowae.agrreader.data.model.preference.LocalArticleRightSwipeOperation
import com.lowae.agrreader.data.model.preference.LocalArticleSortByOldest
import com.lowae.agrreader.data.model.preference.LocalFeedLandscapeMode
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.list.ArticleItemPresenter
import com.lowae.agrreader.ui.list.ArticleList
import com.lowae.agrreader.ui.list.ArticleListPresenter
import com.lowae.agrreader.ui.page.common.ReadingPagerRouter
import com.lowae.agrreader.ui.page.common.RouteName
import com.lowae.agrreader.ui.page.home.article.TodayOfUnreadArticleOperationBottomDrawer
import com.lowae.agrreader.utils.ext.collectAsStateValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayUnreadFlowPage(
    navController: NavHostController,
    todayUnreadViewModel: TodayUnreadViewModel = hiltViewModel()
) {
    val articleLeftSwipe = LocalArticleLeftSwipeOperation.current.value
    val articleRightSwipe = LocalArticleRightSwipeOperation.current.value
    val sortByOldest = LocalArticleSortByOldest.current.value
    val isLandscapeMode = LocalFeedLandscapeMode.current.value

    val uiState = todayUnreadViewModel.todayUnreadUIState.collectAsStateValue()
    val historyItems = uiState.articleFlow.collectAsStateValue()
    var articleOperation by remember { mutableStateOf<ArticleWithFeed?>(null) }
    AgrScaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.surface),
                title = {
                    Text(
                        text = stringResource(R.string.unread_of_today),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                navigationIcon = {
                    FeedbackIconButton(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    ) {
                        if (navController.previousBackStackEntry == null) {
                            navController.navigate(RouteName.FEEDS) {
                                launchSingleTop = true
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                }
            )
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                ArticleList(
                    items = historyItems,
                    itemPresenter = ArticleItemPresenter(
                        articleLeftSwipe,
                        articleRightSwipe,
                        onLeftSwipe = { operation, articleWithFeed ->
                            onArticleSwipe(operation, articleWithFeed, todayUnreadViewModel)
                        },
                        onRightSwipe = { operation, articleWithFeed ->
                            onArticleSwipe(operation, articleWithFeed, todayUnreadViewModel)
                        },
                        onClick = {
                            if (isLandscapeMode) {
                                todayUnreadViewModel.updateReadingArticle(it)
                            } else {
                                ReadingPagerRouter.navigate(navController, it.article.id) {
                                    launchSingleTop = true
                                }
                            }
                        },
                        onLongClick = { articleOperation = it },
                        isSelected = { it.article.id == todayUnreadViewModel.currentArticleId }
                    ),
                    listPresenter = ArticleListPresenter(
                        readingArticleStateFlow = uiState.readingArticleState,
                        onMarkAllRead = {
                            todayUnreadViewModel.markAsRead(
                                ArticleMarkReadAction(
                                    null,
                                    null,
                                    before = it.article.date,
                                    latest = sortByOldest.not()
                                )
                            )
                        },
                        onLoadMore = {
                            todayUnreadViewModel.loadMore()
                        }
                    )
                )
            }
        },
    )
    TodayOfUnreadArticleOperationBottomDrawer(
        todayUnreadViewModel,
        articleOperation
    ) { articleOperation = null }
}

private fun onArticleSwipe(
    swipeOperation: ArticleSwipeOperation,
    articleWithFeed: ArticleWithFeed,
    todayUnreadViewModel: TodayUnreadViewModel
) {
    when (swipeOperation) {
        ArticleSwipeOperation.NONE -> Unit
        ArticleSwipeOperation.READ -> todayUnreadViewModel.markAsRead(
            ArticleMarkReadAction(
                articleId = articleWithFeed.article.id,
                before = MarkAsReadConditions.All.toDate(),
                isUnread = articleWithFeed.article.isUnread.not()
            )
        )

        ArticleSwipeOperation.STAR -> todayUnreadViewModel.markAsStarred(
            articleWithFeed.article.id,
            articleWithFeed.article.isStarred.not()
        )
    }
}