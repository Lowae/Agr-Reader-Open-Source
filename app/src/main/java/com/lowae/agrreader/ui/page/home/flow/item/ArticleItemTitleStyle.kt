package com.lowae.agrreader.ui.page.home.flow.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.preference.ArticleItemStylePreference
import com.lowae.agrreader.ui.component.FeedIcon
import com.lowae.agrreader.ui.theme.Shape12
import com.lowae.agrreader.utils.ifNullOrBlank

@Composable
fun ArticleItemStylePreference.Title.ArticleItemContent(article: Article, feed: Feed) {
    val isHighlight = article.isStarred || article.isUnread
    Column(
        modifier = Modifier
            .alpha(if (isHighlight) 1f else 0.5f)
            .padding(12.dp)
    ) {
        // top
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Article
            Column(
                modifier = Modifier.weight(1f),
            ) {
                // Title
                Text(
                    text = article.translationTitle ?: article.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // icon
            FeedIcon(feed.name, feed.icon)
            // author
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp),
                text = article.author.ifNullOrBlank { feed.name },
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            // Right
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Starred
                if (article.isStarred) {
                    Surface(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = Shape12,
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .size(16.dp)
                                .alpha(0.7f),
                            imageVector = Icons.Rounded.Bookmark,
                            contentDescription = stringResource(R.string.starred),
                            tint = MaterialTheme.colorScheme.outline,
                        )
                    }
                }

                // Date
                Text(
                    modifier = Modifier.alpha(0.7f),
                    text = article.dateString ?: "",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}