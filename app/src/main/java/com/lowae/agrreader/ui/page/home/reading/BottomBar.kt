package com.lowae.agrreader.ui.page.home.reading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.CanBeDisabledIconButton
import com.lowae.agrreader.utils.NoOp
import com.lowae.agrreader.utils.NoOp1
import com.lowae.agrreader.utils.tap
import com.lowae.component.constant.ElevationTokens

@Composable
fun BottomBar(
    isStarred: Boolean,
    pageState: ReadingPageState,
    onStarred: (isStarred: Boolean) -> Unit = NoOp1,
    onFullContent: (isFullContent: Boolean) -> Unit = NoOp1,
    onTranslate: () -> Unit = NoOp
) {
    val view = LocalView.current
    BottomAppBar(
        Modifier
            .height(56.dp)
            .fillMaxWidth(),
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CanBeDisabledIconButton(
                modifier = Modifier.size(40.dp),
                disabled = false,
                imageVector = if (isStarred) {
                    Icons.Rounded.Bookmark
                } else {
                    Icons.Outlined.BookmarkBorder
                },
                contentDescription = stringResource(if (isStarred) R.string.mark_as_unstar else R.string.mark_as_starred),
                tint = if (isStarred) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            ) {
                view.tap(false)
                onStarred(!isStarred)
            }
            CanBeDisabledIconButton(
                disabled = false,
                modifier = Modifier
                    .size(40.dp)
                    .alpha(if (pageState.isTranslated.not()) 0.5f else 1f),
                imageVector = Icons.Rounded.Translate,
                contentDescription = "Translate",
                tint = MaterialTheme.colorScheme.onSurface,
            ) {
                if (pageState.isTranslated.not()) {
                    view.tap(false)
                    onTranslate()
                }
            }
            CanBeDisabledIconButton(
                disabled = false,
                modifier = Modifier.size(40.dp),
                imageVector = if (pageState.isWeb) {
                    Icons.Outlined.Article
                } else {
                    Icons.Rounded.Article
                },
                contentDescription = stringResource(R.string.parse_full_content),
                tint = if (pageState.isWeb) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            ) {
                view.tap(false)
                onFullContent(!pageState.isWeb)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TranslationBetaIconButton(
    modifier: Modifier = Modifier,
    disabled: Boolean,
    imageVector: ImageVector,
    size: Dp = 24.dp,
    contentDescription: String?,
    tint: Color = LocalContentColor.current,
    onClick: () -> Unit = {},
) {
    Box(
        Modifier
            .width(52.dp)
            .wrapContentHeight()
    ) {
        IconButton(
            modifier = modifier
                .size(40.dp)
                .alpha(
                    if (disabled) {
                        0.5f
                    } else {
                        1f
                    }
                ),
            enabled = !disabled,
            onClick = onClick,
        ) {
            Icon(
                modifier = Modifier.size(size),
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = if (disabled) MaterialTheme.colorScheme.outline else tint,
            )
        }

        Badge(
            Modifier
                .align(Alignment.TopEnd),
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                ElevationTokens.Level3.dp
            ),
        ) {
            Text(text = "BETA", fontSize = 10.sp)
        }
    }
}