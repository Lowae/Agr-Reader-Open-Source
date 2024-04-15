package com.lowae.agrreader.ui.glance

import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.source.RYDatabase
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.getEndOfDay
import com.lowae.agrreader.utils.ext.getStartOfDay
import kotlinx.coroutines.flow.map

class AgrReaderWidgetRepository(database: RYDatabase) {

    companion object {
        private const val LATEST_ARTICLE_COUNT = 5
    }

    val unreadOfTodayArticlesFlow = database.articleDao().queryTimeRangeArticlesFlowWhenIsUnread(
        CurrentAccountId, true, getStartOfDay().time, getEndOfDay().time
    ).map { articles ->
        val latestArticleWithFeed = mutableListOf<ArticleWithFeed>()
        val articleWithFeedGroup = articles.groupByTo(HashMap()) { it.feed }
        repeat(LATEST_ARTICLE_COUNT) {
            articleWithFeedGroup.forEach {
                latestArticleWithFeed += it.value.removeFirstOrNull() ?: return@forEach
                if (latestArticleWithFeed.size >= LATEST_ARTICLE_COUNT) {
                    return@map latestArticleWithFeed
                }
            }
            if (latestArticleWithFeed.size >= LATEST_ARTICLE_COUNT) {
                return@map latestArticleWithFeed
            }
        }
        latestArticleWithFeed
    }

}