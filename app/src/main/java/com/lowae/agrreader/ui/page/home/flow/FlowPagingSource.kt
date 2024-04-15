package com.lowae.agrreader.ui.page.home.flow

sealed interface FlowPagingSource {

    object Default : FlowPagingSource

    data class Search(val searchContent: String) : FlowPagingSource
}