package com.lowae.agrreader.data.repository

import android.content.Context
import android.os.Looper
import com.lowae.agrreader.R
import com.lowae.agrreader.data.dao.AccountDao
import com.lowae.agrreader.data.dao.ArticleDao
import com.lowae.agrreader.data.dao.FeedDao
import com.lowae.agrreader.data.dao.GroupDao
import com.lowae.agrreader.data.model.account.Account
import com.lowae.agrreader.data.model.account.AccountType
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.DataStore
import com.lowae.agrreader.utils.ext.DataStoreKeys
import com.lowae.agrreader.utils.ext.GroupIdGenerator
import com.lowae.agrreader.utils.ext.put
import com.lowae.agrreader.utils.ext.showToast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val accountDao: AccountDao,
    private val groupDao: GroupDao,
    private val feedDao: FeedDao,
    private val articleDao: ArticleDao,
    private val rssRepository: RssRepository,
) {

    val accounts: Flow<List<Account>> = accountDao.queryAllAsFlow()
    val currentAccount = DataStore.data.map { it[DataStoreKeys.CurrentAccountId.key] }
        .filter { it != null }
        .flatMapLatest { getAccountFlow(it!!) }

    suspend fun getCurrentAccount(): Account = accountDao.queryById(CurrentAccountId)!!

    suspend fun isNoAccount(): Boolean = accountDao.queryAll().isEmpty()

    suspend fun addAccount(account: Account): Account =
        account.apply {
            id = accountDao.insert(this).toInt()
        }.also {
            // handle default group
            when (it.type) {
                AccountType.Local -> {
                    groupDao.insert(
                        Group(
                            id = GroupIdGenerator.DEFAULT_ID,
                            name = context.getString(R.string.defaults),
                            accountId = it.id!!,
                        )
                    )
                }
            }
            DataStore.put(DataStoreKeys.CurrentAccountId, it.id!!)
            DataStore.put(DataStoreKeys.CurrentAccountType, it.type.id)
        }

    suspend fun addDefaultAccount(): Account =
        addAccount(
            Account(
                type = AccountType.Local,
                name = context.getString(R.string.app_name),
            )
        )

    suspend fun update(accountId: Int, block: Account.() -> Unit) {
        accountDao.queryById(accountId)?.let {
            accountDao.update(it.apply(block))
        }
    }

    suspend fun delete(accountId: Int) {
        if (accountDao.queryAll().size == 1) {
            Looper.myLooper() ?: Looper.prepare()
            context.showToast(context.getString(R.string.must_have_an_account))
            Looper.loop()
            return
        }
        accountDao.queryById(accountId)?.let {
            it.type.removePreferences()
            articleDao.deleteByAccountId(accountId)
            feedDao.deleteByAccountId(accountId)
            groupDao.deleteByAccountId(accountId)
            accountDao.delete(it)
            accountDao.queryAll().getOrNull(0)?.let {
                DataStore.put(DataStoreKeys.CurrentAccountId, it.id!!)
                DataStore.put(DataStoreKeys.CurrentAccountType, it.type.id)
            }
        }
    }

    suspend fun switch(account: Account) {
        rssRepository.get().cancelSync()
        DataStore.put(DataStoreKeys.CurrentAccountId, account.id!!)
        DataStore.put(DataStoreKeys.CurrentAccountType, account.type.id)
        // Restart
        // context.packageManager.getLaunchIntentForPackage(context.packageName)?.let {
        //     it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //     context.startActivity(it)
        //     android.os.Process.killProcess(android.os.Process.myPid())
        // }
    }

    private fun getAccountFlow(accountId: Int) = accountDao.queryAccountFlow(accountId)

}
