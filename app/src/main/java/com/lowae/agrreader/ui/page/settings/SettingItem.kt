package com.lowae.agrreader.ui.page.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.theme.palette.LocalTonalPalettes
import com.lowae.agrreader.ui.theme.palette.onDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingItem(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp, 16.dp, 16.dp, 16.dp),
    enable: Boolean = true,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    desc: String? = null,
    tooltip: String? = null,
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    separatedActions: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val tonalPalettes = LocalTonalPalettes.current

    Surface(
        modifier = Modifier
            .combinedClickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke() },
                onLongClick = onLongClick
            )
            .alpha(if (enable) 1f else 0.5f),
        color = Color.Unspecified
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    modifier = Modifier.padding(end = 12.dp),
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                iconPainter?.let {
                    Icon(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp),
                        painter = it,
                        contentDescription = title,
                        tint = iconTint,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        maxLines = if (desc == null) 2 else 1,
                        style = titleStyle
                    )
                    tooltip?.also {
                        RichTooltipWithManualInvocationSample(it)
                    }
                }
                desc?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            action?.also {
                if (separatedActions) {
                    Divider(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(1.dp, 32.dp),
                        color = tonalPalettes neutralVariant 80 onDark (tonalPalettes neutralVariant 30),
                    )
                }
                Box(Modifier.padding(start = 16.dp)) {
                    it()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RichTooltipWithManualInvocationSample(tooltip: String) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip { Text(modifier = Modifier.padding(6.dp), text = tooltip) }
        },
        state = tooltipState
    ) {
        IconButton(
            modifier = Modifier.size(18.dp),
            onClick = {
                scope.launch {
                    tooltipState.show(MutatePriority.UserInput)
                }
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.tips_and_support),
                tint = MaterialTheme.colorScheme.outline,
            )
        }

    }
}
