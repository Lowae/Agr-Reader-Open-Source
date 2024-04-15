package com.lowae.agrreader.ui.page.home.feeds.management

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.ui.component.base.AgrDialog
import com.lowae.agrreader.ui.component.base.RadioDialog
import com.lowae.agrreader.ui.component.base.RadioDialogOption

@Composable
fun FeedManagementMoveToGroupDialog(
    visible: Boolean,
    groups: List<Group>,
    onDismiss: () -> Unit,
    onConfirmMove: (Group) -> Unit,
) {

    var selectedMoveToGroup by remember { mutableStateOf<Group?>(null) }

    RadioDialog(
        visible = visible,
        title = stringResource(id = R.string.move_to_group),
        options = groups.map { group ->
            RadioDialogOption(text = group.name, onClick = {
                selectedMoveToGroup = group
            })
        },
        onDismissRequest = onDismiss
    )

    AgrDialog(
        visible = selectedMoveToGroup != null,
        onConfirm = {
            onConfirmMove(selectedMoveToGroup!!)
            selectedMoveToGroup = null
        },
        onDismiss = { selectedMoveToGroup = null },
        text = stringResource(
            R.string.move_selected_feeds_to_group,
            selectedMoveToGroup?.name.orEmpty()
        )
    )
}