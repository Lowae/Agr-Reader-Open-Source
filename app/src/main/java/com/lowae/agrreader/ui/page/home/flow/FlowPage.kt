package com.lowae.agrreader.ui.page.home.flow

import RYExtensibleVisibility
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PlaylistAddCheck
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.model.article.ArticleFlowItem
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.general.FilterState
import com.lowae.agrreader.data.model.general.MarkAsReadConditions
import com.lowae.agrreader.data.model.general.title
import com.lowae.agrreader.data.model.preference.ArticleSwipeOperation
import com.lowae.agrreader.data.model.preference.LocalArticleLeftSwipeOperation
import com.lowae.agrreader.data.model.preference.LocalArticleRightSwipeOperation
import com.lowae.agrreader.data.model.preference.LocalArticleSortByOldest
import com.lowae.agrreader.data.model.preference.LocalAutoReadOnScroll
import com.lowae.agrreader.data.model.preference.LocalFeedLandscapeMode
import com.lowae.agrreader.data.model.preference.LocalMarkAllReadConfirm
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.PullRefresh
import com.lowae.agrreader.ui.list.ArticleItemPresenter
import com.lowae.agrreader.ui.list.ArticleList
import com.lowae.agrreader.ui.list.ArticleListPresenter
import com.lowae.agrreader.ui.page.common.ReadingPagerRouter
import com.lowae.agrreader.ui.page.common.RouteName
import com.lowae.agrreader.ui.page.home.article.FlowArticleOperationBottomDrawer
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.tap
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowPage(
    navController: NavHostController,
    flowViewModel: FlowViewModel = hiltViewModel()
) {
    val articleLeftSwipe = LocalArticleLeftSwipeOperation.current.value
    val articleRightSwipe = LocalArticleRightSwipeOperation.current.value
    val sortByOldest = LocalArticleSortByOldest.current.value
    val isLandscapeMode = LocalFeedLandscapeMode.current.value
    val markAllReadConfirm = LocalMarkAllReadConfirm.current.value

    val filterUiState = flowViewModel.filterUiState.collectAsStateValue()
    val flowUiState = flowViewModel.flowUiState.collectAsStateValue()
    val searchContent = flowUiState.searchContent.collectAsStateValue()
    val pagingItems = flowUiState.articleFlow.collectAsStateValue()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var onSearch by rememberSaveable { mutableStateOf(false) }
    var markAsRead by remember { mutableStateOf(false) }
    var styleBottomDrawerVisible by remember { mutableStateOf(false) }
    var articleOperation by remember { mutableStateOf<ArticleWithFeed?>(null) }
    val flowTitle = filterUiState.title
    val view = LocalView.current
    AgrScaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.mediumTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.surface),
                title = {
                    Text(
                        text = flowTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    FeedbackIconButton(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    ) {
                        flowViewModel.inputSearchContent("")
                        if (navController.previousBackStackEntry == null) {
                            navController.navigate(RouteName.FEEDS) {
                                launchSingleTop = true
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                },
                actions = {
                    FeedbackIconButton(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.search),
                        tint = if (onSearch) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    ) { onSearch = !onSearch }
                    FeedbackIconButton(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = stringResource(R.string.style),
                        tint = if (styleBottomDrawerVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    ) {
                        styleBottomDrawerVisible = true
                    }
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !filterUiState.filter.isStarred(),
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingActionButton(onClick = {
                    if (markAllReadConfirm) {
                        markAsRead = true
                    } else {
                        flowViewModel.markAsRead(
                            ArticleMarkReadAction(
                                groupId = filterUiState.group?.id,
                                feedId = filterUiState.feed?.id,
                                before = MarkAsReadConditions.All.toDate(),
                            )
                        )
                        navController.popBackStack()
                    }
                    view.tap()
                }) {
                    Icon(
                        Icons.Rounded.PlaylistAddCheck,
                        contentDescription = stringResource(R.string.mark_all_as_read)
                    )
                }

            }
        },
        bottomBar = {
            FlowBottomBar(
                scrollBehavior = scrollBehavior,
                filter = filterUiState.filter,
            ) {
                view.tap(false)
                flowViewModel.changeFilter(filterUiState.copy(filter = it))
            }
        }
    ) { padding ->
        PullRefresh(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            refreshing = flowUiState.isRefreshing,
            onRefresh = {
                flowViewModel.syncFeed(filterUiState)
            }
        ) {
            Column {
                RYExtensibleVisibility(visible = onSearch) {
                    SearchBar(
                        value = searchContent,
                        placeholder = when {
                            filterUiState.group != null -> stringResource(
                                R.string.search_for, flowTitle, filterUiState.group.name
                            )

                            filterUiState.feed != null -> stringResource(
                                R.string.search_for, flowTitle, filterUiState.feed.name
                            )

                            else -> stringResource(R.string.search_for, flowTitle)
                        },
                        onValueChange = {
                            flowViewModel.inputSearchContent(it)
                        },
                        onClose = {
                            onSearch = false
                            flowViewModel.inputSearchContent("")
                        }
                    )
                }

                ArticleList(
                    items = pagingItems,
                    itemPresenter = ArticleItemPresenter(
                        articleLeftSwipe,
                        articleRightSwipe,
                        onLeftSwipe = { operation, articleWithFeed ->
                            onArticleSwipe(operation, articleWithFeed, flowViewModel)
                        },
                        onRightSwipe = { operation, articleWithFeed ->
                            onArticleSwipe(operation, articleWithFeed, flowViewModel)
                        },
                        onClick = {
                            if (isLandscapeMode) {
                                flowViewModel.updateReadingArticle(it)
                            } else {
                                ReadingPagerRouter.navigate(navController, it.article.id) {
                                    launchSingleTop = true
                                }
                            }
                        },
                        onLongClick = { articleOperation = it },
                        isSelected = { flowUiState.readingArticleState.value?.article?.id == it.article.id }
                    ),
                    listPresenter = ArticleListPresenter(
                        readingArticleStateFlow = flowUiState.readingArticleState,
                        onMarkAllRead = {
                            flowViewModel.markAsRead(
                                ArticleMarkReadAction(
                                    filterUiState.group?.id,
                                    filterUiState.feed?.id,
                                    before = it.article.date,
                                    latest = sortByOldest.not()
                                )
                            )
                        },
                        onLoadMore = {
                            flowViewModel.loadMore()
                        }
                    )
                )
            }
        }
    }
    MarkAsReadDialog(
        visible = markAsRead,
        onConfirm = {
            markAsRead = false
            flowViewModel.markAsRead(
                ArticleMarkReadAction(
                    groupId = filterUiState.group?.id,
                    feedId = filterUiState.feed?.id,
                    before = MarkAsReadConditions.All.toDate(),
                )
            )
            navController.popBackStack()
        },
        onDismiss = {
            markAsRead = false
        },
        message = stringResource(R.string.mark_all_as_read_description, flowTitle)
    )
    FlowPageStyleBottomDrawer(
        visible = styleBottomDrawerVisible,
        onDismissRequest = {
            styleBottomDrawerVisible = false
        },
        onArticleSortClick = {
            flowViewModel.changeArticleSort(it)
        })
    FlowArticleOperationBottomDrawer(
        flowViewModel,
        articleOperation,
        onDismiss = { articleOperation = null },
    )
}

@OptIn(FlowPreview::class)
@Composable
fun MarkReadOnScroll(
    vararg key: Any?,
    firstVisibleItemIndexFlow: Flow<Int>,
    pagingItems: List<ArticleFlowItem>,
    onMarkAllRead: (ArticleWithFeed) -> Unit
) {
    if (LocalAutoReadOnScroll.current.value.not()) return
    var maxScrollPosition by rememberSaveable(*key) { mutableIntStateOf(0) }
    LaunchedEffect(*key) {
        firstVisibleItemIndexFlow.debounce(1000).collectLatest { pos ->
            if (pos > maxScrollPosition) {
                maxScrollPosition = pos
            }
        }
    }
    LaunchedEffect(maxScrollPosition) {
        val firstVisibleArticle =
            (if (pagingItems.getOrNull(maxScrollPosition) !is ArticleFlowItem.Article) {
                pagingItems.getOrNull(maxScrollPosition - 1)
            } else {
                pagingItems.getOrNull(maxScrollPosition)
            }) as? ArticleFlowItem.Article
        if (firstVisibleArticle != null) {
            onMarkAllRead(firstVisibleArticle.articleWithFeed)
        }
    }
}

private fun onArticleSwipe(
    swipeOperation: ArticleSwipeOperation,
    articleWithFeed: ArticleWithFeed,
    flowViewModel: FlowViewModel
) {
    when (swipeOperation) {
        ArticleSwipeOperation.NONE -> Unit
        ArticleSwipeOperation.READ -> flowViewModel.markAsRead(
            ArticleMarkReadAction(
                articleId = articleWithFeed.article.id,
                before = MarkAsReadConditions.All.toDate(),
                isUnread = articleWithFeed.article.isUnread.not()
            )
        )

        ArticleSwipeOperation.STAR -> flowViewModel.markAsStarred(
            articleWithFeed.article.id,
            articleWithFeed.article.isStarred.not()
        )
    }
}

private val FilterState.title: String
    get() {
        return when {
            this.group != null -> this.group.name
            this.feed != null -> this.feed.name
            else -> this.filter.title
        }
    }