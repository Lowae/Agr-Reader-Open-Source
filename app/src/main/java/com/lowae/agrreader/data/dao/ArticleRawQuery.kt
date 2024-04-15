package com.lowae.agrreader.data.dao

import androidx.sqlite.db.SimpleSQLiteQuery
import com.lowae.agrreader.data.model.article.ArticleWithFeed

object ArticleRawQuery {

    suspend fun ArticleDao.queryArticleWithFeedByGroupId(
        accountId: Int,
        groupId: String?,
        isStarred: Boolean,
        isUnread: Boolean,
        limit: Int,
        offset: Int,
        desc: Boolean = true,
    ): List<ArticleWithFeed> {
        val query = SimpleSQLiteQuery(
            """
        SELECT a.id, a.date, a.title, a.author, a.rawDescription, 
        a.shortDescription, a.sourceHtml, a.fullContent, a.img, a.link, a.feedId, 
        a.accountId, a.isUnread, a.isStarred, a.isReadLater, a.updateAt, a.readingAt
        FROM article AS a
        LEFT JOIN feed AS b ON b.id = a.feedId
        LEFT JOIN `group` AS c ON c.id = b.groupId
        WHERE c.id = ?
        AND a.accountId = ?
        ${if (isStarred) "AND a.isStarred = true" else if (isUnread) "AND a.isUnread = true" else ""}
        ORDER BY a.date ${if (desc) "DESC" else "ASC"}
        LIMIT $limit
        OFFSET $offset
        """,
            arrayOf(groupId, accountId)
        )
        return this.queryArticleWithFeedsByRawQuery(query)
    }

    suspend fun ArticleDao.queryArticleWithFeedByFeedId(
        accountId: Int,
        feedId: String,
        isStarred: Boolean,
        isUnread: Boolean,
        limit: Int,
        offset: Int,
        desc: Boolean = true,
    ): List<ArticleWithFeed> {
        val query = SimpleSQLiteQuery(
            """
        SELECT * from article 
        WHERE feedId = ? 
        AND accountId = ?
        ${if (isStarred) "AND isStarred = true" else if (isUnread) "AND isUnread = true" else ""}
        ORDER BY date ${if (desc) "DESC" else "ASC"}
        LIMIT $limit
        OFFSET $offset
        """, arrayOf(feedId, accountId)
        )
        return this.queryArticleWithFeedsByRawQuery(query)
    }

    suspend fun ArticleDao.queryArticleWithFeedByAccountId(
        accountId: Int,
        isStarred: Boolean,
        isUnread: Boolean,
        limit: Int,
        offset: Int,
        desc: Boolean = true,
    ): List<ArticleWithFeed> {
        val query = SimpleSQLiteQuery(
            """
        SELECT * FROM article
        WHERE accountId = ?
        ${if (isStarred) "AND isStarred = true" else if (isUnread) "AND isUnread = true" else ""}
        ORDER BY date ${if (desc) "DESC" else "ASC"}
        LIMIT $limit
        OFFSET $offset
        """, arrayOf(accountId)
        )
        return this.queryArticleWithFeedsByRawQuery(query)
    }

    suspend fun ArticleDao.queryTimeRangeArticles(
        accountId: Int,
        isStarred: Boolean,
        isUnread: Boolean,
        startTime: Long,
        endTime: Long,
        limit: Int,
        offset: Int,
        desc: Boolean = true,
    ): List<ArticleWithFeed> {
        val query = SimpleSQLiteQuery(
            """
        SELECT * FROM article 
        WHERE accountId = ? 
        ${if (isStarred) "AND isStarred = true" else if (isUnread) "AND isUnread = true" else ""}
        AND date >= ?
        AND date < ?
        ORDER BY date ${if (desc) "DESC" else "ASC"}
        LIMIT ?
        OFFSET ?
        """,
            arrayOf(accountId, startTime, endTime, limit, offset)
        )
        return this.queryArticleWithFeedsByRawQuery(query)
    }

}