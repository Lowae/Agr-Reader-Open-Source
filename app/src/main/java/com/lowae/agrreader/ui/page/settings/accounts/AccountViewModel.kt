package com.lowae.agrreader.ui.page.settings.accounts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.data.model.account.Account
import com.lowae.agrreader.data.module.DefaultDispatcher
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.module.MainDispatcher
import com.lowae.agrreader.data.repository.AccountRepository
import com.lowae.agrreader.data.repository.OpmlRepository
import com.lowae.agrreader.data.repository.RssRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val rssRepository: RssRepository,
    private val opmlRepository: OpmlRepository,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher
    private val defaultDispatcher: CoroutineDispatcher,
    @MainDispatcher
    private val mainDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _accountUiState: MutableStateFlow<AccountUiState>
    val accountUiState: StateFlow<AccountUiState>

    init {
        val accountsStateFlow = accountRepository.accounts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        val currentAccountStateFlow = accountRepository.currentAccount.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
        _accountUiState =
            MutableStateFlow(AccountUiState(accountsStateFlow, currentAccountStateFlow))
        accountUiState = _accountUiState.asStateFlow()
    }

    fun update(accountId: Int, block: Account.() -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            accountRepository.update(accountId, block)
        }
    }

    fun exportAsOPML(accountId: Int, callback: (String) -> Unit = {}) {
        viewModelScope.launch(defaultDispatcher) {
            try {
                callback(opmlRepository.saveToString(accountId))
            } catch (e: Exception) {
                Log.e("FeedsViewModel", "exportAsOpml: ", e)
            }
        }
    }

    fun hideDeleteDialog() {
        _accountUiState.update { it.copy(deleteDialogVisible = false) }
    }

    fun showDeleteDialog() {
        _accountUiState.update { it.copy(deleteDialogVisible = true) }
    }

    fun showClearDialog() {
        _accountUiState.update { it.copy(clearDialogVisible = true) }
    }

    fun hideClearDialog() {
        _accountUiState.update { it.copy(clearDialogVisible = false) }
    }

    fun delete(accountId: Int, callback: () -> Unit = {}) {
        viewModelScope.launch(ioDispatcher) {
            accountRepository.delete(accountId)
            withContext(mainDispatcher) {
                callback()
            }
        }
    }

    fun clear(account: Account, callback: () -> Unit = {}) {
        viewModelScope.launch(ioDispatcher) {
            rssRepository.get(account.type.id).deleteAccountArticles(account.id!!)
            withContext(mainDispatcher) {
                callback()
            }
        }
    }

    fun addAccount(account: Account, callback: (Account?) -> Unit = {}) {
        viewModelScope.launch(ioDispatcher) {
            val addAccount = accountRepository.addAccount(account)
            try {
                rssRepository.get(addAccount.type.id).reAuthenticate()
                if (rssRepository.get(addAccount.type.id).validCredentials()) {
                    withContext(mainDispatcher) {
                        callback(addAccount)
                    }
                    accountRepository.switch(addAccount)
                    rssRepository.get().sync()
                } else {
                    throw Exception("Unauthorized")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                accountRepository.delete(account.id!!)
                withContext(mainDispatcher) {
                    callback(null)
                }
            }
        }
    }

    fun switchAccount(targetAccount: Account, callback: () -> Unit = {}) {
        viewModelScope.launch(ioDispatcher) {
            accountRepository.switch(targetAccount)
            withContext(mainDispatcher) {
                callback()
            }
        }
    }
}

data class AccountUiState(
    val accounts: StateFlow<List<Account>>,
    val currentAccount: StateFlow<Account?>,
    val deleteDialogVisible: Boolean = false,
    val clearDialogVisible: Boolean = false,
)
