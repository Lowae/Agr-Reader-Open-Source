package com.lowae.agrreader.ui.page.home.feeds.subscribe

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FindInPage
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.account.AccountType
import com.lowae.agrreader.ui.component.base.RYTextField
import com.lowae.agrreader.ui.page.home.feeds.FeedOptionViewV2
import com.lowae.component.base.AgrTextButton

@Composable
fun ColumnScope.LocalAccountTypeSubscribePage(
    subscribeUiState: SubscribeUiState,
    subscribeViewModel: SubscribeViewModel,
    navController: NavHostController
) {
    val feed = subscribeUiState.feed
    val groups = subscribeUiState.groups.collectAsState(initial = emptyList()).value

    AnimatedContent(
        targetState = subscribeUiState.isSearchPage,
        transitionSpec = {
            fadeIn(animationSpec = tween(500, delayMillis = 90)) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(500, delayMillis = 90)
            ) togetherWith fadeOut(animationSpec = tween(90))
        },
        label = ""
    ) { isSearchPage ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isSearchPage) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Rounded.RssFeed,
                    contentDescription = stringResource(R.string.subscribe),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier,
                    text = subscribeUiState.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                RYTextField(
                    readOnly = subscribeUiState.lockLinkInput,
                    value = subscribeUiState.linkContent,
                    singleLine = true,
                    onValueChange = { subscribeViewModel.inputLink(it) },
                    placeholder = stringResource(R.string.feed_or_site_url),
                    errorMessage = subscribeUiState.errorMessage,
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            subscribeViewModel.search()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                )
            } else {
                FeedOptionViewV2(
                    modifier = Modifier.weight(1f),
                    feed = feed,
                    onFeedUrlClick = {},
                    onFeedUrlLongClick = {},
                    selectedAllowNotificationPreset = subscribeUiState.allowNotificationPreset,
                    selectedContentSourceTypePreset = subscribeUiState.contentSourceTypePreset,
                    allowNotificationPresetOnClick = {
                        subscribeViewModel.changeAllowNotificationPreset()
                    },
                    contentSourceTypePresetOnClick = { sourceType ->
                        subscribeViewModel.changeParseFullContentPreset(sourceType)
                    },
                    interceptionResourceLoad = subscribeUiState.interceptionResource,
                    interceptionResourceLoadClick = {
                        subscribeViewModel.changeInterceptionResource()
                    },
                    groups = groups,
                    selectedGroupId = subscribeUiState.selectedGroupId,
                    onGroupClick = { group ->
                        subscribeViewModel.selectedGroup(group.id)
                    },
                    onAddNewGroup = {
                        subscribeViewModel.showNewGroupDialog()
                    },
                    onTranslationLanguageChange = {
                        subscribeViewModel.changeTranslationLanguage(it)
                    }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            AgrTextButton(
                enabled = subscribeUiState.lockLinkInput.not(),
                text = if (subscribeUiState.isSearchPage) stringResource(R.string.search) else stringResource(
                    R.string.subscribe
                ),
                icon = if (subscribeUiState.isSearchPage) Icons.Outlined.FindInPage else Icons.Outlined.RssFeed
            ) {
                if (subscribeUiState.isSearchPage) {
                    subscribeViewModel.search()
                } else {
                    subscribeViewModel.subscribe(AccountType.Local)
                    navController.popBackStack()
                }
            }
        }
    }
}