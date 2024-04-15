package com.lowae.agrreader.ui.page.home.feeds.management

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.TextFieldDialog

@Composable
fun CreateNewGroupDialog(visible: Boolean, onDismiss: () -> Unit, onCreateNew: (String) -> Unit) {
    TextFieldDialog(
        visible = visible,
        title = stringResource(R.string.create_new_group),
        icon = Icons.Outlined.CreateNewFolder,
        initialValue = "",
        placeholder = stringResource(R.string.name),
        onDismissRequest = onDismiss,
        onConfirm = { onCreateNew(it) }
    )
}