package com.lowae.agrreader.ui.page.home.flow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.postDelayed
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.preference.ArticleItemStylePreference
import com.lowae.agrreader.data.model.preference.ArticleSwipeOperation
import com.lowae.agrreader.data.model.preference.LocalArticleItemStyle
import com.lowae.agrreader.ui.page.home.flow.item.ArticleItemContent
import com.lowae.agrreader.ui.theme.Shape12
import com.lowae.agrreader.utils.tap
import com.lowae.component.base.NoFlingDismissState
import com.lowae.component.base.NoFlingSwipeToDismiss
import com.lowae.component.base.rememberNoFlingDismissState
import com.lowae.component.constant.ElevationTokens

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArticleItem(
    articleWithFeed: ArticleWithFeed,
    leftSwipe: ArticleSwipeOperation,
    rightSwipe: ArticleSwipeOperation,
    onLeftSwipe: (ArticleSwipeOperation, ArticleWithFeed) -> Unit,
    onRightSwipe: (ArticleSwipeOperation, ArticleWithFeed) -> Unit,
    onClick: (ArticleWithFeed) -> Unit = {},
    onLongClick: (ArticleWithFeed) -> Unit = {},
    isSelected: (ArticleWithFeed) -> Boolean = { false },
) {
    val view = LocalView.current
    val itemStyle = LocalArticleItemStyle.current
    val (article, feed) = articleWithFeed
    val dismissState = rememberNoFlingDismissState(confirmValueChange = {
        if (it == DismissValue.DismissedToEnd) {
            view.postDelayed(200) { onRightSwipe(rightSwipe, articleWithFeed) }
        } else if (it == DismissValue.DismissedToStart) {
            view.postDelayed(200) { onLeftSwipe(leftSwipe, articleWithFeed) }
        }
        false
    }, positionalThreshold = {
        (it / 3.33).toFloat()
    })
    NoFlingSwipeToDismiss(
        state = dismissState,
        directions = buildSet {
            if (leftSwipe != ArticleSwipeOperation.NONE) add(DismissDirection.EndToStart)
            if (rightSwipe != ArticleSwipeOperation.NONE) add(DismissDirection.StartToEnd)
        },
        background = {
            SwipeToDeleteBackground(leftSwipe, rightSwipe, article, dismissState)
        },
        dismissContent = {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (isSelected(articleWithFeed)) ElevationTokens.Level4.dp else ElevationTokens.Level0_1.dp
                ),
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(Shape12)
                    .combinedClickable(
                        onLongClick = {
                            view.tap(false)
                            onLongClick(articleWithFeed)
                        },
                        onClick = { onClick(articleWithFeed) }
                    ),
            ) {
                when (itemStyle) {
                    is ArticleItemStylePreference.Card -> {
                        itemStyle.ArticleItemContent(article, feed)
                    }

                    is ArticleItemStylePreference.Default -> {
                        itemStyle.ArticleItemContent(article, feed)
                    }

                    is ArticleItemStylePreference.Text -> {
                        itemStyle.ArticleItemContent(article, feed)
                    }

                    is ArticleItemStylePreference.Title -> {
                        itemStyle.ArticleItemContent(article, feed)
                    }
                }
            }
        })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteBackground(
    leftSwipe: ArticleSwipeOperation,
    rightSwipe: ArticleSwipeOperation,
    article: Article,
    dismissState: NoFlingDismissState,
) {
    val view = LocalView.current
    val isStartHighlight by remember {
        derivedStateOf { dismissState.targetValue == DismissValue.DismissedToEnd }
    }
    val isEndHighlight by remember {
        derivedStateOf { dismissState.targetValue == DismissValue.DismissedToStart }
    }
    Row(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isStartHighlight || isEndHighlight) {
            view.tap(false)
        }
        when (rightSwipe) {
            ArticleSwipeOperation.NONE -> Unit
            ArticleSwipeOperation.READ -> {
                Icon(
                    if (article.isUnread) Icons.Rounded.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = stringResource(id = R.string.mark_as_read),
                    tint = if (isStartHighlight) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }

            ArticleSwipeOperation.STAR -> {
                Icon(
                    if (article.isStarred) Icons.Outlined.StarOutline else Icons.Rounded.Star,
                    contentDescription = stringResource(id = R.string.mark_as_starred),
                    tint = if (isStartHighlight) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        when (leftSwipe) {
            ArticleSwipeOperation.NONE -> Unit
            ArticleSwipeOperation.READ -> {
                Icon(
                    if (article.isUnread) Icons.Rounded.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = stringResource(id = R.string.mark_as_read),
                    tint = if (isEndHighlight) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }

            ArticleSwipeOperation.STAR -> {
                Icon(
                    if (article.isStarred) Icons.Outlined.StarOutline else Icons.Rounded.Star,
                    contentDescription = stringResource(id = R.string.mark_as_starred),
                    tint = if (isEndHighlight) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }
    }
}