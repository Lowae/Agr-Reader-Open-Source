package com.lowae.agrreader.ui.page.home.flow

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.general.MarkAsReadConditions
import com.lowae.agrreader.ui.component.base.AnimatedPopup
import com.lowae.agrreader.ui.theme.palette.alwaysLight
import com.lowae.agrreader.utils.tap

@Composable
fun MarkAsReadBar(
    visible: Boolean = false,
    absoluteY: Dp = Dp.Hairline,
    onDismissRequest: () -> Unit = {},
    onItemClick: (MarkAsReadConditions) -> Unit = {},
) {
    val animated = remember { Animatable(absoluteY.value) }

    LaunchedEffect(absoluteY) {
        snapshotFlow { absoluteY }.collect {
            animated.animateTo(it.value, spring(stiffness = Spring.StiffnessMediumLow))
        }
    }

    AnimatedPopup(
        visible = visible,
        absoluteY = animated.value.dp,
        onDismissRequest = onDismissRequest,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MarkAsReadBarItem(
                modifier = Modifier.width(56.dp),
                text = stringResource(R.string.seven_days),
            ) {
                onItemClick(MarkAsReadConditions.SevenDays)
            }
            MarkAsReadBarItem(
                modifier = Modifier.width(56.dp),
                text = stringResource(R.string.three_days),
            ) {
                onItemClick(MarkAsReadConditions.ThreeDays)
            }
            MarkAsReadBarItem(
                modifier = Modifier.width(56.dp),
                text = stringResource(R.string.one_day),
            ) {
                onItemClick(MarkAsReadConditions.OneDay)
            }
            MarkAsReadBarItem(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.mark_all_as_read),
                isPrimary = true,
            ) {
                onItemClick(MarkAsReadConditions.All)
            }
        }
    }
}

@Composable
fun MarkAsReadBarItem(
    modifier: Modifier = Modifier,
    text: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit = {},
) {
    val view = LocalView.current

    Surface(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                view.tap()
                onClick()
            },
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        color = if (isPrimary) {
            MaterialTheme.colorScheme.primaryContainer alwaysLight true
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 5.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall.copy(
                    textAlign = TextAlign.Center,
                ),
                color = if (isPrimary) {
                    MaterialTheme.colorScheme.onSurface alwaysLight true
                } else {
                    MaterialTheme.colorScheme.secondary
                },
            )
        }
    }
    if (!isPrimary) {
        Spacer(modifier = Modifier.width(8.dp))
    }
}
