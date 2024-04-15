package com.lowae.agrreader.ui.page.home.feeds.management

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderDelete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.ui.component.base.LWBottomSheet
import com.lowae.agrreader.ui.component.base.TextFieldDialog
import com.lowae.agrreader.ui.page.home.feeds.drawer.group.DeleteGroupDialog
import com.lowae.agrreader.utils.ext.GroupIdGenerator
import com.lowae.agrreader.utils.ext.getString
import com.lowae.agrreader.utils.ext.toast
import com.lowae.component.base.OperationItem
import com.lowae.component.base.OperationModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedManagementInfoBottomDrawer(
    group: Group,
    onDismissRequest: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val animateToDismiss: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismissRequest()
            }
        }
    }

    var renameGroupDialogVisible by remember { mutableStateOf(false) }
    var deleteGroupDialogVisible by remember { mutableStateOf(false) }

    val operations = rememberGroupOperations(onRename = {
        renameGroupDialogVisible = true
    }, onDelete = {
        if (GroupIdGenerator.isDefaultGroupId(group.id)) {
            toast(R.string.group_default_delete_error_toast)
        } else {
            deleteGroupDialogVisible = true
        }
    })
    LWBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Folder,
                contentDescription = stringResource(R.string.groups)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .align(Alignment.CenterHorizontally),
                text = group.name,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            operations.forEach { item ->
                OperationItem(
                    imageVector = item.icon,
                    title = item.title,
                    onClick = {
                        item.onClick()
                    }
                )
            }
            Spacer(modifier = Modifier.height(36.dp))
        }
    }

    TextFieldDialog(
        visible = renameGroupDialogVisible,
        title = stringResource(R.string.rename),
        icon = Icons.Outlined.Edit,
        initialValue = group.name,
        onDismissRequest = {
            renameGroupDialogVisible = false
        },
        onConfirm = {
            onRename(it)
            renameGroupDialogVisible = false
            animateToDismiss()
        }
    )
    DeleteGroupDialog(
        visible = deleteGroupDialogVisible,
        groupName = group.name,
        onDelete = {
            onDelete(it)
            animateToDismiss()
        },
        onDismiss = {
            deleteGroupDialogVisible = false

        })
}

@Composable
fun rememberGroupOperations(
    onRename: () -> Unit,
    onDelete: () -> Unit,
): List<OperationModel> {
    val operations = remember {
        listOf(
            OperationModel(
                Icons.Rounded.Edit,
                getString(R.string.rename)
            ) {
                onRename()
            },
            OperationModel(
                Icons.Rounded.FolderDelete,
                getString(R.string.delete_group)
            ) {
                onDelete()
            },
        )
    }
    return operations
}