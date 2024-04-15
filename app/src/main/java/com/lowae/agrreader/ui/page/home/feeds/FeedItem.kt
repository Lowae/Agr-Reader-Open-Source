package com.lowae.agrreader.ui.page.home.feeds

import RYExtensibleVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.ui.component.FeedIcon
import com.lowae.agrreader.ui.theme.Shape24
import com.lowae.agrreader.ui.theme.ShapeBottom24
import com.lowae.agrreader.ui.theme.ShapeTop24
import com.lowae.agrreader.utils.tap
import com.lowae.component.constant.ElevationTokens

@Composable
fun FeedItem(
    feed: Feed,
    count: Int,
    cornerType: FeedItemCornerType,
    isExpanded: () -> Boolean,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    FeedBaseItem(
        icon = {
            FeedIcon(feed.name, feed.icon)
        },
        title = feed.name,
        tipIcons = {
            if (feed.isNotification) {
                Icon(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(12.dp),
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            }
            if (feed.translationLanguage != null) {
                Icon(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(12.dp),
                    imageVector = Icons.Outlined.Translate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            }
        },
        count = count,
        cornerType = cornerType,
        isExpanded = isExpanded,
        onClick,
        onLongClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedBaseItem(
    icon: @Composable () -> Unit,
    title: String,
    tipIcons: @Composable RowScope.() -> Unit = {},
    count: Int,
    cornerType: FeedItemCornerType,
    isExpanded: () -> Boolean,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val view = LocalView.current

    RYExtensibleVisibility(visible = isExpanded()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(cornerType.toShape())
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(ElevationTokens.Level0_1.dp))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        view.tap()
                        onLongClick()
                    }
                )
                .padding(horizontal = 12.dp)
                .padding(
                    top = 12.dp,
                    bottom = if (cornerType == FeedItemCornerType.BOTTOM) 18.dp else 12.dp
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icon()
                    Text(
                        modifier = Modifier.padding(start = 12.dp, end = 6.dp),
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                tipIcons()
                if (count != 0) {
                    Badge(
                        modifier = Modifier.height(16.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            ElevationTokens.Level3.dp
                        ),
                        contentColor = MaterialTheme.colorScheme.outline,
                        content = {
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                    )
                }
            }
        }
    }
}

enum class FeedItemCornerType {

    TOP,
    BOTTOM,
    RECTANGLE,
    ALL;

    fun toShape() = when (this) {
        TOP -> ShapeTop24
        BOTTOM -> ShapeBottom24
        RECTANGLE -> RectangleShape
        ALL -> Shape24
    }

    companion object {

        fun getFeedItemCornerType(feeds: List<Feed>, index: Int) =
            if (feeds.size <= 1) {
                ALL
            } else if (index == 0) {
                TOP
            } else if (index == feeds.lastIndex) {
                BOTTOM
            } else {
                RECTANGLE
            }

    }

}