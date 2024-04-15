package com.lowae.agrreader.ui.page.home.flow.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.preference.ArticleItemStylePreference
import com.lowae.agrreader.ui.theme.Shape12
import com.lowae.component.constant.ElevationTokens

@Composable
@Preview
fun ArticleItemContentPreview(itemStyle: ArticleItemStylePreference = ArticleItemStylePreference.Title) {
    val articleWithFeed = ArticleWithFeed(
        Article.Mock.copy(
            title = "夏天的飞鸟，来到我的窗前，歌唱，又飞走了。秋天的黄叶，它们没有什么曲子可唱，一声叹息，飘落在地上。",
            shortDescription = "Stray birds of summer come to my window to singand fly away.And yellow leaves of autumn,which have no songs,flutter and fall there with a sign.",
            img = " ",
            author = "泰戈尔《飞鸟集》",
        ).apply { dateString = "00:00" },
        Feed.Mock.copy(
            name = "泰戈尔《飞鸟集》"
        )
    )
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = ElevationTokens.Level0_1.dp),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(Shape12)
            .clickable { }
    ) {
        when (itemStyle) {
            is ArticleItemStylePreference.Default -> itemStyle.ArticleItemContent(
                articleWithFeed.article, articleWithFeed.feed
            )

            is ArticleItemStylePreference.Card -> itemStyle.ArticleItemContent(
                articleWithFeed.article, articleWithFeed.feed
            )

            is ArticleItemStylePreference.Text -> itemStyle.ArticleItemContent(
                articleWithFeed.article, articleWithFeed.feed
            )

            is ArticleItemStylePreference.Title -> itemStyle.ArticleItemContent(
                articleWithFeed.article, articleWithFeed.feed
            )
        }
    }
}