package com.lowae.agrreader.data.provider.fever

import android.content.Context
import android.util.Log
import androidx.work.WorkManager
import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.action.ArticleMarkStarAction
import com.lowae.agrreader.data.dao.AccountDao
import com.lowae.agrreader.data.dao.ArticleDao
import com.lowae.agrreader.data.dao.ArticleHistoryDao
import com.lowae.agrreader.data.dao.FeedDao
import com.lowae.agrreader.data.dao.GroupDao
import com.lowae.agrreader.data.model.account.security.FeverSecurityKey
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.model.entities.FeverDTO
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.DefaultDispatcher
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.module.MainDispatcher
import com.lowae.agrreader.data.repository.AbstractRssRepository
import com.lowae.agrreader.data.repository.NotificationHelper
import com.lowae.agrreader.data.repository.RssHelper
import com.lowae.agrreader.data.repository.SyncProgress
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.showToast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeverRssRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @ApplicationScope
    private val scope: CoroutineScope,
    private val articleDao: ArticleDao,
    articleHistoryDao: ArticleHistoryDao,
    private val feedDao: FeedDao,
    rssHelper: RssHelper,
    private val notificationHelper: NotificationHelper,
    private val accountDao: AccountDao,
    private val groupDao: GroupDao,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher
    private val mainDispatcher: CoroutineDispatcher,
    @DefaultDispatcher
    private val defaultDispatcher: CoroutineDispatcher,
    workManager: WorkManager,
) : AbstractRssRepository(
    context, scope, accountDao, articleDao, articleHistoryDao, groupDao,
    feedDao, workManager, rssHelper, notificationHelper, ioDispatcher, defaultDispatcher
) {

    companion object {
        private const val TAG = "FeverRssRepository"
        private val FEVER_DEFAULT_GROUP_ID =
            UUID.nameUUIDFromBytes("Fever".toByteArray()).toString()
        private val feverDefaultGroupTitle = setOf("未分类", "Uncategorized")
    }

    override val groupOperation: Boolean = false
    override val feedOperation: Boolean = false
    override val articleOperation: Boolean = false

    private suspend fun getFeverAPI() =
        FeverSecurityKey(accountDao.queryById(CurrentAccountId)!!.securityKey).run {
            FeverAPI.getInstance(
                serverUrl = serverUrl!!,
                username = username!!,
                password = password!!,
                httpUsername = null,
                httpPassword = null,
            )
        }

    override suspend fun validCredentials(): Boolean = getFeverAPI().validCredentials() > 0

    override suspend fun subscribe(feed: Feed, articles: List<Article>) {
        throw Exception("Unsupported")
    }

    override suspend fun addGroup(name: String): String {
        throw Exception("Unsupported")
    }

    /**
     * Sync handling for the Fever API.
     *
     * 1. Fetch the Fever groups
     * 2. Fetch the Fever feeds
     * 3. Fetch the Fever favicons
     * 4. Fetch the Fever articles
     */
    override suspend fun sync(feedId: String?, groupId: String?) {
        super.sync(feedId, groupId)
        return supervisorScope {
            notifySyncProgress(SyncProgress.Start)
            try {
                val preTime = System.currentTimeMillis()
                val accountId = CurrentAccountId
                val feverAPI = getFeverAPI()

                val feverGroups = feverAPI.getGroups()
                if (feverGroups.isEmpty()) {
                    groupDao.insert(generateFeverDefaultGroup(accountId))
                }
                // 1. Fetch the Fever groups
                groupDao.insertOrUpdate(feverGroups)
                val defaultGroup = feverGroups.find { feverDefaultGroupTitle.contains(it.name) }
                RLog.d(TAG, "feverGroups: ${feverGroups.size}")

                // 2. Fetch the Fever feeds
                val feedsBody = feverAPI.getFeeds()
                val feedsGroupsMap = mutableMapOf<String, String>()
                feedsBody.feeds_groups?.forEach { feedsGroups ->
                    feedsGroups.group_id?.toString()?.let { groupId ->
                        feedsGroups.feed_ids?.split(",")?.forEach { feedId ->
                            feedsGroupsMap[feedId] = groupId
                        }
                    }
                }
                RLog.d(TAG, "feverGroups: ${feedsGroupsMap.size}")
                val feverFeeds = feedsBody.feeds.orEmpty()
                val insertedFeeds = feedDao.insertOrUpdate(
                    feverFeeds.mapNotNull {
                        val belongGroupId = feedsGroupsMap[it.id.toString()]
                            ?: defaultGroup?.id
                            ?: feverGroups.firstOrNull()?.id
                            ?: FEVER_DEFAULT_GROUP_ID

                        it.convertToFeed(accountId, belongGroupId)
                    }
                )
                RLog.d(TAG, "feverFeeds: ${feverFeeds.size}")

                if (insertedFeeds.isNotEmpty()) {
                    val feedIdToFaviconIdMap =
                        feedsBody.feeds.orEmpty().associate { it.id to it.favicon_id }
                    val favicons = feverAPI.getFavicons()
                    insertedFeeds.forEach {
                        val feedFaviconId =
                            feedIdToFaviconIdMap[it.id.toIntOrNull()] ?: return@forEach
                        it.icon =
                            favicons[feedFaviconId]?.data.orEmpty().substringAfter("base64,", "")
                    }
                    feedDao.insertOrUpdate(insertedFeeds)
                }

                val articleStatusIds = feverAPI.syncArticleStatus()
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
                val localFeeds = feedDao.queryAll(accountId)
                // 3. Fetch the Fever articles (up to unlimited counts)
                val articles = mutableListOf<Article>()
                missingArticleIds.chunked(50).forEach { chunk ->
                    val articleChunk = feverAPI.getItemsWith(chunk).items
                        ?.mapNotNull {
                            it.convertToArticle(accountId, rssHelper)
                        }?.filter {
                            localFeeds.find { feed -> feed.id == it.feedId } != null
                        }.orEmpty()
                    RLog.d(TAG, "getItemsWith: ${articleChunk.size}")
                    articleDao.insertListIfNotExist(articleChunk)
                    articles.addAll(articleChunk)
                }

                RLog.d(TAG, "onCompletion: ${System.currentTimeMillis() - preTime}")
                accountDao.queryById(accountId)?.let { account ->
                    accountDao.update(account.apply { updateAt = Date() })
                }
                articles.groupBy { it.feedId }.forEach { (feedId, feedArticles) ->
                    val feed = feedDao.queryById(feedId) ?: return@forEach
                    if (feed.isNotification.not()) return@forEach
                    notificationHelper.notify(feed, feedArticles.size)
                }
            } catch (e: Exception) {
                Log.e("RLog", "On sync exception: ${e.message}", e)
                withContext(mainDispatcher) {
                    context.showToast(e.message)
                }
            } finally {
                notifySyncProgress(SyncProgress.End)
            }
        }
    }

    override suspend fun markAsRead(action: ArticleMarkReadAction) {
        val (groupId, feedId, articleId, before, isUnread, latest) = action
        val feverAPI = getFeverAPI()
        val accountId = CurrentAccountId
        val ids = mutableListOf<String>()
        when {
            groupId != null -> {
                if (latest && before != null) {
                    ids += articleDao.getMarkAllAsReadByGroupIdLatest(
                        accountId = accountId,
                        groupId = groupId,
                        isUnread = isUnread.not(),
                        before = before
                    )
                } else {
                    feverAPI.markGroup(
                        status = if (isUnread) FeverDTO.StatusEnum.Unread else FeverDTO.StatusEnum.Read,
                        id = groupId.toLong(),
                        before = (before?.time ?: Date().time) / 1000
                    )
                }
            }

            feedId != null -> {
                if (latest && before != null) {
                    ids += articleDao.getMarkAllAsReadByFeedIdLatest(
                        accountId = accountId,
                        feedId = feedId,
                        isUnread = isUnread.not(),
                        before = before
                    )
                } else {
                    feverAPI.markFeed(
                        status = if (isUnread) FeverDTO.StatusEnum.Unread else FeverDTO.StatusEnum.Read,
                        id = feedId.toLong(),
                        before = (before?.time ?: Date().time) / 1000
                    )
                }
            }

            articleId != null -> {
                ids += articleId
            }

            else -> {
                if (latest && before != null) {
                    ids += articleDao.getMarkAllAsReadLatest(
                        accountId,
                        isUnread.not(),
                        before
                    )
                } else {
                    feedDao.queryAll(CurrentAccountId).forEach {
                        feverAPI.markFeed(
                            status = if (isUnread) FeverDTO.StatusEnum.Unread else FeverDTO.StatusEnum.Read,
                            id = it.id.toLong(),
                            before = (before?.time ?: Date().time) / 1000
                        )
                    }
                }
            }
        }
        try {
            feverAPI.markItemsRead(ids, isUnread)
            super.markAsRead(action)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun markAsStarred(action: ArticleMarkStarAction) {
        super.markAsStarred(action)
        val feverAPI = getFeverAPI()
        feverAPI.markItem(
            status = if (action.isStarred) FeverDTO.StatusEnum.Saved else FeverDTO.StatusEnum.Unsaved,
            id = action.articleId
        )
    }

    private fun generateFeverDefaultGroup(accountId: Int): Group {
        return Group(
            id = FEVER_DEFAULT_GROUP_ID,
            name = "未分类",
            accountId = accountId,
        )
    }
}
