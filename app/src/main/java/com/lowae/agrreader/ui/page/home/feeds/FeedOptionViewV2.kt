package com.lowae.agrreader.ui.page.home.feeds

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.EditNotifications
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.feed.FeedSourceType
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.repository.NotificationHelper
import com.lowae.agrreader.translator.request.TranslatorApi
import com.lowae.agrreader.ui.component.FeedIcon
import com.lowae.agrreader.ui.component.base.AgrDialog
import com.lowae.agrreader.ui.component.base.RYSelectionChip
import com.lowae.agrreader.ui.component.base.RYSwitch
import com.lowae.agrreader.ui.component.base.RadioDialog
import com.lowae.agrreader.ui.component.base.RadioDialogOption
import com.lowae.agrreader.ui.component.base.Subtitle
import com.lowae.agrreader.ui.page.settings.SettingItem
import com.lowae.agrreader.ui.theme.Shape12
import com.lowae.agrreader.utils.NoOp
import com.lowae.agrreader.utils.NoOp1

@Composable
fun FeedOptionViewV2(
    modifier: Modifier = Modifier,
    feed: Feed?,
    onFeedNameClick: () -> Unit = NoOp,
    onFeedUrlClick: () -> Unit = NoOp,
    onFeedUrlLongClick: () -> Unit = NoOp,
    selectedAllowNotificationPreset: Boolean = true,
    selectedContentSourceTypePreset: Int = Feed.SOURCE_TYPE_FULL_CONTENT,
    allowNotificationPresetOnClick: () -> Unit = NoOp,
    contentSourceTypePresetOnClick: (Int) -> Unit = NoOp1,
    interceptionResourceLoad: Boolean = false,
    interceptionResourceLoadClick: () -> Unit = NoOp,
    groups: List<Group> = emptyList(),
    selectedGroupId: String = "",
    onGroupClick: (Group) -> Unit = {},
    onAddNewGroup: () -> Unit = {},
    onTranslationLanguageChange: (Pair<String, String>?) -> Unit = {}
) {
    feed ?: return
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        if (groups.isNotEmpty() && selectedGroupId.isEmpty()) onGroupClick(groups.first())
    }

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        OptionSummaryView(
            iconUrl = feed.icon.orEmpty(),
            name = feed.name,
            description = feed.description,
            feedUrl = feed.url,
            onFeedNameClick = onFeedNameClick,
            onFeedUrlClick = onFeedUrlClick,
            onFeedUrlLongClick = onFeedUrlLongClick
        )

        OptionPresetView(
            selectedAllowNotificationPreset = selectedAllowNotificationPreset,
            selectedContentSourceTypePreset = selectedContentSourceTypePreset,
            allowNotificationPresetOnClick = allowNotificationPresetOnClick,
            contentSourceTypePresetOnClick = contentSourceTypePresetOnClick,
            interceptionResourceLoad = interceptionResourceLoad,
            interceptionResourceLoadClick = interceptionResourceLoadClick
        )
        TranslationSettings(feed.translationLanguage, onTranslationLanguageChange)
        OptionGroupView(
            groups = groups,
            selectedGroupId = selectedGroupId,
            onGroupClick = onGroupClick,
            onAddNewGroup = onAddNewGroup,
        )
    }
}

@Composable
fun FeedOptionViewV1(
    modifier: Modifier = Modifier,
    feed: Feed?,
    onFeedNameClick: () -> Unit = NoOp,
    onFeedUrlClick: () -> Unit = NoOp,
    onFeedUrlLongClick: () -> Unit = NoOp,
    selectedAllowNotificationPreset: Boolean = true,
    selectedContentSourceTypePreset: Int = Feed.SOURCE_TYPE_FULL_CONTENT,
    allowNotificationPresetOnClick: () -> Unit = NoOp,
    contentSourceTypePresetOnClick: (Int) -> Unit = NoOp1,
    interceptionResourceLoad: Boolean = false,
    interceptionResourceLoadClick: () -> Unit = NoOp,
    onTranslationLanguageChange: (Pair<String, String>?) -> Unit = {}
) {
    feed ?: return
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        OptionSummaryView(
            iconUrl = feed.icon.orEmpty(),
            name = feed.name,
            description = feed.description,
            feedUrl = feed.url,
            onFeedNameClick = onFeedNameClick,
            onFeedUrlClick = onFeedUrlClick,
            onFeedUrlLongClick = onFeedUrlLongClick
        )

        OptionPresetView(
            selectedAllowNotificationPreset = selectedAllowNotificationPreset,
            selectedContentSourceTypePreset = selectedContentSourceTypePreset,
            allowNotificationPresetOnClick = allowNotificationPresetOnClick,
            contentSourceTypePresetOnClick = contentSourceTypePresetOnClick,
            interceptionResourceLoad = interceptionResourceLoad,
            interceptionResourceLoadClick = interceptionResourceLoadClick
        )
        TranslationSettings(feed.translationLanguage, onTranslationLanguageChange)
    }
}

@Composable
fun OptionSummaryView(
    iconUrl: String,
    iconSize: Dp = 48.dp,
    name: String?,
    description: String?,
    feedUrl: String,
    onFeedNameClick: () -> Unit,
    onFeedUrlClick: () -> Unit,
    onFeedUrlLongClick: () -> Unit
) {
    FeedIcon(feedName = name ?: "", feedIcon = iconUrl, size = iconSize)
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        modifier = Modifier
            .clip(Shape12)
            .clickable(onClick = onFeedNameClick),
        text = name ?: stringResource(R.string.unknown),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    if (description.isNullOrBlank().not()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }

    EditableUrl(
        text = feedUrl,
        onClick = onFeedUrlClick,
        onLongClick = onFeedUrlLongClick,
    )
}

@Composable
fun OptionPresetView(
    title: @Composable () -> Unit = {
        Subtitle(text = stringResource(R.string.preset))
    },
    selectedAllowNotificationPreset: Boolean = false,
    allowNotificationPresetOnClick: () -> Unit,
    @FeedSourceType selectedContentSourceTypePreset: Int,
    contentSourceTypePresetOnClick: (Int) -> Unit,
    interceptionResourceLoad: Boolean = false,
    interceptionResourceLoadClick: () -> Unit,
) {
    var gotoNotificationInfoDialogVisible by remember { mutableStateOf(false) }
    title()
    SourceTypeItem(selectedContentSourceTypePreset, contentSourceTypePresetOnClick)
    SettingItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        title = stringResource(R.string.allow_notification),
        titleStyle = MaterialTheme.typography.bodyLarge,
        tooltip = stringResource(R.string.feed_info_allow_notification_tooltip),
        icon = Icons.Outlined.Notifications,
    ) {
        RYSwitch(activated = selectedAllowNotificationPreset) {
            if (NotificationManagerCompat.from(AgrReaderApp.application)
                    .areNotificationsEnabled()
            ) {
                allowNotificationPresetOnClick()
            } else {
                gotoNotificationInfoDialogVisible = true
            }
        }
    }
    SettingItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        title = stringResource(R.string.interception_resource_load),
        desc = stringResource(R.string.interception_resource_load_desc),
        titleStyle = MaterialTheme.typography.bodyLarge,
        icon = Icons.Outlined.BrokenImage,
    ) {
        RYSwitch(activated = interceptionResourceLoad) { interceptionResourceLoadClick() }
    }
    AgrDialog(
        visible = gotoNotificationInfoDialogVisible,
        onConfirm = {
            NotificationHelper.gotoNotificationSettings(AgrReaderApp.application)
            gotoNotificationInfoDialogVisible = false

        },
        onDismiss = {
            gotoNotificationInfoDialogVisible = false
        },
        icon = {
            Icon(imageVector = Icons.Rounded.EditNotifications, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.notification_request_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.notification_request_dialog_content))
        },
    )
}

private const val LANGUAGE_DIALOG_TYPE_NONE = 0
private const val LANGUAGE_DIALOG_TYPE_SOURCE = 1
private const val LANGUAGE_DIALOG_TYPE_TARGET = 2

@Composable
private fun ColumnScope.TranslationSettings(
    translationLanguage: Pair<String, String>?,
    onTranslationLanguageChange: (Pair<String, String>?) -> Unit
) {
    var translationLanguageDialogType by remember { mutableIntStateOf(LANGUAGE_DIALOG_TYPE_NONE) }
    Subtitle(text = stringResource(R.string.interactive_translation_setting_title))

    SettingItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        title = stringResource(R.string.article_translate_setting),
        titleStyle = MaterialTheme.typography.bodyLarge,
        icon = Icons.Outlined.Translate,
    ) {
        RYSwitch(activated = translationLanguage != null) { activated ->
            onTranslationLanguageChange(if (activated) null else TranslatorApi.DEFAULT_LANGUAGE)
        }
    }

    if (translationLanguage != null) {
        val source = translationLanguage.first
        val target = translationLanguage.second
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ElevatedAssistChip(
                onClick = {
                    translationLanguageDialogType = LANGUAGE_DIALOG_TYPE_SOURCE
                },
                label = {
                    Text(
                        text = TranslatorApi.toLanguageDesc(source)
                    )
                })

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
            ElevatedAssistChip(
                onClick = {
                    translationLanguageDialogType = LANGUAGE_DIALOG_TYPE_TARGET
                },
                label = {
                    Text(
                        text = TranslatorApi.toLanguageDesc(target)
                    )
                })
        }
        RadioDialog(
            visible = translationLanguageDialogType != LANGUAGE_DIALOG_TYPE_NONE,
            title = stringResource(R.string.article_translate_language_setting),
            options = (if (translationLanguageDialogType == LANGUAGE_DIALOG_TYPE_TARGET) TranslatorApi.LANGUAGES.filter { it.key == TranslatorApi.DEFAULT_TARGET_LANGUAGE } else TranslatorApi.LANGUAGES.filter { it.key != target }).map {
                RadioDialogOption(
                    text = it.value,
                    selected = it.key == if (translationLanguageDialogType == LANGUAGE_DIALOG_TYPE_SOURCE) source else target,
                ) {
                    if (translationLanguageDialogType == LANGUAGE_DIALOG_TYPE_SOURCE) {
                        onTranslationLanguageChange(Pair(it.key, target))
                    } else {
                        onTranslationLanguageChange(Pair(source, it.key))
                    }
                }
            }
        ) {
            translationLanguageDialogType = LANGUAGE_DIALOG_TYPE_NONE
        }
    }


}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.OptionGroupView(
    title: @Composable () -> Unit = {
        Subtitle(text = stringResource(R.string.groups))
    },
    groups: List<Group>,
    selectedGroupId: String,
    onGroupClick: (group: Group) -> Unit = {},
    canAddNewGroup: Boolean = true,
    onAddNewGroup: () -> Unit = {},
) {
    title()
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        groups.forEach {
            RYSelectionChip(
                modifier = Modifier.padding(end = 6.dp),
                content = it.name,
                selected = it.id == selectedGroupId,
            ) {
                onGroupClick(it)
            }
        }
        if (canAddNewGroup) {
            NewGroupButton(onAddNewGroup)
        }
    }
}

@Composable
private fun NewGroupButton(onAddNewGroup: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(top = 6.dp)
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onAddNewGroup() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.Outlined.Add,
            contentDescription = stringResource(R.string.create_new_group),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EditableUrl(
    text: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                ),
            text = text,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SourceTypeItem(sourceType: Int, parseFullContentPresetOnClick: (Int) -> Unit) {
    var dropMenu by remember { mutableStateOf(false) }
    SettingItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        title = stringResource(R.string.parse_full_content),
        titleStyle = MaterialTheme.typography.bodyLarge,
        tooltip = stringResource(R.string.feed_info_article_source_tooltip),
        icon = Icons.AutoMirrored.Outlined.Article,
    ) {
        Row(modifier = Modifier.clickable {
            dropMenu = true
        }) {
            Text(text = Feed.sourceTypeStr(sourceType))
            Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = dropMenu,
            onDismissRequest = { dropMenu = false },
            offset = DpOffset(0.dp, (-24).dp),
        ) {
            DropdownMenuItem(text = {
                Text(text = Feed.sourceTypeStr(Feed.SOURCE_TYPE_RAW_DESCRIPTION))
            }, onClick = {
                dropMenu = false
                parseFullContentPresetOnClick(Feed.SOURCE_TYPE_RAW_DESCRIPTION)
            })
            DropdownMenuItem(text = {
                Text(text = Feed.sourceTypeStr(Feed.SOURCE_TYPE_FULL_CONTENT))
            }, onClick = {
                dropMenu = false
                parseFullContentPresetOnClick(Feed.SOURCE_TYPE_FULL_CONTENT)
            })
            DropdownMenuItem(text = {
                Text(text = Feed.sourceTypeStr(Feed.SOURCE_TYPE_WEB))
            }, onClick = {
                dropMenu = false
                parseFullContentPresetOnClick(Feed.SOURCE_TYPE_WEB)
            })
        }
    }
}