package com.lowae.agrreader.ui.page.home.feeds.info

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.TextFieldDialog

@Composable
fun RenameFeedDialog(
    visible: Boolean,
    feedName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    TextFieldDialog(
        visible = visible,
        title = stringResource(R.string.rename_feed),
        icon = Icons.Outlined.DriveFileRenameOutline,
        initialValue = feedName,
        onConfirm = onConfirm,
        onDismissRequest = onDismiss
    )
}