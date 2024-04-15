package com.lowae.agrreader.ui.page.home.feeds.management

import RYExtensibleVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.DriveFileMove
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.account.AccountType
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.ui.component.base.AgrDialog
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.page.home.feeds.FeedItemCornerType
import com.lowae.agrreader.utils.ext.CurrentAccountType
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.ext.getString
import com.lowae.agrreader.utils.ext.toast
import com.lowae.component.base.popup.DropMenuItem
import com.lowae.component.base.popup.SimpleDropMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedManagementPage(
    navController: NavHostController,
    managementViewModel: FeedManagementViewModel = hiltViewModel()
) {
    val accountType = CurrentAccountType
    val uiState = managementViewModel.uiState.collectAsStateValue()
    val groupFeeds = uiState.groupFeeds.collectAsStateValue()
    val groups = remember(groupFeeds) { groupFeeds.map { it.group } }

    var dropMenuVisible by remember { mutableStateOf(false) }
    var groupSortDialogVisible by remember { mutableStateOf(false) }
    var createNewFolderDialogVisible by remember { mutableStateOf(false) }
    var inSelectedMode by rememberSaveable { mutableStateOf(false) }
    var deleteFeedDialogVisible by remember { mutableStateOf(false) }
    var moveFeedDialogVisible by remember { mutableStateOf(false) }
    var selectGroup by remember { mutableStateOf<Group?>(null) }

    val dropMenuItems = remember {
        listOf(
            DropMenuItem(
                Icons.AutoMirrored.Rounded.Sort,
                text = getString(R.string.group_sort),
                onClick = {
                    groupSortDialogVisible = true
                }),
            DropMenuItem(
                Icons.Rounded.CreateNewFolder,
                text = getString(R.string.create_new_group),
                onClick = {
                    createNewFolderDialogVisible = true
                }),
        )
    }

    AgrScaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.mediumTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.surface),
            title = {
                Text(
                    text = stringResource(R.string.feed_management),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            navigationIcon = {
                FeedbackIconButton(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onSurface
                ) {
                    navController.popBackStack()
                }
            },
            actions = {
                RYExtensibleVisibility(visible = inSelectedMode) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FeedbackIconButton(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.group_sort),
                            tint = MaterialTheme.colorScheme.error
                        ) {
                            if (uiState.selectedFeedSet.isEmpty()) {
                                toast(R.string.feed_management_none_selected)
                            } else {
                                deleteFeedDialogVisible = true
                            }
                        }
                        FeedbackIconButton(
                            imageVector = Icons.AutoMirrored.Rounded.DriveFileMove,
                            contentDescription = stringResource(R.string.move_to_group),
                        ) {
                            if (uiState.selectedFeedSet.isEmpty()) {
                                toast(R.string.feed_management_none_selected)
                            } else {
                                moveFeedDialogVisible = true
                            }
                        }
                    }

                }
                FeedbackIconButton(
                    imageVector = Icons.Rounded.Checklist,
                    contentDescription = stringResource(R.string.selected),
                    tint = if (inSelectedMode) MaterialTheme.colorScheme.primary else LocalContentColor.current
                ) {
                    inSelectedMode = inSelectedMode.not()
                }


                FeedbackIconButton(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(R.string.group_sort)
                ) {
                    dropMenuVisible = true
                }

                SimpleDropMenu(items = dropMenuItems, visible = dropMenuVisible) {
                    dropMenuVisible = false
                }
            }
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            groupFeeds.forEachIndexed { index, groupWithFeed ->
                item(
                    key = groupWithFeed.group.key,
                ) {
                    SelectedGroupItem(
                        name = groupWithFeed.group.name,
                        groupOnLongClick = {
                            selectGroup = groupWithFeed.group
                        }
                    )
                }
                groupWithFeed.feeds.forEachIndexed { feedIndex, feed ->
                    item(
                        key = feed.key,
                    ) {
                        SelectedFeedItem(
                            name = feed.name,
                            icon = feed.icon.orEmpty(),
                            cornerType = FeedItemCornerType.getFeedItemCornerType(
                                groupWithFeed.feeds,
                                feedIndex
                            ),
                            inSelectedMode = inSelectedMode,
                            selected = uiState.selectedFeedSet[feed] == true,
                            onSelected = {
                                managementViewModel.selectFeed(
                                    !(uiState.selectedFeedSet[feed] ?: false), feed
                                )
                            },
                            onClick = {
                                managementViewModel.selectFeed(
                                    !(uiState.selectedFeedSet[feed] ?: false), feed
                                )
                            },
                            onLongClick = {
                                inSelectedMode = !inSelectedMode
                            }
                        )
                    }
                }
            }

        }
    }
    FeedManagementGroupSortDialog(
        visible = groupSortDialogVisible,
        groupWithFeeds = groupFeeds,
        onDismiss = { groupSortDialogVisible = false }
    ) {
        groupSortDialogVisible = false
        managementViewModel.sortGroup(it)
    }
    AgrDialog(
        visible = deleteFeedDialogVisible,
        icon = Icons.Rounded.Delete,
        title = stringResource(id = R.string.unsubscribe),
        text = stringResource(
            R.string.feed_management_unsubscribe_tips,
            AccountType.valueOf(accountType).toDesc()
        ),
        onConfirm = {
            managementViewModel.unSubscribeFeeds(accountType)
            deleteFeedDialogVisible = false
        },
        onDismiss = { deleteFeedDialogVisible = false }
    )

    FeedManagementMoveToGroupDialog(
        visible = moveFeedDialogVisible,
        groups = groups,
        onDismiss = { moveFeedDialogVisible = false },
        onConfirmMove = {
            moveFeedDialogVisible = false
            managementViewModel.moveFeeds(accountType, it)
        }
    )
    if (selectGroup != null) {
        val group = selectGroup!!
        FeedManagementInfoBottomDrawer(
            group,
            onDismissRequest = {
                selectGroup = null
            },
            onRename = { name ->
                managementViewModel.groupRename(accountType, group, name)
                toast(R.string.rename_toast, name)
            },
            onDelete = {
                managementViewModel.groupDelete(accountType, group, it)
                toast(R.string.group_delete_toast, group.name)
            }
        )
    }
    CreateNewGroupDialog(
        visible = createNewFolderDialogVisible,
        onDismiss = { createNewFolderDialogVisible = false }
    ) {
        createNewFolderDialogVisible = false
        managementViewModel.groupCreate(accountType, it)
    }
}