package com.lowae.agrreader.data.model.entities

import androidx.core.text.HtmlCompat
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.repository.RssHelper
import com.lowae.agrreader.utils.ext.getString
import org.jsoup.Jsoup
import java.util.Date

object FeverDTO {

    data class Common(
        val api_version: Int?,
        val auth: Int?,
        val last_refreshed_on_time: Long?,
    )

    /**
     * @link fever.php/?api=&feeds=
     * @sample
     *  {
     *      "api_version": 3,
     *      "auth": 1,
     *      "last_refreshed_on_time": 1647530101,
     *      "feeds": [
     *          {
     *          "id": 2,
     *          "favicon_id": 2,
     *          "title": "Ash's Knowledge Base",
     *          "url": "https://www.ashinch.com/feed",
     *          "site_url": "http://ashinch.com/",
     *          "is_spark": 0,
     *          "last_updated_on_time": 1647530101
     *          }
     *      ],
     *      "feeds_groups": [
     *          {
     *              "group_id": 2,
     *              "feed_ids": "2,3,4"
     *          }
     *      ]
     *  }
     */
    data class Feeds(
        val api_version: Int?,
        val auth: Int?,
        val last_refreshed_on_time: Long?,
        val feeds: List<FeedItem>?,
        val feeds_groups: List<FeedsGroupsItem>?,
    )

    data class FeedItem(
        val id: Int?,
        val favicon_id: Int?,
        val title: String?,
        val url: String?,
        val site_url: String?,
        val is_spark: Int?,
        val last_refreshed_on_time: Long?,
    ) {
        fun convertToFeed(accountId: Int, groupId: String): Feed? {
            if (id == null || url == null) return null
            return Feed(
                id = id.toString(),
                name = title ?: getString(R.string.defaults),
                url = url,
                groupId = groupId,
                accountId = accountId,
            )
        }
    }

    /**
     * @link fever.php/?api=&groups=
     * @sample
     *  {
     *      "api_version": 3,
     *      "auth": 1,
     *      "last_refreshed_on_time": 1647534602,
     *      "groups": [
     *          {
     *              "id": 1,
     *              "title": "未分类"
     *          }
     *      ],
     *      "feeds_groups": [
     *          {
     *          "group_id": 2,
     *          "feed_ids": "2,3,4"
     *          },
     *      ]
     *  }
     */
    data class Groups(
        val api_version: Int?,
        val auth: Int?,
        val last_refreshed_on_time: Long?,
        val groups: List<GroupItem>?,
        val feeds_groups: List<FeedsGroupsItem>?,
    )

    data class GroupItem(
        val id: Int?,
        val title: String?,
    )

    data class FeedsGroupsItem(
        val group_id: Int?,
        val feed_ids: String?,
    )

    /**
     * @link fever.php/?api=&favicons=
     * @sample
     *  {
     *  }
     */
    data class Favicons(
        val api_version: Int?,
        val auth: Int?,
        val last_refreshed_on_time: Long?,
        val favicons: List<Favicon> = emptyList(),
    )

    data class Favicon(
        val id: Int? = null,
        val data: String = "",
    )

    /**
     * @link fever.php/?api=&items=&with_ids={ids}
     * @link fever.php/?api=&items=&since_id={since}
     * @sample
     *  {
     *      "api_version": 3,
     *      "auth": 1,
     *      "last_refreshed_on_time": 1647534602,
     *      "total_items": 853,
     *      "items": [
     *          {
     *              "id": "1647445533955157",
     *              "feed_id": 37,
     *              "title": "智能音箱自己把自己黑了：随机购物拨号，自主开灯关门，平均成功率达88%",
     *              "author": "博雯",
     *              "html": "<blockquote>\n<p data-track=\"48\">博雯 发自 凹非寺</p>\n<p d...",
     *              "url": "https://www.qbitai.com/2022/03/33402.html",
     *              "is_saved": 0,
     *              "is_read": 0,
     *              "created_on_time": 1647442680
     *          }
     *      ]
     *  {
     */
    data class Items(
        val api_version: Int?,
        val auth: Int?,
        val last_refreshed_on_time: Long?,
        val total_items: Int?,
        val items: List<Item>?,
    )

    data class Item(
        val id: String?,
        val feed_id: Int?,
        val title: String?,
        val author: String?,
        val html: String?,
        val url: String?,
        val is_saved: Int?,
        val is_read: Int?,
        val created_on_time: Long?,
    ) {
        fun convertToArticle(accountId: Int, rssHelper: RssHelper): Article? {
            if (id == null) return null
            val document = Jsoup.parse(html.orEmpty())
            val shortDescription =
                if (html.isNullOrBlank()) "" else rssHelper.parseArticleShortDescription(document)

            return Article(
                id = id,
                date = created_on_time?.run { Date(this * 1000) } ?: Date(),
                title = HtmlCompat.fromHtml(
                    title ?: getString(R.string.empty),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                ).toString(),
                author = author,
                rawDescription = html ?: "",
                shortDescription = shortDescription,
                fullContent = html,
                img = rssHelper.parseArticleCover(html ?: ""),
                link = url ?: "",
                feedId = feed_id.toString(),
                accountId = accountId,
                isUnread = (is_read ?: 0) <= 0,
                isStarred = (is_saved ?: 0) > 0,
                updateAt = Date(),
            )
        }
    }

    /**
     * @link fever.php/?api=&links=
     * @sample
     *  {
     *  }
     */
    data class Links(
        val api_version: Int?,
        val auth: Int?,
        val last_refreshed_on_time: Long?,
        val links: List<Link>?,
    )

    data class Link(
        val id: String?,
        val feed_id: String?,
        val item_id: String?,
        val temperature: Float?,
        val is_item: Boolean?,
        val is_local: Boolean?,
        val is_saved: Boolean?,
        val title: String?,
        val url: String?,
        val item_ids: List<String>?,
    )

    /**
     * @link fever.php/?api=&unread_item_ids=
     * @sample
     *  {
     *      "api_version": 3,
     *      "auth": 1,
     *      "last_refreshed_on_time": 1647530135,
     *      "unread_item_ids": "1646660589277217,1646660589277218"
     *  }
     */
    data class ItemsByUnread(
        val api_version: Int?,
        val auth: Int?,
        val last_refreshed_on_time: Long?,
        val unread_item_ids: String?,
    )

    /**
     * @link fever.php/?api=&saved_item_ids=
     * @sample
     *  {
     *      "api_version": 3,
     *      "auth": 1,
     *      "last_refreshed_on_time": 1647534602,
     *      "saved_item_ids": "1647441026698935,1646660589277218"
     *  }
     */
    data class ItemsByStarred(
        val api_version: Int?,
        val auth: Int?,
        val last_refreshed_on_time: Long?,
        val saved_item_ids: String?,
    )

    enum class StatusEnum(val value: String) {
        Read("read"),
        Unread("unread"),
        Saved("saved"),
        Unsaved("unsaved"),
    }
}
