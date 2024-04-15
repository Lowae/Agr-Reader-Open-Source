package com.lowae.agrreader.ui.component.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.R

@Composable
fun RadioDialog(
    modifier: Modifier = Modifier,
    visible: Boolean = false,
    title: String = "",
    options: List<RadioDialogOption> = emptyList(),
    onDismissRequest: () -> Unit = {},
) {
    MultipleItemsDialog(
        modifier,
        visible,
        title = title,
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        options = options
    ) { option ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .clickable {
                    option.onClick()
                    onDismissRequest()
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(selected = option.selected, onClick = {
                option.onClick()
                onDismissRequest()
            })
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = option.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    baselineShift = BaselineShift.None
                ).merge(other = option.style),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun <T> MultipleItemsDialog(
    modifier: Modifier = Modifier,
    visible: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
    title: String = "",
    subTitle: String = "",
    onDismissRequest: () -> Unit = {},
    confirmButton: @Composable () -> Unit = {},
    dismissButton: @Composable () -> Unit = {},
    options: List<T> = emptyList(),
    optionContent: @Composable (T) -> Unit
) {
    AgrDialog(
        modifier = modifier,
        visible = visible,
        onDismissRequest = onDismissRequest,
        icon = icon,
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Text(
                text = subTitle,
            )
            LazyColumn {
                items(options) { option ->
                    optionContent(option)
                }
            }
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton,
    )
}

@Immutable
data class RadioDialogOption(
    val text: String = "",
    val style: TextStyle? = null,
    val selected: Boolean = false,
    val onClick: () -> Unit = {},
)
