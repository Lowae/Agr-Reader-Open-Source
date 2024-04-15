package com.lowae.agrreader.ui.page.home.feeds.drawer.group

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderDelete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.AgrDialog

@Composable
fun DeleteGroupDialog(
    visible: Boolean,
    groupName: String,
    onDelete: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AgrDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.FolderDelete,
                contentDescription = stringResource(R.string.delete_group),
            )
        },
        title = {
            Text(text = stringResource(R.string.delete_group))
        },
        text = {
            Text(text = stringResource(R.string.dialog_delete_group_text, groupName))
        },
        content = {
            Column(horizontalAlignment = Alignment.End) {
                TextButton(
                    onClick = { onDelete(false) },
                    contentPadding = ButtonDefaults.TextButtonContentPadding
                ) {
                    Text(text = stringResource(R.string.dialog_delete_group_default_option))
                }
                TextButton(onClick = { onDelete(true) }) {
                    Text(
                        text = stringResource(R.string.dialog_delete_group_warning_option),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                    )
                }
            }
        }
    )
}