package com.lowae.agrreader.data.repository

import android.content.Context
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.glance.appwidget.updateAll
import androidx.work.WorkManager
import com.lowae.agrreader.data.action.ArticleActions
import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.action.ArticleMarkStarAction
import com.lowae.agrreader.data.dao.AccountDao
import com.lowae.agrreader.data.dao.ArticleDao
import com.lowae.agrreader.data.dao.ArticleHistoryDao
import com.lowae.agrreader.data.dao.ArticleRawQuery.queryArticleWithFeedByAccountId
import com.lowae.agrreader.data.dao.ArticleRawQuery.queryArticleWithFeedByFeedId
import com.lowae.agrreader.data.dao.ArticleRawQuery.queryArticleWithFeedByGroupId
import com.lowae.agrreader.data.dao.ArticleRawQuery.queryTimeRangeArticles
import com.lowae.agrreader.data.dao.FeedDao
import com.lowae.agrreader.data.dao.GroupDao
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.model.article.ArticleHistory
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.feed.StatusCount
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.model.group.GroupWithFeed
import com.lowae.agrreader.data.model.preference.KeepArchivedPreference
import com.lowae.agrreader.ui.glance.AgrReaderAppWidget
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.GroupIdGenerator
import com.lowae.agrreader.utils.ext.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.Date

abstract class AbstractRssRepository(
    private val context: Context,
    scope: CoroutineScope,
    private val accountDao: AccountDao,
    private val articleDao: ArticleDao,
    private val articleHistoryDao: ArticleHistoryDao,
    private val groupDao: GroupDao,
    private val feedDao: FeedDao,
    private val workManager: WorkManager,
    protected val rssHelper: RssHelper,
    private val notificationHelper: NotificationHelper,
    private val dispatcherIO: CoroutineDispatcher,
    private val dispatcherDefault: CoroutineDispatcher,
) {

    private var syncingJob: Job? = null
    private val syncProgressStateFlow = MutableStateFlow<SyncProgress>(SyncProgress.End)
    val syncProgress = syncProgressStateFlow.asStateFlow()
    protected val articleUpdateAction = MutableSharedFlow<ArticleActions>()
    val articleUpdateFlow = articleUpdateAction.asSharedFlow()

    init {
        scope.launch {
            syncProgress.collectLatest {
                if (it is SyncProgress.End) {
                    AgrReaderAppWidget().updateAll(context)
                }
            }
        }
    }

    open val tag: String = "AbstractRssRepository"
    open val groupOperation = true
    open val feedOperation = true
    open val articleOperation = true

    open suspend fun sync(feedId: String? = null, groupId: String? = null) {
        clearKeepArchivedArticles()
    }

    open suspend fun validCredentials(): Boolean = true

    open suspend fun reAuthenticate() = Unit

    open suspend fun subscribe(feed: Feed, articles: List<Article>) {
        feedDao.insert(feed)
        articleDao.insertList(articles.map {
            it.copy(feedId = feed.id)
        })
    }

    open suspend fun subscribe(feedUrl: String) = Unit

    open suspend fun addGroup(name: String): String {
        val accountId = CurrentAccountId
        val groupId = GroupIdGenerator.id()
        groupDao.insert(Group(id = groupId, name = name, accountId = accountId))
        return groupId
    }

    open suspend fun addGroup(group: Group) {
        groupDao.insert(group)
    }

    open suspend fun addGroup(groups: List<Group>) {
        groupDao.insertOrUpdate(groups)
    }

    open suspend fun updateGroup(vararg group: Group) {
        groupDao.update(*group)
    }

    open suspend fun addFeed(feed: Feed) {
        feedDao.insert(feed)
    }

    open suspend fun addFeed(feeds: List<Feed>) {
        feedDao.insertOrUpdate(feeds)
    }

    open suspend fun deleteFeed(feed: Feed) {
        deleteArticles(feed = feed)
        feedDao.delete(feed)
    }

    open suspend fun updateFeed(feed: Feed) {
        feedDao.update(feed)
    }

    open suspend fun markAsRead(action: ArticleMarkReadAction) {
        val accountId = CurrentAccountId
        val (groupId, feedId, articleId, before, isUnread, latest) = action
        when {
            groupId != null -> {
                if (latest && before != null) {
                    articleDao.markAllAsReadByGroupIdLatest(
                        accountId = accountId,
                        groupId = groupId,
                        isUnread = isUnread,
                        before = before
                    )
                } else {
                    articleDao.markAllAsReadByGroupId(
                        accountId = accountId,
                        groupId = groupId,
                        isUnread = isUnread,
                        before = before ?: Date()
                    )
                }
            }

            feedId != null -> {
                if (latest && before != null) {
                    articleDao.markAllAsReadByFeedIdLatest(
                        accountId = accountId,
                        feedId = feedId,
                        isUnread = isUnread,
                        before = before
                    )
                } else {
                    articleDao.markAllAsReadByFeedId(
                        accountId = accountId,
                        feedId = feedId,
                        isUnread = isUnread,
                        before = before ?: Date()
                    )
                }
            }

            articleId != null -> {
                articleDao.markAsReadByArticleId(accountId, articleId, isUnread)
            }

            else -> {
                if (latest && before != null) {
                    articleDao.markAllAsReadLatest(
                        accountId,
                        isUnread,
                        before
                    )
                } else {
                    articleDao.markAllAsRead(accountId, isUnread, before ?: Date())
                }
            }
        }
        RLog.i(tag, "action: $action, isUnread: $isUnread")
        articleUpdateAction.emit(action)
    }

    open suspend fun markAsStarred(action: ArticleMarkStarAction) {
        val accountId = CurrentAccountId
        articleDao.markAsStarredByArticleId(accountId, action.articleId, action.isStarred)
        articleUpdateAction.emit(action)
    }

    open suspend fun addOpml(inputStream: InputStream) = Unit

    open fun cancelSync() = Unit

    protected fun notifySyncProgress(progress: SyncProgress) {
        syncProgressStateFlow.tryEmit(progress)
    }

    suspend fun clearKeepArchivedArticles() {
        val accountId = CurrentAccountId
        val keepArchivedPreference = DataStore.get(DataStoreKeys.KeepArchived)
        articleDao.deleteAllArchivedBeforeThan(
            accountId,
            Date(
                System.currentTimeMillis() - (keepArchivedPreference
                    ?: KeepArchivedPreference.default.value)
            )
        )
    }

    fun pullGroups(): Flow<MutableList<Group>> =
        groupDao.queryAllGroup(CurrentAccountId).flowOn(dispatcherIO)

    fun pullFeeds(): Flow<MutableList<GroupWithFeed>> =
        groupDao.queryAllGroupWithFeedAsFlow(CurrentAccountId).flowOn(dispatcherIO)
            .onEach { it.sortBy { it.group.priority } }

    suspend fun pullArticles2(
        groupId: String?,
        feedId: String?,
        isStarred: Boolean,
        isUnread: Boolean,
        limit: Int,
        offset: Int,
        desc: Boolean = true,
    ): List<ArticleWithFeed> {
        val accountId = CurrentAccountId
        Log.i(
            "RLog",
            "pullArticles2: accountId: ${accountId}, groupId: ${groupId}, feedId: ${feedId}, isStarred: ${isStarred}, isUnread: ${isUnread}"
        )
        return when {
            groupId != null -> articleDao.queryArticleWithFeedByGroupId(
                accountId, groupId, isStarred, isUnread, limit, offset, desc
            )

            feedId != null -> articleDao.queryArticleWithFeedByFeedId(
                accountId, feedId, isStarred, isUnread, limit, offset, desc
            )

            else -> articleDao.queryArticleWithFeedByAccountId(
                accountId, isStarred, isUnread, limit, offset, desc
            )
        }
    }

    suspend fun findFeedById(id: String): Feed? = feedDao.queryById(id)

    suspend fun findGroupById(id: String): Group? = groupDao.queryById(id)

    suspend fun findArticleById(id: String): ArticleWithFeed? = articleDao.queryById(id)

    suspend fun isFeedExist(url: String): Boolean =
        feedDao.queryByLink(CurrentAccountId, url).isNotEmpty()

    suspend fun deleteGroup(groupId: String) {
        groupDao.deleteById(groupId)
    }

    suspend fun deleteArticles(group: Group? = null, feed: Feed? = null) {
        when {
            group != null -> articleDao.deleteByGroupId(CurrentAccountId, group.id)
            feed != null -> articleDao.deleteByFeedId(CurrentAccountId, feed.id)
        }
    }

    suspend fun deleteAccountArticles(accountId: Int) {
        articleDao.deleteByAccountId(accountId)
    }

    suspend fun groupParseFullContent(group: Group, sourceType: Int) {
        feedDao.updateIsFullContentByGroupId(CurrentAccountId, group.id, sourceType)
    }

    suspend fun groupAllowNotification(group: Group, isNotification: Boolean) {
        feedDao.updateInterceptionResourceByGroupId(CurrentAccountId, group.id, isNotification)
    }

    suspend fun groupMoveToTargetGroup(groupId: String, targetGroupId: String) {
        feedDao.updateTargetGroupIdByGroupId(CurrentAccountId, groupId, targetGroupId)
    }

    fun searchArticles2(
        content: String,
        groupId: String?,
        feedId: String?,
        isStarred: Boolean,
        isUnread: Boolean,
        limit: Int,
        offset: Int
    ): List<ArticleWithFeed> {
        val accountId = CurrentAccountId
        Log.i(
            "RLog",
            "searchArticles: content: ${content}, accountId: ${accountId}, groupId: ${groupId}, feedId: ${feedId}, isStarred: ${isStarred}, isUnread: ${isUnread}"
        )
        return when {
            groupId != null -> when {
                isStarred -> articleDao.searchArticleByGroupIdWhenIsStarredPaging(
                    accountId, content, groupId, true,
                    limit, offset
                )

                isUnread -> articleDao.searchArticleByGroupIdWhenIsUnreadPaging(
                    accountId, content, groupId, true,
                    limit, offset
                )

                else -> articleDao.searchArticleByGroupIdWhenAllPaging(
                    accountId, content, groupId,
                    limit, offset
                )
            }

            feedId != null -> when {
                isStarred -> articleDao.searchArticleByFeedIdWhenIsStarredPaging(
                    accountId, content, feedId, true,
                    limit, offset
                )

                isUnread -> articleDao.searchArticleByFeedIdWhenIsUnreadPaging(
                    accountId, content, feedId, true,
                    limit, offset
                )

                else -> articleDao.searchArticleByFeedIdWhenAllPaging(
                    accountId, content, feedId,
                    limit, offset
                )
            }

            else -> when {
                isStarred -> articleDao.searchArticleWhenIsStarredPaging(
                    accountId, content, true,
                    limit, offset
                )

                isUnread -> articleDao.searchArticleWhenIsUnreadPaging(
                    accountId, content, true,
                    limit, offset
                )

                else -> articleDao.searchArticleWhenAllPaging(
                    accountId, content,
                    limit, offset
                )
            }
        }
    }

    fun fastCountArticle(accountId: Int, feedId: String): StatusCount {
        return articleDao.fastQueryArticleUnreadOrStarredCursor(accountId, feedId).use { cursor ->
            val all = cursor.count
            var unread = 0
            var starred = 0
            while (cursor.moveToNext()) {
                val isStarred = cursor.getIntOrNull(0) != 0
                val isUnread = cursor.getIntOrNull(1) != 0
                unread += if (isUnread) 1 else 0
                starred += if (isStarred) 1 else 0
            }
            StatusCount.from(all, unread, starred)
        }
    }

    suspend fun queryTimeRangeUnreadArticlesPaging(
        accountId: Int = CurrentAccountId,
        startTime: Long,
        endTime: Long,
        isUnread: Boolean,
        isStarred: Boolean,
        limit: Int,
        offset: Int,
        desc: Boolean,
    ): List<ArticleWithFeed> {
        return when {
            isStarred -> articleDao.queryTimeRangeArticles(
                accountId, isStarred = true, isUnread = false,
                startTime = startTime, endTime = endTime,
                limit = limit, offset = offset, desc
            )

            isUnread -> articleDao.queryTimeRangeArticles(
                accountId, isStarred = false, isUnread = true,
                startTime, endTime,
                limit, offset, desc
            )

            else -> articleDao.queryTimeRangeArticles(
                accountId, false, isUnread = false,
                startTime = startTime, endTime = endTime,
                limit = limit, offset = offset, desc
            )
        }
    }

    fun countTimeRangeUnreadFlow(isUnread: Boolean, startTime: Long, endTime: Long): Flow<Int> {
        return articleDao.countTimeRangeArticlesWhenIsUnread(
            CurrentAccountId,
            isUnread,
            startTime,
            endTime
        )
    }

    suspend fun insertHistoryRecord(entity: ArticleHistory) {
        articleDao.updateReadingAtByArticleId(entity.accountId, entity.id, entity.readingAt)
        articleHistoryDao.insertWithDropOldest(entity)
    }
}
