package com.lowae.agrreader.data.model.entities

import androidx.annotation.Keep
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.repository.RssHelper
import org.jsoup.Jsoup
import java.util.Date

object GReader {

    @Keep
    class FeedsResponse(
        val subscriptions: List<Feeds> = emptyList()
    )

    @Keep
    data class Feeds(
        val id: String = "",
        val title: String = "",
        val categories: List<Category> = emptyList(),
        val url: String = "",
        val htmlUrl: String = "",
        val iconUrl: String = ""
    ) {
        val category: Category
            get() = categories.first()
    }

    @Keep
    data class Category(
        val id: String = "",
        val label: String = ""
    )

    @Keep
    data class Articles(
        val id: String = "",
        val updated: Long = 0,
        val items: List<ArticleItem> = emptyList(),
        val continuation: String = ""
    )

    data class ArticleItem(
        val id: String = "",
        val crawlTimeMsec: String = "",
        val published: Long = 0,
        val title: String = "",

        /**
         * "canonical": [
         *                 {
         *                     "href": "http://www.geekpark.net/news/325199"
         *                 }
         *             ],
         */
        val canonical: JsonArray = JsonArray(),
        /**
         * "categories": [
         *                 "user/-/state/com.google/reading-list",
         *                 "user/-/label/ACG文化",
         *                 "user/-/label/海外企业新闻",
         *                 "user/-/label/ROBLOX"
         *             ]
         */
        val categories: JsonArray = JsonArray(),
        /**
         * "origin": {
         *                 "streamId": "feed/8",
         *                 "htmlUrl": "http://mainssl.geekpark.net/rss.rss",
         *                 "title": "极客公园"
         *             }
         */
        val origin: JsonObject = JsonObject(),

        /**
         * "summary": {
         *                 "content": "<p>「我说 123，油门踩到底！」</p>
         *            }
         */
        val summary: JsonObject = JsonObject(),
        val author: String = ""

    ) {
        val compactId: String
            get() = compactId(id)

        fun convertToArticle(accountId: Int, rssHelper: RssHelper): Article {
            val sourceHtml = summary["content"].asString
            val document = Jsoup.parse(sourceHtml)

            var isUnread = true
            var isStarred = false
            categories.forEach { c ->
                if (c.asString.endsWith("/state/com.google/read")) {
                    isUnread = false
                }
                if (c.asString.endsWith("/state/com.google/starred")) {
                    isStarred = true
                }
            }

            return Article(
                id = compactId,
                date = Date(published * 1000),
                title = title,
                author = author,
                rawDescription = sourceHtml,
                shortDescription = rssHelper.parseArticleShortDescription(document),
                fullContent = sourceHtml,
                img = rssHelper.parseArticleCover(document.body()),
                link = canonical.firstOrNull()?.asJsonObject?.get("href")?.asString.orEmpty(),
                feedId = origin.get("streamId").asString,
                accountId = accountId,
                isUnread = isUnread,
                isStarred = isStarred
            )
        }

    }

    private fun compactId(longId: String): String {
        val last = longId.split("/").last()
        return last.toLong(16).toString()
    }
}