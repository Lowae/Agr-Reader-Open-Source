package com.lowae.agrreader.ui.component.base

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RYSelectionChip(
    content: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = CircleShape,
    border: BorderStroke? = null,
    selectedIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    ElevatedFilterChip(
        selected = selected,
        modifier = modifier.defaultMinSize(minHeight = 32.dp),
        interactionSource = interactionSource,
        enabled = enabled,
        trailingIcon = if (selected) selectedIcon ?: {
            Icon(
                modifier = Modifier
                    .size(20.dp),
                imageVector = Icons.Rounded.Check,
                contentDescription = stringResource(R.string.selected),
            )
        } else null,
        shape = shape,
        onClick = {
            focusManager.clearFocus()
            onClick()
        },
        label = {
            Text(
                modifier = Modifier,
                text = content,
                style = MaterialTheme.typography.titleSmall,
            )
        },
    )
}