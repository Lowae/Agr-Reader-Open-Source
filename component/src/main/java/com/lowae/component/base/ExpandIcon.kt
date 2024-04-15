package com.lowae.component.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lowae.component.R
import com.lowae.component.constant.ElevationTokens

@Composable
fun ExpandableIcon(isExpanded: () -> Boolean, onExpanded: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .padding(horizontal = 7.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(ElevationTokens.Level3.dp))
            .then(
                if (onExpanded != null) Modifier.clickable { onExpanded() } else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isExpanded()) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
            contentDescription = stringResource(if (isExpanded()) R.string.expand_less else R.string.expand_more),
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}