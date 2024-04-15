package com.lowae.agrreader.ui.page.home.feeds

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ImportExport
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.MenuOpen
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.account.AccountType
import com.lowae.agrreader.data.model.feed.StatusCount
import com.lowae.agrreader.data.model.general.FilterState
import com.lowae.agrreader.data.model.general.title
import com.lowae.agrreader.data.model.preference.LocalFeedGroupExpandState
import com.lowae.agrreader.data.model.preference.LocalFeedOnlyCountUnread
import com.lowae.agrreader.data.provider.RssOperation
import com.lowae.agrreader.data.repository.isSyncing
import com.lowae.agrreader.data.repository.progress
import com.lowae.agrreader.ui.component.base.AgrDialog
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.EmptyPlaceHolder
import com.lowae.agrreader.ui.component.base.ExpandableFloatingActionButton
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.FloatingActionButtonItem
import com.lowae.agrreader.ui.component.base.LogoText
import com.lowae.agrreader.ui.component.base.PullRefresh
import com.lowae.agrreader.ui.component.base.SpeedDialState
import com.lowae.agrreader.ui.component.base.rememberExpandableFloatingActionButtonState
import com.lowae.agrreader.ui.component.showProCheckDialog
import com.lowae.agrreader.ui.page.common.FeedInfoRouter
import com.lowae.agrreader.ui.page.common.FlowRouter
import com.lowae.agrreader.ui.page.common.GroupInfoRouter
import com.lowae.agrreader.ui.page.common.RouteName
import com.lowae.agrreader.ui.page.common.TodayOfUnreadFlowRouter
import com.lowae.agrreader.ui.page.home.HomeViewModel
import com.lowae.agrreader.ui.page.home.feeds.subscribe.SubscribeViewModel
import com.lowae.agrreader.utils.ext.CurrentAccountType
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.ext.getDayOfMonth
import com.lowae.agrreader.utils.ext.isScrollingUp
import com.lowae.agrreader.utils.ext.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CONTENT_TYPE_FEED_GROUP = "lazy_row_group"
private const val CONTENT_TYPE_FEED_ITEM = "lazy_row_feed"
private const val CONTENT_TYPE_FEED_GAP = "lazy_row_gap"


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedsContent(
    navController: NavHostController,
    feedsViewModel: FeedsViewModel,
    subscribeViewModel: SubscribeViewModel,
    homeViewModel: HomeViewModel,
    drawerState: DrawerState,
) {
    val defaultFeedGroupExpandState = LocalFeedGroupExpandState.current.value
    val isActivePro = false
    val feedOnlyCountUnread = LocalFeedOnlyCountUnread.current.value

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val feedsUiState = feedsViewModel.feedsUiState.collectAsStateValue()
    val filterUiState = feedsViewModel.filterUiState.collectAsStateValue()
    val groupWithFeedList = feedsUiState.groupWithFeedList.collectAsStateValue()
    val syncProgress = homeViewModel.syncProgressState.collectAsStateValue()
    val totalStatusCount = feedsUiState.totalStatusCountState.collectAsStateValue()
    val todayUnreadCount = feedsUiState.todayUnreadCount.collectAsStateValue()
    val groupsVisible: SnapshotStateMap<String, Boolean> = feedsUiState.groupsVisible
    val lazyListState = rememberLazyListState()
    val feedsSum = groupWithFeedList.sumOf { it.feeds.size }
    val showEmpty by remember(groupWithFeedList) {
        derivedStateOf { feedsUiState.account != null && feedsSum == 0 }
    }
    var isRefreshing by remember { mutableStateOf(false) }
    var cancelSyncDialogVisible by remember { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null || uri.toString().isBlank()) {
                toast("获取文件失败")
            } else {
                context.contentResolver.openInputStream(uri)?.let { inputStream ->
                    subscribeViewModel.importFromInputStream(inputStream)
                }
            }
        }

    AgrScaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            FeedContentTopBar(
                syncProgress.progress,
                syncProgress.isSyncing,
                onSettingClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            ) {
                if (CurrentAccountType == AccountType.Local.id && syncProgress.isSyncing) {
                    cancelSyncDialogVisible = true
                } else {
                    homeViewModel.sync()
                }
            }
        },
    ) { paddingValues ->
        PullRefresh(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            refreshing = isRefreshing,
            enable = syncProgress.isSyncing.not(),
            onRefresh = {
                homeViewModel.sync()
                scope.launch {
                    isRefreshing = true
                    delay(1000)
                    isRefreshing = false
                }
            }
        ) {
            LazyColumn(state = lazyListState) {
                item {
                    FeedBaseItem(
                        icon = {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape),
                                imageVector = Icons.Rounded.MenuBook,
                                contentDescription = null
                            )
                        },
                        title = filterUiState.filter.title,
                        count = totalStatusCount.toCount(filterUiState, feedOnlyCountUnread),
                        cornerType = FeedItemCornerType.TOP,
                        isExpanded = { true },
                        onClick = {
                            navigationFlowPage(
                                navController,
                                filterUiState.copy(group = null, feed = null)
                            )
                        }
                    ) {}
                }
                item {
                    FeedBaseItem(
                        icon = {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp),
                                    imageVector = Icons.Rounded.CalendarToday,
                                    contentDescription = null
                                )
                                Text(
                                    modifier = Modifier.padding(top = 2.dp, end = 1.dp),
                                    text = getDayOfMonth().toString(),
                                    fontFamily = FontFamily.SansSerif,
                                    textAlign = TextAlign.Center,
                                    fontSize = 11.sp,
                                    lineHeight = 11.sp
                                )
                            }
                        },
                        title = stringResource(R.string.unread_of_today),
                        count = todayUnreadCount,
                        cornerType = FeedItemCornerType.BOTTOM,
                        isExpanded = { true },
                        onClick = {
                            TodayOfUnreadFlowRouter.navigate(navController)
                        }
                    ) {}

                }

                groupWithFeedList.forEachIndexed { index, groupWithFeed ->
                    stickyHeader(
                        key = groupWithFeed.group.key,
                        contentType = CONTENT_TYPE_FEED_GROUP
                    ) {
                        GroupItem(
                            name = groupWithFeed.group.name,
                            count = groupWithFeed.group.count.toCount(
                                filterUiState,
                                feedOnlyCountUnread
                            ),
                            isExpanded = {
                                groupsVisible.getOrPut(groupWithFeed.group.id) { defaultFeedGroupExpandState }
                            },
                            onExpanded = {
                                groupsVisible[groupWithFeed.group.id] =
                                    (groupsVisible[groupWithFeed.group.id]
                                        ?: defaultFeedGroupExpandState).not()
                            },
                            groupOnClick = {
                                navigationFlowPage(
                                    navController,
                                    filterUiState.copy(group = groupWithFeed.group, feed = null)
                                )
                            },
                            groupOnLongClick = {
                                GroupInfoRouter.navigate(navController, groupWithFeed.group.id)
                            }
                        )
                    }
                    groupWithFeed.feeds.forEachIndexed { feedIndex, feed ->
                        item(
                            key = feed.key,
                            contentType = CONTENT_TYPE_FEED_ITEM
                        ) {
                            FeedItem(
                                feed = feed,
                                count = feed.count.toCount(filterUiState, feedOnlyCountUnread),
                                cornerType = FeedItemCornerType.getFeedItemCornerType(
                                    groupWithFeed.feeds,
                                    feedIndex
                                ),
                                isExpanded = { groupsVisible.getOrPut(feed.groupId) { true } },
                                onClick = {
                                    navigationFlowPage(
                                        navController,
                                        filterUiState.copy(group = null, feed = feed)
                                    )
                                },
                                onLongClick = {
                                    FeedInfoRouter.navigate(navController, feed.id)
                                }
                            )
                        }
                    }
                }
                item(contentType = CONTENT_TYPE_FEED_GAP) {
                    Spacer(modifier = Modifier.height(128.dp))
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
            if (showEmpty) {
                EmptyPlaceHolder(
                    modifier = Modifier.fillMaxSize(),
                    tips = stringResource(R.string.placeholder_empty_feeds)
                )
            }
            FeedFabContent(
                lazyListState.isScrollingUp(),
                onStateChange = {
                    if (isActivePro.not() && feedsSum >= 50) {
                        showProCheckDialog(
                            navController,
                            navController.context.getString(R.string.agr_reader_pro_dialog_max_subscribe_content)
                        )
                        true
                    } else {
                        false
                    }
                },
                onImportFromUrlClick = {
                    if (AccountType.checkOperation(CurrentAccountType, RssOperation.IMPORT_URL)) {
                        navController.navigate(RouteName.SUBSCRIBE)
                    }
                },
                onImportFromOpmlClick = {
                    if (AccountType.checkOperation(CurrentAccountType, RssOperation.IMPORT_OPML)) {
                        try {
                            launcher.launch(arrayOf("*/*"))
                        } catch (e: Exception) {
                            toast("打开文件管理失败")
                            e.printStackTrace()
                        }
                    }
                }
            )
        }
    }

    AgrDialog(
        visible = cancelSyncDialogVisible,
        onConfirm = {
            homeViewModel.cancelSync()
            cancelSyncDialogVisible = false
        },
        onDismiss = { cancelSyncDialogVisible = false },
        text = "是否停止更新订阅源"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedContentTopBar(
    progressing: String?,
    isSyncing: Boolean,
    onSettingClick: () -> Unit,
    onRefreshClick: () -> Unit
) {

    @Composable
    fun rotateComposable(): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val twinCircleAnimation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ), label = ""
        )

        return twinCircleAnimation
    }

    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.surface),
        title = {
            Row(modifier = Modifier.wrapContentWidth(), verticalAlignment = Alignment.Bottom) {
                LogoText(
                    text = stringResource(id = R.string.app_name),
                )
                if (progressing != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.padding(vertical = 3.dp),
                        text = progressing,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

            }
        },
        navigationIcon = {
            FeedbackIconButton(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Rounded.MenuOpen,
                contentDescription = stringResource(R.string.settings),
                tint = MaterialTheme.colorScheme.onSurface,
                onClick = onSettingClick
            )
        },
        actions = {
            FeedbackIconButton(
                modifier = Modifier.rotate(if (isSyncing) rotateComposable() else 0F),
                imageVector = Icons.Rounded.Refresh,
                contentDescription = stringResource(R.string.refresh),
                tint = MaterialTheme.colorScheme.primary,
                onClick = onRefreshClick
            )
        },
    )
}

@Composable
private fun BoxScope.FeedFabContent(
    visible: Boolean,
    onStateChange: () -> Boolean,
    onImportFromUrlClick: () -> Unit,
    onImportFromOpmlClick: () -> Unit
) {
    val fabState = rememberExpandableFloatingActionButtonState()
    if (visible.not()) {
        fabState.currentState = SpeedDialState.COLLAPSED
    }
    val fabItems = listOf(
        FloatingActionButtonItem(
            Icons.Rounded.RssFeed,
            stringResource(R.string.import_from_url),
            {
                fabState.currentState = SpeedDialState.COLLAPSED
                onImportFromUrlClick()
            },
        ),

        FloatingActionButtonItem(
            Icons.Rounded.ImportExport,
            stringResource(R.string.import_from_opml),
            {
                fabState.currentState = SpeedDialState.COLLAPSED
                onImportFromOpmlClick()
            },
        )
    )
    val padding = 16.dp
    val paddingPx = with(LocalDensity.current) { padding.toPx() }

    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = padding, bottom = padding * 2),
        visible = visible,
        enter = slideInHorizontally { it + paddingPx.toInt() },
        exit = slideOutHorizontally { it + paddingPx.toInt() },
    ) {
        ExpandableFloatingActionButton(
            state = fabState,
            items = fabItems,
            onStateChange = onStateChange,
            expandContent = {
                Icon(Icons.Rounded.Close, contentDescription = "")
            },
            collapsedContent = {
                Icon(Icons.Rounded.Add, contentDescription = "")
            }
        )
    }
}

private fun navigationFlowPage(
    navController: NavHostController,
    filterState: FilterState,
) {
    FlowRouter.navigate(navController, listOf(filterState)) {
        launchSingleTop = true
    }
}

private fun StatusCount.toCount(filterState: FilterState, onlyCountUnread: Boolean): Int {
    return if (filterState.filter.isUnread()) {
        unread
    } else if (filterState.filter.isStarred()) {
        starred
    } else {
        if (onlyCountUnread) unread else all
    }
}