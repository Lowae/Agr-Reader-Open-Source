package com.lowae.agrreader.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.model.article.ArticleStatus
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ArticleDao {

    @RawQuery
    suspend fun queryArticleWithFeedsByRawQuery(query: SupportSQLiteQuery): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE accountId = :accountId 
        AND feedId IN (
            SELECT id FROM feed WHERE groupId = :groupId
        )
        AND isUnread = :isUnread
        AND (
            title LIKE '%' || :text || '%'
            OR shortDescription LIKE '%' || :text || '%'
            OR fullContent LIKE '%' || :text || '%'
        )
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun searchArticleByGroupIdWhenIsUnreadPaging(
        accountId: Int,
        text: String,
        groupId: String,
        isUnread: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE accountId = :accountId 
        AND feedId IN (
            SELECT id FROM feed WHERE groupId = :groupId
        )
        AND isStarred = :isStarred
        AND (
            title LIKE '%' || :text || '%'
            OR shortDescription LIKE '%' || :text || '%'
            OR fullContent LIKE '%' || :text || '%'
        )
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun searchArticleByGroupIdWhenIsStarredPaging(
        accountId: Int,
        text: String,
        groupId: String,
        isStarred: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE accountId = :accountId 
        AND feedId IN (
            SELECT id FROM feed WHERE groupId = :groupId
        )
        AND (
            title LIKE '%' || :text || '%'
            OR shortDescription LIKE '%' || :text || '%'
            OR fullContent LIKE '%' || :text || '%'
        )
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun searchArticleByGroupIdWhenAllPaging(
        accountId: Int,
        text: String,
        groupId: String,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE accountId = :accountId 
        AND feedId = :feedId
        AND isUnread = :isUnread
        AND (
            title LIKE '%' || :text || '%'
            OR shortDescription LIKE '%' || :text || '%'
            OR fullContent LIKE '%' || :text || '%'
        )
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun searchArticleByFeedIdWhenIsUnreadPaging(
        accountId: Int,
        text: String,
        feedId: String,
        isUnread: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE accountId = :accountId 
        AND feedId = :feedId
        AND isStarred = :isStarred
        AND (
            title LIKE '%' || :text || '%'
            OR shortDescription LIKE '%' || :text || '%'
            OR fullContent LIKE '%' || :text || '%'
        )
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun searchArticleByFeedIdWhenIsStarredPaging(
        accountId: Int,
        text: String,
        feedId: String,
        isStarred: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE accountId = :accountId 
        AND feedId = :feedId 
        AND (
            title LIKE '%' || :text || '%'
            OR shortDescription LIKE '%' || :text || '%'
            OR fullContent LIKE '%' || :text || '%'
        )
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun searchArticleByFeedIdWhenAllPaging(
        accountId: Int,
        text: String,
        feedId: String,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE accountId = :accountId 
        AND isUnread = :isUnread
        AND (
            title LIKE '%' || :text || '%'
            OR shortDescription LIKE '%' || :text || '%'
            OR fullContent LIKE '%' || :text || '%'
        )
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun searchArticleWhenIsUnreadPaging(
        accountId: Int,
        text: String,
        isUnread: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE accountId = :accountId 
        AND isStarred = :isStarred
        AND (
            title LIKE '%' || :text || '%'
            OR shortDescription LIKE '%' || :text || '%'
            OR fullContent LIKE '%' || :text || '%'
        )
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun searchArticleWhenIsStarredPaging(
        accountId: Int,
        text: String,
        isStarred: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE accountId = :accountId 
        AND (
            title LIKE '%' || :text || '%'
            OR shortDescription LIKE '%' || :text || '%'
            OR fullContent LIKE '%' || :text || '%'
        )
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun searchArticleWhenAllPaging(
        accountId: Int,
        text: String,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Query(
        """
        DELETE FROM article
        WHERE accountId = :accountId
        AND updateAt < :before
        AND isUnread = 0
        AND isStarred = 0
        """
    )
    suspend fun deleteAllArchivedBeforeThan(
        accountId: Int,
        before: Date,
    )

    @Query(
        """
        UPDATE article SET isUnread = :isUnread 
        WHERE accountId = :accountId
        AND date <= :before
        """
    )
    suspend fun markAllAsRead(
        accountId: Int,
        isUnread: Boolean,
        before: Date,
    )

    @Query(
        """
        SELECT id FROM article
        WHERE accountId = :accountId
        AND isUnread = :isUnread
        AND date <= :before
        """
    )
    suspend fun getMarkAllAsRead(
        accountId: Int,
        isUnread: Boolean,
        before: Date,
    ): List<String>

    @Query(
        """
        UPDATE article SET isUnread = :isUnread 
        WHERE accountId = :accountId
        AND date >= :before
        """
    )
    suspend fun markAllAsReadLatest(
        accountId: Int,
        isUnread: Boolean,
        before: Date,
    )

    @Query(
        """
        SELECT id FROM article
        WHERE accountId = :accountId
        AND isUnread = :isUnread
        AND date >= :before
        """
    )
    suspend fun getMarkAllAsReadLatest(
        accountId: Int,
        isUnread: Boolean,
        before: Date,
    ): List<String>

    @Query(
        """
        UPDATE article SET isUnread = :isUnread 
        WHERE feedId IN (
            SELECT id FROM feed 
            WHERE groupId = :groupId
        )
        AND accountId = :accountId
        AND date <= :before
        """
    )
    suspend fun markAllAsReadByGroupId(
        accountId: Int,
        groupId: String,
        isUnread: Boolean,
        before: Date,
    )

    @Query(
        """
        SELECT id FROM article
        WHERE feedId IN (
            SELECT id FROM feed 
            WHERE groupId = :groupId
        )
        AND accountId = :accountId
        AND isUnread = :isUnread
        AND date <= :before
        """
    )
    suspend fun getMarkAllAsReadByGroupId(
        accountId: Int,
        groupId: String,
        isUnread: Boolean,
        before: Date,
    ): List<String>

    @Query(
        """
        UPDATE article SET isUnread = :isUnread 
        WHERE feedId IN (
            SELECT id FROM feed 
            WHERE groupId = :groupId
        )
        AND accountId = :accountId
        AND date >= :before
        """
    )
    suspend fun markAllAsReadByGroupIdLatest(
        accountId: Int,
        groupId: String,
        isUnread: Boolean,
        before: Date,
    )

    @Query(
        """
        SELECT id FROM article
        WHERE feedId IN (
            SELECT id FROM feed 
            WHERE groupId = :groupId
        )
        AND accountId = :accountId
        AND isUnread = :isUnread
        AND date >= :before
        """
    )
    suspend fun getMarkAllAsReadByGroupIdLatest(
        accountId: Int,
        groupId: String,
        isUnread: Boolean,
        before: Date,
    ): List<String>

    @Query(
        """
        UPDATE article SET isUnread = :isUnread 
        WHERE feedId = :feedId
        AND accountId = :accountId
        AND date <= :before
        """
    )
    suspend fun markAllAsReadByFeedId(
        accountId: Int,
        feedId: String,
        isUnread: Boolean,
        before: Date,
    )

    @Query(
        """
        SELECT id FROM article
        WHERE feedId = :feedId
        AND accountId = :accountId
        AND isUnread = :isUnread
        AND date <= :before
        """
    )
    suspend fun getMarkAllAsReadByFeedId(
        accountId: Int,
        feedId: String,
        isUnread: Boolean,
        before: Date,
    ): List<String>

    @Query(
        """
        UPDATE article SET isUnread = :isUnread 
        WHERE feedId = :feedId
        AND accountId = :accountId
        AND date >= :before
        """
    )
    suspend fun markAllAsReadByFeedIdLatest(
        accountId: Int,
        feedId: String,
        isUnread: Boolean,
        before: Date,
    )

    @Query(
        """
        SELECT id FROM article
        WHERE feedId = :feedId
        AND accountId = :accountId
        AND isUnread = :isUnread
        AND date >= :before
        """
    )
    suspend fun getMarkAllAsReadByFeedIdLatest(
        accountId: Int,
        feedId: String,
        isUnread: Boolean,
        before: Date,
    ): List<String>

    @Query(
        """
        UPDATE article SET isUnread = :isUnread 
        WHERE id = :articleId
        AND accountId = :accountId
        """
    )
    suspend fun markAsReadByArticleId(
        accountId: Int,
        articleId: String,
        isUnread: Boolean,
    ): Int

    @Query(
        """
        UPDATE article SET isStarred = :isStarred 
        WHERE id = :articleId
        AND accountId = :accountId
        """
    )
    fun markAsStarredByArticleId(
        accountId: Int,
        articleId: String,
        isStarred: Boolean,
    ): Int

    @Query(
        """
        DELETE FROM article
        WHERE accountId = :accountId
        AND feedId = :feedId
        """
    )
    suspend fun deleteByFeedId(accountId: Int, feedId: String)

    @Query(
        """
        DELETE FROM article
        WHERE id IN (
            SELECT a.id FROM article AS a, feed AS b, `group` AS c
            WHERE a.accountId = :accountId
            AND a.feedId = b.id
            AND b.groupId = c.id
            AND c.id = :groupId
        )
        """
    )
    suspend fun deleteByGroupId(accountId: Int, groupId: String)

    @Query(
        """
        DELETE FROM article
        WHERE accountId = :accountId
        """
    )
    suspend fun deleteByAccountId(accountId: Int)

    @Transaction
    @Query(
        """
        SELECT * FROM article 
        WHERE accountId = :accountId
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun queryArticleWithFeedWhenIsAllPaging(
        accountId: Int,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article 
        WHERE accountId = :accountId
        ORDER BY date DESC
        """
    )
    fun queryArticleWithFeedWhenIsAllV2(accountId: Int): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE isStarred = :isStarred 
        AND accountId = :accountId
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun queryArticleWithFeedWhenIsStarredPaging(
        accountId: Int,
        isStarred: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE isStarred = :isStarred 
        AND accountId = :accountId
        ORDER BY date DESC
        """
    )
    fun queryArticleWithFeedWhenIsStarredV2(
        accountId: Int,
        isStarred: Boolean,
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article 
        WHERE isUnread = :isUnread 
        AND accountId = :accountId
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun queryArticleWithFeedWhenIsUnreadPaging(
        accountId: Int,
        isUnread: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article 
        WHERE isUnread = :isUnread 
        AND accountId = :accountId
        ORDER BY date DESC
        """
    )
    fun queryArticleWithFeedWhenIsUnreadV2(
        accountId: Int,
        isUnread: Boolean,
    ): List<ArticleWithFeed>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT a.id, a.date, a.title, a.author, a.rawDescription, 
        a.shortDescription, a.sourceHtml, a.fullContent, a.img, a.link, a.feedId, 
        a.accountId, a.isUnread, a.isStarred, a.isReadLater, a.updateAt, a.readingAt
        FROM article AS a
        LEFT JOIN feed AS b ON b.id = a.feedId
        LEFT JOIN `group` AS c ON c.id = b.groupId
        WHERE c.id = :groupId
        AND a.accountId = :accountId
        ORDER BY a.date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun queryArticleWithFeedByGroupIdWhenIsAllPaging(
        accountId: Int,
        groupId: String,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT a.id, a.date, a.title, a.author, a.rawDescription, 
        a.shortDescription, a.sourceHtml, a.fullContent, a.img, a.link, a.feedId, 
        a.accountId, a.isUnread, a.isStarred, a.isReadLater, a.updateAt, a.readingAt
        FROM article AS a
        LEFT JOIN feed AS b ON b.id = a.feedId
        LEFT JOIN `group` AS c ON c.id = b.groupId
        WHERE c.id = :groupId
        AND a.accountId = :accountId
        ORDER BY a.date DESC
        """
    )
    fun queryArticleWithFeedByGroupIdWhenIsAllV2(
        accountId: Int,
        groupId: String,
    ): List<ArticleWithFeed>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT a.id, a.date, a.title, a.author, a.rawDescription, 
        a.shortDescription, a.sourceHtml, a.fullContent, a.img, a.link, a.feedId, 
        a.accountId, a.isUnread, a.isStarred, a.isReadLater, a.updateAt, a.readingAt
        FROM article AS a
        LEFT JOIN feed AS b ON b.id = a.feedId
        LEFT JOIN `group` AS c ON c.id = b.groupId
        WHERE c.id = :groupId
        AND a.isStarred = :isStarred
        AND a.accountId = :accountId
        ORDER BY a.date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun queryArticleWithFeedByGroupIdWhenIsStarredPaging(
        accountId: Int,
        groupId: String,
        isStarred: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT a.id, a.date, a.title, a.author, a.rawDescription, 
        a.shortDescription, a.sourceHtml, a.fullContent, a.img, a.link, a.feedId, 
        a.accountId, a.isUnread, a.isStarred, a.isReadLater, a.updateAt, a.readingAt
        FROM article AS a
        LEFT JOIN feed AS b ON b.id = a.feedId
        LEFT JOIN `group` AS c ON c.id = b.groupId
        WHERE c.id = :groupId
        AND a.isStarred = :isStarred
        AND a.accountId = :accountId
        ORDER BY a.date DESC
        """
    )
    fun queryArticleWithFeedByGroupIdWhenIsStarredV2(
        accountId: Int,
        groupId: String,
        isStarred: Boolean,
    ): List<ArticleWithFeed>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT a.id, a.date, a.title, a.author, a.rawDescription, 
        a.shortDescription, a.sourceHtml, a.fullContent, a.img, a.link, a.feedId, 
        a.accountId, a.isUnread, a.isStarred, a.isReadLater, a.updateAt, a.readingAt
        FROM article AS a
        LEFT JOIN feed AS b ON b.id = a.feedId
        LEFT JOIN `group` AS c ON c.id = b.groupId
        WHERE c.id = :groupId
        AND a.isUnread = :isUnread
        AND a.accountId = :accountId
        ORDER BY a.date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun queryArticleWithFeedByGroupIdWhenIsUnreadPaging(
        accountId: Int,
        groupId: String,
        isUnread: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT a.id, a.date, a.title, a.author, a.rawDescription, 
        a.shortDescription, a.sourceHtml, a.fullContent, a.img, a.link, a.feedId, 
        a.accountId, a.isUnread, a.isStarred, a.isReadLater, a.updateAt, a.readingAt
        FROM article AS a
        LEFT JOIN feed AS b ON b.id = a.feedId
        LEFT JOIN `group` AS c ON c.id = b.groupId
        WHERE c.id = :groupId
        AND a.isUnread = :isUnread
        AND a.accountId = :accountId
        ORDER BY a.date DESC
        """
    )
    fun queryArticleWithFeedByGroupIdWhenIsUnreadV2(
        accountId: Int,
        groupId: String,
        isUnread: Boolean,
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE feedId = :feedId
        AND accountId = :accountId
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun queryArticleWithFeedByFeedIdWhenIsAllPaging(
        accountId: Int,
        feedId: String,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE feedId = :feedId
        AND accountId = :accountId
        ORDER BY date DESC
        """
    )
    fun queryArticleWithFeedByFeedIdWhenIsAllV2(
        accountId: Int,
        feedId: String,
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * from article 
        WHERE feedId = :feedId 
        AND isStarred = :isStarred
        AND accountId = :accountId
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun queryArticleWithFeedByFeedIdWhenIsStarredPaging(
        accountId: Int,
        feedId: String,
        isStarred: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * from article 
        WHERE feedId = :feedId 
        AND isStarred = :isStarred
        AND accountId = :accountId
        ORDER BY date DESC
        """
    )
    fun queryArticleWithFeedByFeedIdWhenIsStarredV2(
        accountId: Int,
        feedId: String,
        isStarred: Boolean,
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article 
        WHERE feedId = :feedId 
        AND isUnread = :isUnread
        AND accountId = :accountId
        ORDER BY date DESC
        LIMIT :limit
        OFFSET :offset
        """
    )
    fun queryArticleWithFeedByFeedIdWhenIsUnreadPaging(
        accountId: Int,
        feedId: String,
        isUnread: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed>

    @Transaction
    @Query(
        """
        SELECT * FROM article 
        WHERE feedId = :feedId 
        AND isUnread = :isUnread
        AND accountId = :accountId
        ORDER BY date DESC
        """
    )
    fun queryArticleWithFeedByFeedIdWhenIsUnreadV2(
        accountId: Int,
        feedId: String,
        isUnread: Boolean,
    ): List<ArticleWithFeed>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT a.id, a.date, a.title, a.author, a.rawDescription, 
        a.shortDescription, a.sourceHtml, a.fullContent, a.img, a.link, a.feedId, 
        a.accountId, a.isUnread, a.isStarred, a.isReadLater, a.updateAt, a.readingAt
        FROM article AS a LEFT JOIN feed AS b 
        ON a.feedId = b.id
        WHERE a.feedId = :feedId 
        AND a.accountId = :accountId
        ORDER BY date DESC LIMIT 1
        """
    )
    suspend fun queryLatestByFeedId(accountId: Int, feedId: String): Article?

    @Query(
        """
        SELECT * from article 
        WHERE link = :link
        AND feedId = :feedId
        AND accountId = :accountId
        """
    )
    suspend fun queryArticleByLink(
        link: String,
        feedId: String,
        accountId: Int,
    ): Article?

    @Query("SELECT isStarred, isUnread FROM article WHERE feedId = :feedId AND accountId = :accountId")
    fun fastQueryArticleUnreadOrStarredCursor(accountId: Int, feedId: String): Cursor

    @Transaction
    @Query(
        """
        SELECT * FROM article 
        WHERE isUnread = :isUnread 
        AND accountId = :accountId
        AND date >= :startTime
        AND date < :endTime
        ORDER BY date DESC
        """
    )
    fun queryTimeRangeArticlesFlowWhenIsUnread(
        accountId: Int,
        isUnread: Boolean,
        startTime: Long,
        endTime: Long,
    ): Flow<List<ArticleWithFeed>>

    @Transaction
    @Query(
        """
        SELECT COUNT(id) FROM article 
        WHERE isUnread = :isUnread 
        AND accountId = :accountId
        AND date >= :startTime
        AND date < :endTime
        """
    )
    fun countTimeRangeArticlesWhenIsUnread(
        accountId: Int,
        isUnread: Boolean,
        startTime: Long,
        endTime: Long
    ): Flow<Int>

    @Query(
        """
        UPDATE article SET readingAt = :readingAt 
        WHERE id = :articleId
        AND accountId = :accountId
        """
    )
    suspend fun updateReadingAtByArticleId(accountId: Int, articleId: String, readingAt: Long)

    @Query("SELECT id, isStarred, isUnread FROM article WHERE accountId = :accountId AND isUnread = :unread OR isStarred = :starred")
    suspend fun queryArticleIdWhereUnreadOrStarred(
        accountId: Int,
        unread: Boolean,
        starred: Boolean
    ): List<ArticleStatus>

    @Transaction
    @Query(
        """
        SELECT * FROM article
        WHERE id = :id
        """
    )
    suspend fun queryById(id: String): ArticleWithFeed?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(articles: List<Article>): List<Long>

    @Update(entity = Article::class)
    suspend fun updateParsedResult(updater: ArticleParsedUpdater)

    @Transaction
    suspend fun insertListIfNotExist(articles: List<Article>): List<Article> {
        return articles.mapNotNull {
            if (queryArticleByLink(
                    link = it.link,
                    feedId = it.feedId,
                    accountId = it.accountId
                ) == null
            ) it else null
        }.also {
            insertList(it)
        }
    }
}

@Entity
class ArticleParsedUpdater(
    val id: String,
    val img: String?,
    val fullContent: String?,
    val sourceHtml: String,
)