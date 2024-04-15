package com.lowae.agrreader.data.provider.greader

import android.content.Context
import androidx.work.WorkManager
import com.lowae.agrreader.R
import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.action.ArticleMarkStarAction
import com.lowae.agrreader.data.dao.AccountDao
import com.lowae.agrreader.data.dao.ArticleDao
import com.lowae.agrreader.data.dao.ArticleHistoryDao
import com.lowae.agrreader.data.dao.FeedDao
import com.lowae.agrreader.data.dao.GroupDao
import com.lowae.agrreader.data.model.account.security.GoogleReaderSecurityKey
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.DefaultDispatcher
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.repository.AbstractRssRepository
import com.lowae.agrreader.data.repository.NotificationHelper
import com.lowae.agrreader.data.repository.RssHelper
import com.lowae.agrreader.data.repository.SyncProgress
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.toast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class GReaderRssRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @ApplicationScope
    private val scope: CoroutineScope,
    private val accountDao: AccountDao,
    private val articleDao: ArticleDao,
    articleHistoryDao: ArticleHistoryDao,
    private val groupDao: GroupDao,
    private val feedDao: FeedDao,
    workManager: WorkManager,
    rssHelper: RssHelper,
    private val notificationHelper: NotificationHelper,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher
    private val defaultDispatcher: CoroutineDispatcher,
) : AbstractRssRepository(
    context, scope, accountDao, articleDao, articleHistoryDao, groupDao,
    feedDao, workManager, rssHelper, notificationHelper, ioDispatcher, defaultDispatcher
) {

    private val accountId: Int
        get() = CurrentAccountId

    override val groupOperation: Boolean
        get() {
            toast(R.string.rss_server_operation_disable_toast)
            return false
        }

    override val feedOperation: Boolean
        get() = false

    protected open val gReaderApi: GReaderAPI = GReaderAPI(rssHelper, GoogleReaderSecurityKey())

    override suspend fun sync(feedId: String?, groupId: String?) {
        super.sync(feedId, groupId)
        syncInner()
    }

    override suspend fun validCredentials(): Boolean = gReaderApi.validCredentials()

    override suspend fun reAuthenticate() {
        gReaderApi.securityKey =
            GoogleReaderSecurityKey(accountDao.queryById(accountId)!!.securityKey)
        gReaderApi.reAuthenticate()
    }

    override suspend fun markAsRead(action: ArticleMarkReadAction) {
        reAuthenticate()
        val (groupId, feedId, articleId, before, isUnread, latest) = action
        val ids = mutableListOf<String>()
        when {
            groupId != null -> {
                ids += if (latest && before != null) {
                    articleDao.getMarkAllAsReadByGroupIdLatest(
                        accountId = accountId,
                        groupId = groupId,
                        isUnread = isUnread.not(),
                        before = before
                    )
                } else {
                    articleDao.getMarkAllAsReadByGroupId(
                        accountId,
                        groupId,
                        isUnread.not(),
                        before ?: Date()
                    )
                }
            }

            feedId != null -> {
                ids += if (latest && before != null) {
                    articleDao.getMarkAllAsReadByFeedIdLatest(
                        accountId = accountId,
                        feedId = feedId,
                        isUnread = isUnread.not(),
                        before = before
                    )
                } else {
                    articleDao.getMarkAllAsReadByFeedId(
                        accountId = accountId,
                        feedId = feedId,
                        isUnread = isUnread.not(),
                        before ?: Date()
                    )
                }
            }

            articleId != null -> {
                ids += articleId
            }

            else -> {
                ids += if (latest && before != null) {
                    articleDao.getMarkAllAsReadLatest(
                        accountId,
                        isUnread.not(),
                        before
                    )
                } else {
                    articleDao.getMarkAllAsRead(
                        accountId,
                        isUnread.not(),
                        before ?: Date()
                    )
                }
            }
        }
        RLog.d("markAsRead", "ids: ${ids.size}")
        try {
            gReaderApi.markAsRead(ids, isUnread)
            super.markAsRead(action)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun markAsStarred(action: ArticleMarkStarAction) {
        reAuthenticate()
        gReaderApi.markAsStarred(action.articleId, action.isStarred)
        super.markAsStarred(action)
    }

    override suspend fun deleteFeed(feed: Feed) {
        reAuthenticate()
        gReaderApi.deleteFeed(feed)
        super.deleteFeed(feed)
    }

    override suspend fun addFeed(feed: Feed) {
        reAuthenticate()
        gReaderApi.addFeed(feed.url)
        super.addFeed(feed)
        syncInner()
    }

    override suspend fun updateFeed(feed: Feed) {
        reAuthenticate()
        super.updateFeed(feed)
        gReaderApi.updateFeed(feed)
    }

    override suspend fun addGroup(name: String): String {
        reAuthenticate()
        val groupId = gReaderApi.addGroup(name) ?: return ""
        val accountId = CurrentAccountId
        groupDao.insert(Group(id = groupId, name = name, accountId = accountId))
        return groupId
    }

    override suspend fun subscribe(feedUrl: String) {
        super.subscribe(feedUrl)
        reAuthenticate()
        gReaderApi.addFeed(feedUrl)
        syncInner()
    }

    private suspend fun syncInner() {
        val preTime = System.currentTimeMillis()
        notifySyncProgress(SyncProgress.Start)
        try {
            reAuthenticate()
            val groupWithFeeds = gReaderApi.fetchGroupWithFeeds()
            RLog.i("sync", "fetchGroupWithFeeds: ${System.currentTimeMillis() - preTime}")
            groupDao.insertOrUpdate(groupWithFeeds.map { it.group })
            feedDao.insertOrUpdate(groupWithFeeds.flatMap { it.feeds })
            val articleStatusIds = gReaderApi.syncArticleStatus()
            RLog.i("sync", "syncArticleStatus: ${System.currentTimeMillis() - preTime}")

            articleDao.queryArticleIdWhereUnreadOrStarred(
                accountId,
                unread = true,
                starred = true
            ).forEach {
                // 如果本地是未读但是云端非未读则重置本地未读状态
                // todo 判断已哪一端为主
                if (it.isUnread && !articleStatusIds.unreadIds.remove(it.id)) {
                    articleDao.markAsReadByArticleId(accountId, it.id, false)
                }
                if (it.isStarred && !articleStatusIds.starredIds.remove(it.id)) {
                    articleDao.markAsStarredByArticleId(accountId, it.id, false)
                }
            }

//            articleDao.insertList(gReaderApi.fetchArticles())

            val missingArticleIds = mutableListOf<String>()

            articleStatusIds.unreadIds.forEach {
                val updateResult = articleDao.markAsReadByArticleId(accountId, it, true)
                if (updateResult == 0) {
                    missingArticleIds.add(it)
                }
            }
            articleStatusIds.starredIds.forEach {
                val updateResult = articleDao.markAsStarredByArticleId(accountId, it, true)
                if (updateResult == 0) {
                    missingArticleIds.add(it)
                }
            }

            val articles: List<Article> = if (missingArticleIds.isNotEmpty()) {
                val missArticles = gReaderApi.fetchArticles(missingArticleIds)
                val result = articleDao.insertList(missArticles)
                RLog.i("sync", "missArticles insert count: ${result.size}")
                missArticles
            } else {
                emptyList()
            }
            RLog.i("sync", "fetchArticles missing: ${System.currentTimeMillis() - preTime}")

            accountDao.queryById(accountId)?.let { account ->
                accountDao.update(account.apply { updateAt = Date() })
            }
            articles.groupBy { it.feedId }.forEach { (feedId, feedArticles) ->
                val feed = feedDao.queryById(feedId) ?: return@forEach
                if (feed.isNotification.not()) return@forEach
                notificationHelper.notify(feed, feedArticles.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            RLog.i("sync", "onCompletion: ${System.currentTimeMillis() - preTime}")
            notifySyncProgress(SyncProgress.End)
        }

    }
}