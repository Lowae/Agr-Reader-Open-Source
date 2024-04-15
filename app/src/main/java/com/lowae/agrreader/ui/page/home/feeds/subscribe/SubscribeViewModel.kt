package com.lowae.agrreader.ui.page.home.feeds.subscribe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.account.AccountType
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.repository.OpmlRepository
import com.lowae.agrreader.data.repository.RssHelper
import com.lowae.agrreader.data.repository.RssRepository
import com.lowae.agrreader.data.repository.StringsRepository
import com.lowae.agrreader.utils.ext.CurrentAccountType
import com.lowae.agrreader.utils.ext.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class SubscribeViewModel @Inject constructor(
    private val opmlRepository: OpmlRepository,
    @ApplicationScope
    private val scope: CoroutineScope,
    val rssRepository: RssRepository,
    private val rssHelper: RssHelper,
    private val stringsRepository: StringsRepository,
) : ViewModel() {

    private val _subscribeUiState = MutableStateFlow(SubscribeUiState())
    val subscribeUiState: StateFlow<SubscribeUiState> = _subscribeUiState.asStateFlow()

    private var searchJob: Job? = null

    fun init() {
        _subscribeUiState.update {
            it.copy(
                title = stringsRepository.getString(R.string.subscribe),
                groups = rssRepository.get().pullGroups(),
            )
        }
    }

    fun reset() {
        searchJob?.cancel()
        searchJob = null
        _subscribeUiState.update {
            SubscribeUiState().copy(title = stringsRepository.getString(R.string.subscribe))
        }
    }

    fun importFromInputStream(inputStream: InputStream) {
        when (CurrentAccountType) {
            AccountType.FreshRSS.id -> {
                toast("Rss服务不支持导入Opml，请在网页端操作")
                return
//                viewModelScope.launch {
//                    try {
//                        rssRepository.get().addOpml(inputStream)
//                    } catch (e: Exception) {
//                        toast("Rss服务不支持导入Opml，请在网页端操作")
//                        e.printStackTrace()
//                    }
//                }
            }

            AccountType.Local.id -> opmlRepository.parserFeedFromFile2(inputStream) {
                toast("OPML文件解析失败")
            }
        }
    }

    fun selectedGroup(groupId: String) {
        _subscribeUiState.update { it.copy(selectedGroupId = groupId) }
    }

    fun addNewGroup(groupName: String) {
        if (groupName.isNotBlank()) {
            viewModelScope.launch {
                selectedGroup(rssRepository.get().addGroup(groupName))
                hideNewGroupDialog()
            }
        }
    }

    fun changeParseFullContentPreset(sourceType: Int) {
        _subscribeUiState.update {
            it.copy(contentSourceTypePreset = sourceType)
        }
    }

    fun changeAllowNotificationPreset() {
        _subscribeUiState.update {
            it.copy(allowNotificationPreset = !_subscribeUiState.value.allowNotificationPreset)
        }
    }

    fun search() {
        searchJob?.cancel()
        viewModelScope.launch {
            try {
                _subscribeUiState.update {
                    it.copy(
                        errorMessage = "",
                    )
                }
                val searchFeedUrl = validCheckedUrl() ?: return@launch
                _subscribeUiState.update {
                    it.copy(
                        linkContent = searchFeedUrl,
                        title = stringsRepository.getString(R.string.searching),
                        lockLinkInput = true,
                    )
                }
                if (rssRepository.get().isFeedExist(searchFeedUrl)) {
                    _subscribeUiState.update {
                        it.copy(
                            title = stringsRepository.getString(R.string.subscribe),
                            errorMessage = stringsRepository.getString(R.string.already_subscribed),
                            lockLinkInput = false,
                        )
                    }
                    return@launch
                }
                val feed = rssHelper.searchRssFeedFromUrl(searchFeedUrl)
                _subscribeUiState.update {
                    it.copy(
                        feed = feed,
                        lockLinkInput = false
                    )
                }
                switchPage(false)
            } catch (e: Exception) {
                e.printStackTrace()
                _subscribeUiState.update {
                    it.copy(
                        title = stringsRepository.getString(R.string.subscribe),
                        errorMessage = stringsRepository.getString(R.string.search_rss_url_unknow_error),
                        lockLinkInput = false,
                    )
                }
            }
        }.also {
            searchJob = it
        }
    }

    fun subscribe(accountType: AccountType) {
        when (accountType) {
            AccountType.FreshRSS -> {
                scope.launch {
                    validCheckedUrl()?.also {
                        rssRepository.get().subscribe(it)
                    }
                }
            }

            AccountType.Local -> {
                val feed = _subscribeUiState.value.feed ?: return
                scope.launch {
                    rssRepository.get().subscribe(
                        feed.copy(
                            groupId = _subscribeUiState.value.selectedGroupId,
                            isNotification = _subscribeUiState.value.allowNotificationPreset,
                            sourceType = _subscribeUiState.value.contentSourceTypePreset,
                            interceptionResource = _subscribeUiState.value.interceptionResource,
                        ), emptyList()
                    )
                    rssRepository.get().sync(feedId = feed.id)
                }
            }
        }
    }

    fun inputLink(content: String) {
        _subscribeUiState.update {
            it.copy(
                linkContent = content,
                errorMessage = "",
            )
        }
    }

    fun showNewGroupDialog() {
        _subscribeUiState.update { it.copy(newGroupDialogVisible = true) }
    }

    fun hideNewGroupDialog() {
        _subscribeUiState.update { it.copy(newGroupDialogVisible = false) }
    }

    fun switchPage(isSearchPage: Boolean) {
        _subscribeUiState.update { it.copy(isSearchPage = isSearchPage) }
    }

    fun changeTranslationLanguage(translationLanguage: Pair<String, String>?) {
        _subscribeUiState.update {
            it.copy(
                feed = it.feed?.copy(translationLanguage = translationLanguage),
            )
        }
    }


    fun showRenameDialog() {
        _subscribeUiState.update {
            it.copy(
                renameDialogVisible = true,
                newName = _subscribeUiState.value.feed?.name ?: "",
            )
        }
    }

    fun hideRenameDialog() {
        _subscribeUiState.update {
            it.copy(
                renameDialogVisible = false,
                newName = "",
            )
        }
    }

    fun inputNewName(content: String) {
        _subscribeUiState.update { it.copy(newName = content) }
    }

    fun renameFeed() {
        _subscribeUiState.value.feed?.let {
            _subscribeUiState.update {
                it.copy(
                    feed = it.feed?.copy(name = _subscribeUiState.value.newName),
                )
            }
        }
    }

    private fun validCheckedUrl(): String? {
        val searchFeedUrl =
            _subscribeUiState.value.linkContent.toHttpUrlOrNull()?.toString()
        if (searchFeedUrl == null) {
            _subscribeUiState.update {
                it.copy(
                    title = stringsRepository.getString(R.string.subscribe),
                    errorMessage = stringsRepository.getString(R.string.search_rss_url_illegal_error),
                    lockLinkInput = false,
                )
            }
        }
        return searchFeedUrl
    }

    fun changeInterceptionResource() {
        _subscribeUiState.update {
            it.copy(interceptionResource = !_subscribeUiState.value.interceptionResource)
        }
    }
}

data class SubscribeUiState(
    val title: String = "",
    val errorMessage: String = "",
    val linkContent: String = "",
    val lockLinkInput: Boolean = false,
    val feed: Feed? = null,
    val allowNotificationPreset: Boolean = false,
    val contentSourceTypePreset: Int = Feed.SOURCE_TYPE_FULL_CONTENT,
    val interceptionResource: Boolean = false,
    val selectedGroupId: String = "",
    val newGroupDialogVisible: Boolean = false,
    val newGroupContent: String = "",
    val groups: Flow<List<Group>> = emptyFlow(),
    val isSearchPage: Boolean = true,
    val newName: String = "",
    val renameDialogVisible: Boolean = false,
)
