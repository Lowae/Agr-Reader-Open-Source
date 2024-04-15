package com.lowae.agrreader.ui.page.settings.interactive

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistAddCheck
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.SwipeLeft
import androidx.compose.material.icons.outlined.SwipeRight
import androidx.compose.material.icons.outlined.SwipeUp
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.preference.ArticleSwipeOperation
import com.lowae.agrreader.data.model.preference.ArticleSwipePreference
import com.lowae.agrreader.data.model.preference.FeedOnlyCountUnreadPreference
import com.lowae.agrreader.data.model.preference.InitialFilterPreference
import com.lowae.agrreader.data.model.preference.LocalArticleLeftSwipeOperation
import com.lowae.agrreader.data.model.preference.LocalArticleRightSwipeOperation
import com.lowae.agrreader.data.model.preference.LocalAutoReadOnScroll
import com.lowae.agrreader.data.model.preference.LocalClickVibration
import com.lowae.agrreader.data.model.preference.LocalFeedOnlyCountUnread
import com.lowae.agrreader.data.model.preference.LocalInitialFilter
import com.lowae.agrreader.data.model.preference.LocalMarkAllReadConfirm
import com.lowae.agrreader.data.model.preference.LocalVolumePageScroll
import com.lowae.agrreader.data.model.preference.MarkAllReadConfirmPreference
import com.lowae.agrreader.data.model.preference.VolumePageScrollPreference
import com.lowae.agrreader.data.model.preference.not
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.RYSwitch
import com.lowae.agrreader.ui.component.base.Subtitle
import com.lowae.agrreader.ui.page.common.FeedManagementRouter
import com.lowae.agrreader.ui.page.common.InteractiveTranslatorSettingRouter
import com.lowae.agrreader.ui.page.settings.SettingItem
import com.lowae.component.base.popup.SimpleTextDropMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveSettingPage(navController: NavHostController) {
    val clickVibrationPreference = LocalClickVibration.current
    val autoMarkReadOnScroll = LocalAutoReadOnScroll.current
    val articleLeftSwipeOperation = LocalArticleLeftSwipeOperation.current
    val articleRightSwipeOperation = LocalArticleRightSwipeOperation.current
    val initialFilterState = LocalInitialFilter.current
    val volumePageScroll = LocalVolumePageScroll.current
    val markAllReadConfirm = LocalMarkAllReadConfirm.current
    val feedOnlyCountUnread = LocalFeedOnlyCountUnread.current.value

    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var initialFilterDialogVisible by remember { mutableStateOf(false) }
    var leftSwipeDropMenuVisible by remember { mutableStateOf(false) }
    var rightSwipeDropMenuVisible by remember { mutableStateOf(false) }
    var volumePageScrollDropMenuVisible by remember { mutableStateOf(false) }

    AgrScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(text = stringResource(R.string.interaction))
                },
                navigationIcon = {
                    FeedbackIconButton(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    ) {
                        navController.popBackStack()
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                SettingItem(
                    title = stringResource(R.string.interactive_hapic_setting_title),
                    desc = stringResource(R.string.interactive_hapic_setting_description),
                    icon = Icons.Outlined.Vibration,
                    onClick = {
                        clickVibrationPreference.not().put(scope)
                    },
                ) {
                    RYSwitch(activated = clickVibrationPreference.value) {
                        clickVibrationPreference.not().put(scope)
                    }
                }
                SettingItem(
                    title = "使用音量键翻页",
                    desc = volumePageScroll.toDesc,
                    icon = Icons.Outlined.KeyboardDoubleArrowDown,
                    onClick = {
                        volumePageScrollDropMenuVisible = true
                    },
                ) {
                    SimpleTextDropMenu(
                        visible = volumePageScrollDropMenuVisible,
                        data = VolumePageScrollPreference.values,
                        onText = { it.toDesc },
                        onDismiss = { volumePageScrollDropMenuVisible = false }
                    ) {
                        it.put(scope)
                        volumePageScrollDropMenuVisible = false
                    }
                }

                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.interactive_subtitle_rss_feed),
                )
                SettingItem(
                    title = "仅统计未读数和收藏数",
                    desc = "在全部过滤状态下只统计未读数",
                    onClick = {
                        FeedOnlyCountUnreadPreference(feedOnlyCountUnread.not()).put(scope)
                    },
                ) {
                    RYSwitch(activated = feedOnlyCountUnread) {
                        FeedOnlyCountUnreadPreference(feedOnlyCountUnread.not()).put(scope)
                    }
                }
                SettingItem(
                    title = stringResource(R.string.initial_filter_state_title),
                    desc = initialFilterState.toDesc(),
                    icon = Icons.Outlined.FilterAlt,
                    onClick = { initialFilterDialogVisible = true },
                ) {
                    SimpleTextDropMenu(
                        visible = initialFilterDialogVisible,
                        data = InitialFilterPreference.values,
                        onText = { it.toDesc() },
                        onDismiss = { initialFilterDialogVisible = false }
                    ) {
                        it.put(scope)
                        initialFilterDialogVisible = false
                    }
                }
                SettingItem(
                    title = stringResource(R.string.feed_management),
                    icon = Icons.AutoMirrored.Outlined.Sort,
                    onClick = { FeedManagementRouter.navigate(navController) },
                )

                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.interactive_subtitle_article_feed),
                )
                SettingItem(
                    title = "全部标记已读前确认",
                    desc = "标记全部已读前是否进行确认",
                    icon = Icons.AutoMirrored.Outlined.PlaylistAddCheck,
                    onClick = {
                        MarkAllReadConfirmPreference(markAllReadConfirm.value.not()).put(scope)
                    },
                ) {
                    RYSwitch(activated = markAllReadConfirm.value) {
                        MarkAllReadConfirmPreference(markAllReadConfirm.value.not()).put(scope)
                    }
                }
                SettingItem(
                    title = stringResource(R.string.interactive_mark_read_when_scrolling_setting_title),
                    desc = stringResource(R.string.interactive_mark_read_when_scrolling_setting_description),
                    icon = Icons.Outlined.SwipeUp,
                    onClick = {
                        autoMarkReadOnScroll.not().put(scope)
                    },
                ) {
                    RYSwitch(activated = autoMarkReadOnScroll.value) {
                        autoMarkReadOnScroll.not().put(scope)
                    }
                }
                SettingItem(
                    title = stringResource(R.string.interactive_left_swipe_title),
                    desc = articleLeftSwipeOperation.value.toDesc(),
                    icon = Icons.Outlined.SwipeLeft,
                    onClick = {
                        leftSwipeDropMenuVisible = true
                    },
                ) {
                    SimpleTextDropMenu(
                        visible = leftSwipeDropMenuVisible,
                        data = ArticleSwipeOperation.entries,
                        onText = { it.toDesc() },
                        onDismiss = { leftSwipeDropMenuVisible = false }
                    ) {
                        ArticleSwipePreference.LeftSwipe(it).put(scope)
                        leftSwipeDropMenuVisible = false
                    }
                }
                SettingItem(
                    title = stringResource(R.string.interactive_right_swipe_title),
                    desc = articleRightSwipeOperation.value.toDesc(),
                    icon = Icons.Outlined.SwipeRight,
                    onClick = {
                        rightSwipeDropMenuVisible = true
                    },
                ) {
                    SimpleTextDropMenu(
                        visible = rightSwipeDropMenuVisible,
                        data = ArticleSwipeOperation.entries,
                        onText = { it.toDesc() },
                        onDismiss = { rightSwipeDropMenuVisible = false }
                    ) {
                        ArticleSwipePreference.RightSwipe(it).put(scope)
                        rightSwipeDropMenuVisible = false
                    }
                }

                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.interactive_subtitle_reading),
                )
                SettingItem(
                    title = stringResource(R.string.interactive_translation_setting_title),
                    desc = stringResource(R.string.interactive_translation_setting_descrption),
                    icon = Icons.Outlined.Translate,
                    onClick = {
                        InteractiveTranslatorSettingRouter.navigate(navController)
                    },
                )
            }
        }
    }
}