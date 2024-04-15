package com.lowae.agrreader.ui.page.settings.color

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.ui.component.base.DynamicSVGImage
import com.lowae.agrreader.ui.svg.illustrations.AgrPro
import com.lowae.agrreader.ui.svg.illustrations.Illustrations

@Composable
fun AppThemePreviewItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    colorScheme: ColorScheme,
    shapes: Shapes = MaterialTheme.shapes,
    isCustom: Boolean = false,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1.7f)
            .border(
                width = 4.dp,
                color = if (selected) {
                    colorScheme.primary
                } else {
                    colorScheme.onSurface.copy(alpha = 0.75f)
                },
                shape = RoundedCornerShape(15.dp),
            )
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.background)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = colorScheme.primary
                )
            }

            if (isCustom) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    tint = colorScheme.primary
                )
                DynamicSVGImage(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(20.dp),
                    svgImageString = Illustrations.AgrPro
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .background(
                    color = colorScheme.surfaceVariant,
                    shape = shapes.small,
                )
                .fillMaxWidth(1f),
        ) {
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .height(32.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .weight(1f)
                        .background(
                            color = colorScheme.tertiary,
                            shape = RoundedCornerShape(5.dp)
                        ),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .weight(1f)
                        .background(
                            color = colorScheme.secondary,
                            shape = RoundedCornerShape(5.dp)
                        ),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .background(
                        color = colorScheme.primary,
                        shape = shapes.small,
                    )
            )
        }

        // Bottom bar
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                tonalElevation = 3.dp,
            ) {
                Row(
                    modifier = Modifier
                        .height(32.dp)
                        .fillMaxWidth()
                        .background(colorScheme.surface)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .alpha(0.6f)
                            .height(17.dp)
                            .weight(1f)
                            .background(
                                color = colorScheme.surfaceTint,
                                shape = shapes.small,
                            ),
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(17.dp)
                            .background(
                                color = colorScheme.primaryContainer,
                                shape = CircleShape,
                            ),
                    )
                }
            }
        }
    }
}
