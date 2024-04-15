package com.lowae.agrreader.data.repository

import android.content.Context
import androidx.work.WorkManager
import com.lowae.agrreader.data.dao.AccountDao
import com.lowae.agrreader.data.dao.ArticleDao
import com.lowae.agrreader.data.dao.ArticleHistoryDao
import com.lowae.agrreader.data.dao.FeedDao
import com.lowae.agrreader.data.dao.GroupDao
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.DefaultDispatcher
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.CurrentAccountId
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocalRssRepository @Inject constructor(
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
    @DefaultDispatcher
    private val defaultDispatcher: CoroutineDispatcher,
    workManager: WorkManager,
) : AbstractRssRepository(
    context, scope, accountDao, articleDao, articleHistoryDao, groupDao,
    feedDao, workManager, rssHelper, notificationHelper, ioDispatcher, defaultDispatcher
) {

    companion object {
        private const val SYNC_FEED_CONCURRENT_SIZE = 12
    }

    private var syncingJob: Job? = null

    override val tag: String = "LocalRssRepository"

    override suspend fun sync(feedId: String?, groupId: String?) {
        super.sync(feedId, groupId)
        syncingJob?.cancel()
        notifySyncProgress(SyncProgress.Start)
        val accountId = CurrentAccountId
        syncingJob = when {
            feedId != null -> {
                val feed = feedDao.queryById(feedId)
                syncFeed(listOfNotNull(feed))
            }

            groupId != null -> {
                val feed = feedDao.queryAllByGroupId(groupId)
                syncFeed(feed)
            }

            else -> {
                val allFeeds = feedDao.queryAll(accountId)
                syncFeed(allFeeds)
            }
        }
        syncingJob?.join()
        notifySyncProgress(SyncProgress.End)
        accountDao.queryById(accountId)?.let { account ->
            accountDao.update(account.apply { updateAt = Date() })
        }
        syncingJob = null
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    suspend fun syncFeed(feeds: List<Feed>): Job {
        return scope.launch {
            val preTime = System.currentTimeMillis()
            val syncProgress = AtomicInteger(0)
            val syncPreferenceConfig = FeedSyncPreferenceConfig.create()
            val channel = produce(capacity = SYNC_FEED_CONCURRENT_SIZE) {
                feeds.forEach { feed ->
                    send(async(ioDispatcher) {
                        syncFeed(syncPreferenceConfig, feed)
                        notifySyncProgress(
                            SyncProgress.Syncing(
                                syncProgress.incrementAndGet(), feeds.size, feed.name,
                            )
                        )
                        feed
                    })
                }
            }
            while (!(channel.isEmpty && channel.isClosedForReceive)) {
                channel.receive().await()
            }
            RLog.i(
                tag,
                "syncFeed: ${feeds.size}, onCompletion: ${System.currentTimeMillis() - preTime}"
            )
        }
    }

    private suspend fun syncFeed(config: FeedSyncPreferenceConfig, feed: Feed) {
        val latest = articleDao.queryLatestByFeedId(CurrentAccountId, feed.id)
        val result = rssHelper.queryRssXml(config, feed, latest?.link)
        var articles = result.articles
        if (feed.isNotification) {
            articles = articleDao.insertListIfNotExist(articles)
            notificationHelper.notify(feed, articles.size)
        } else {
            articleDao.insertListIfNotExist(articles)
        }
        if (result.needUpdateFeed) {
            feed.icon = result.iconUrl
            feedDao.update(feed)
        }
    }

    override fun cancelSync() {
        super.cancelSync()
        syncingJob?.cancel()
    }
}
