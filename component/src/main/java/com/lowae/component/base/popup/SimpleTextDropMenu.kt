package com.lowae.component.base.popup

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun <T> SimpleTextDropMenu(
    data: List<T>,
    onText: (T) -> String,
    visible: Boolean,
    onDismiss: () -> Unit,
    onClick: (T) -> Unit
) {
    DropdownMenu(expanded = visible, onDismissRequest = onDismiss) {
        data.forEach {
            DropdownMenuItem(text = { Text(text = onText(it)) }, onClick = { onClick(it) })
        }
    }
}

@Composable
fun SimpleDropMenu(
    items: List<DropMenuItem>,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = visible,
        onDismissRequest = onDismiss,
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    item.onClick()
                    onDismiss()
                },
                leadingIcon = {
                    Icon(
                        item.icon,
                        contentDescription = item.text,
                    )
                },
                text = {
                    Text(item.text)
                },
            )
        }
    }
}

data class DropMenuItem(
    val icon: ImageVector,
    val text: String,
    val onClick: () -> Unit,
)