package com.lowae.agrreader.data.provider

enum class RssOperation {
    IMPORT_URL,
    IMPORT_OPML,
    GROUP,
    UN_SUBSCRIBE;

    companion object {
        val Local = RssOperation.entries.toList()
        val Fever = emptyList<RssOperation>()
        val GoogleReader = listOf(IMPORT_URL, UN_SUBSCRIBE)
        val FreshRSS = GoogleReader
        val Feedly = emptyList<RssOperation>()
        val Inoreader = emptyList<RssOperation>()
    }
}