package com.lowae.agrreader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lowae.agrreader.data.model.general.FilterState
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.repository.RssRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val rssRepository: RssRepository,
    application: Application
) : AndroidViewModel(application) {


    private val _openArticleIntentFlow = Channel<String>()
    val openArticleIntentFlow = _openArticleIntentFlow.receiveAsFlow()

    private val _openFeedIntentFlow = Channel<FilterState>()
    val openFeedIntentFlow = _openFeedIntentFlow.receiveAsFlow()

    fun openArticleIntent(articleId: String) {
        if (articleId.isEmpty()) return
        viewModelScope.launch {
            _openArticleIntentFlow.send(articleId)
        }
    }

    fun openFeedIntent(feedId: String) {
        if (feedId.isEmpty()) return
        viewModelScope.launch(ioDispatcher) {
            rssRepository.get().findFeedById(feedId)?.also { feed ->
                _openFeedIntentFlow.send(FilterState(feed = feed))
            }
        }
    }
}