package com.lowae.agrreader.ui.page.home.flow

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlaylistAddCheck
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.AgrDialog
import com.lowae.agrreader.ui.component.base.FeedbackIconButton

@Composable
fun MarkAsReadDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    message: String,
) {
    AgrDialog(
        visible = visible,
        modifier = Modifier.padding(horizontal = 24.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        icon = {
            FeedbackIconButton(
                imageVector = Icons.Rounded.PlaylistAddCheck,
                contentDescription = stringResource(R.string.mark_all_as_read),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            Text(
                text = stringResource(R.string.mark_as_read),
                overflow = TextOverflow.Clip
            )
        },
        text = {
            Text(
                text = message,
                textAlign = TextAlign.Center
            )
        },
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )

}