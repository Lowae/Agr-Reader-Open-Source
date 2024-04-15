package com.lowae.agrreader.ui.component.base

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.utils.tap
import com.lowae.component.base.modifier.MultipleEventsCutter
import com.lowae.component.base.modifier.get

@Composable
fun FeedbackIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current,
    showBadge: Boolean = false,
    isHaptic: Boolean = true,
    isSound: Boolean = true,
    onClick: () -> Unit = {},
) {
    val view = LocalView.current
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    IconButton(
        onClick = {
            if (isHaptic) {
                view.tap(isSound)
            } else {
                if (isSound) view.playSoundEffect(SoundEffectConstants.CLICK)
            }
            multipleEventsCutter.processEvent { onClick() }
        },
    ) {
        if (showBadge) {
            BadgedBox(
                badge = {
                    Badge(modifier = Modifier.padding(end = 1.dp))
                }
            ) {
                Icon(
                    modifier = modifier,
                    imageVector = imageVector,
                    contentDescription = contentDescription,
                    tint = tint,
                )
            }
        } else {
            Icon(
                modifier = modifier,
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = tint,
            )
        }
    }
}

@Composable
fun FeedbackIconButton(
    modifier: Modifier = Modifier,
    imagePainter: Painter,
    contentDescription: String?,
    tint: Color = LocalContentColor.current,
    showBadge: Boolean = false,
    isHaptic: Boolean = true,
    isSound: Boolean = true,
    onClick: () -> Unit = {},
) {
    val view = LocalView.current

    IconButton(
        onClick = {
            if (isHaptic) {
                view.tap(isSound)
            } else {
                if (isSound) view.playSoundEffect(SoundEffectConstants.CLICK)
            }
            onClick()
        },
    ) {
        if (showBadge) {
            BadgedBox(
                badge = {
                    Badge(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape),
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                }
            ) {
                Icon(
                    modifier = modifier,
                    painter = imagePainter,
                    contentDescription = contentDescription,
                    tint = tint,
                )
            }
        } else {
            Icon(
                modifier = modifier,
                painter = imagePainter,
                contentDescription = contentDescription,
                tint = tint,
            )
        }
    }
}