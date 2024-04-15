package com.lowae.agrreader.ui.page.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.EmptyPlaceHolder
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.page.common.ReadingRouter
import com.lowae.agrreader.ui.page.common.RouteName
import com.lowae.agrreader.ui.page.home.flow.ArticleList
import com.lowae.agrreader.utils.ext.collectAsStateValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPage(
    navController: NavHostController,
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val articleLeftSwipe = LocalArticleLeftSwipeOperation.current.value
    val articleRightSwipe = LocalArticleRightSwipeOperation.current.value

    val historyUiState = historyViewModel.historyUIState.collectAsStateValue()
    val historyItems = historyUiState.totalHistories.collectAsStateValue()
    AgrScaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.surface),
                title = {
                    Text(
                        text = stringResource(R.string.recently_read),
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
                },
            )
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ArticleList(
                        historyItems,
                        leftSwipe = articleLeftSwipe,
                        rightSwipe = articleRightSwipe,
                        onLeftSwipe = { operation, articleWithFeed ->
                            onArticleSwipe(operation, articleWithFeed, historyViewModel)
                        },
                        onRightSwipe = { operation, articleWithFeed ->
                            onArticleSwipe(operation, articleWithFeed, historyViewModel)
                        },
                        onClick = {
                            ReadingRouter.navigate(navController, it.article.id) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                if (historyItems.isEmpty()) {
                    EmptyPlaceHolder(modifier = Modifier.fillMaxSize(), tips = stringResource(R.string.placeholder_empty_articles))
                }
            }
        },
    )
}

private fun onArticleSwipe(
    swipeOperation: ArticleSwipeOperation,
    articleWithFeed: ArticleWithFeed,
    historyViewModel: HistoryViewModel
) {
    when (swipeOperation) {
        ArticleSwipeOperation.NONE -> Unit
        ArticleSwipeOperation.READ -> historyViewModel.markAsRead(
            ArticleMarkReadAction(
                articleId = articleWithFeed.article.id,
                before = MarkAsReadConditions.All.toDate(),
                isUnread = articleWithFeed.article.isUnread.not()
            )
        )

        ArticleSwipeOperation.STAR -> historyViewModel.markAsStarred(
            articleWithFeed.article.id,
            articleWithFeed.article.isStarred.not()
        )
    }
}