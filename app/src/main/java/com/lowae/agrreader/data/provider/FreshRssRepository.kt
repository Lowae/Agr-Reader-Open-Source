package com.lowae.agrreader.data.provider

import android.content.Context
import androidx.work.WorkManager
import com.lowae.agrreader.data.dao.AccountDao
import com.lowae.agrreader.data.dao.ArticleDao
import com.lowae.agrreader.data.dao.ArticleHistoryDao
import com.lowae.agrreader.data.dao.FeedDao
import com.lowae.agrreader.data.dao.GroupDao
import com.lowae.agrreader.data.model.account.security.FreshRSSSecurityKey
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.module.DefaultDispatcher
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.provider.greader.GReaderRssRepository
import com.lowae.agrreader.data.repository.NotificationHelper
import com.lowae.agrreader.data.repository.RssHelper
import com.lowae.agrreader.utils.ext.CurrentAccountId
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FreshRssRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @ApplicationScope
    private val scope: CoroutineScope,
    articleDao: ArticleDao,
    articleHistoryDao: ArticleHistoryDao,
    feedDao: FeedDao,
    rssHelper: RssHelper,
    notificationHelper: NotificationHelper,
    private val accountDao: AccountDao,
    groupDao: GroupDao,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher
    private val defaultDispatcher: CoroutineDispatcher,
    workManager: WorkManager,
) : GReaderRssRepository(
    context, scope, accountDao, articleDao, articleHistoryDao, groupDao,
    feedDao, workManager, rssHelper, notificationHelper, ioDispatcher, defaultDispatcher
) {
    private val accountId: Int
        get() = CurrentAccountId

    override suspend fun reAuthenticate() {
        gReaderApi.securityKey = FreshRSSSecurityKey(accountDao.queryById(accountId)!!.securityKey)
        gReaderApi.reAuthenticate()
    }

}