package com.lowae.agrreader.ui.page.home.feeds.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.TextFieldDialog
import com.lowae.agrreader.ui.page.common.RouteName
import com.lowae.agrreader.ui.page.home.feeds.FeedOptionViewV1
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.ext.openURL
import com.lowae.agrreader.utils.ext.showToastLong
import com.lowae.component.base.AgrTextButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedInfoPage(
    navController: NavHostController,
    feedOptionViewModel: FeedOptionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val feedOptionUiState = feedOptionViewModel.feedOptionUiState.collectAsStateValue()
    var renameDialogVisible by remember { mutableStateOf(false) }
    val feed = feedOptionUiState.feed

    AgrScaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    FeedbackIconButton(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.close),
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
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FeedOptionViewV1(
                modifier = Modifier.weight(1f),
                feed = feed,
                onFeedNameClick = {
                    renameDialogVisible = true
                },
                onFeedUrlClick = {
                    context.openURL(feed?.url)
                },
                onFeedUrlLongClick = {
                    if (feedOptionViewModel.rssRepository.get().feedOperation) {
                        feedOptionViewModel.showFeedUrlDialog()
                    }
                },
                selectedAllowNotificationPreset = feedOptionUiState.feed?.isNotification ?: false,
                selectedContentSourceTypePreset = feedOptionUiState.feed?.sourceType
                    ?: Feed.SOURCE_TYPE_FULL_CONTENT,
                allowNotificationPresetOnClick = {
                    feedOptionViewModel.changeAllowNotificationPreset()
                },
                contentSourceTypePresetOnClick = { sourceType ->
                    feedOptionViewModel.changeParseFullContentPreset(sourceType)
                },
                interceptionResourceLoad = feedOptionUiState.feed?.interceptionResource ?: false,
                interceptionResourceLoadClick = {
                    feedOptionViewModel.changeInterceptionResource()
                },
                onTranslationLanguageChange = {
                    feedOptionViewModel.changeTranslationLanguage(it)
                },
            )
            Spacer(modifier = Modifier.height(6.dp))
            AgrTextButton(
                text = stringResource(R.string.clear_articles),
                icon = Icons.Outlined.ClearAll
            ) {
                feedOptionViewModel.showClearDialog()
            }
            AgrTextButton(
                text = stringResource(R.string.unsubscribe),
                icon = Icons.Outlined.DeleteOutline
            ) {
                feedOptionViewModel.showDeleteDialog()
            }
        }
    }

    TextFieldDialog(
        visible = feedOptionUiState.changeUrlDialogVisible,
        title = stringResource(R.string.change_url),
        icon = Icons.Outlined.Edit,
        initialValue = feed?.url.orEmpty(),
        placeholder = stringResource(R.string.feed_or_site_url),
        onDismissRequest = { feedOptionViewModel.hideFeedUrlDialog() },
        onConfirm = { feedOptionViewModel.changeFeedUrl(it) },
    )

    ClearFeedDialog(feedName = feed?.name ?: "")

    DeleteFeedDialog(feedName = feed?.name ?: "") {
        feedOptionViewModel.delete {
            feedOptionViewModel.hideDeleteDialog()
            AgrReaderApp.application.showToastLong(R.string.delete_toast, feed?.name.orEmpty())
            navController.popBackStack()
        }
    }
    RenameFeedDialog(
        visible = renameDialogVisible,
        feedName = feed?.name.orEmpty(),
        onDismiss = { renameDialogVisible = false },
        onConfirm = {
            feedOptionViewModel.renameFeed(it)
            renameDialogVisible = false
        })
}