package com.lowae.agrreader.ui.page.home.feeds.info

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.AgrDialog
import com.lowae.agrreader.utils.ext.collectAsStateValue

@Composable
fun DeleteFeedDialog(
    feedName: String,
    feedOptionViewModel: FeedOptionViewModel = hiltViewModel(),
    onDeleteClick: () -> Unit,
) {
    val feedOptionUiState = feedOptionViewModel.feedOptionUiState.collectAsStateValue()

    AgrDialog(
        visible = feedOptionUiState.deleteDialogVisible,
        onDismissRequest = {
            feedOptionViewModel.hideDeleteDialog()
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = stringResource(R.string.unsubscribe),
            )
        },
        title = {
            Text(text = stringResource(R.string.unsubscribe))
        },
        text = {
            Text(text = stringResource(R.string.unsubscribe_tips, feedName))
        },
        confirmButton = {
            TextButton(onClick = onDeleteClick) {
                Text(
                    text = stringResource(R.string.unsubscribe),
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    feedOptionViewModel.hideDeleteDialog()
                }
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                )
            }
        },
    )
}