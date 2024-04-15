package com.lowae.agrreader.data.model.feed

import com.lowae.agrreader.data.model.general.AllValue
import com.lowae.agrreader.data.model.general.StarredValue
import com.lowae.agrreader.data.model.general.UnreadValue

data class StatusCount(
    private val _all: AllValue<Int>,
    private val _unread: UnreadValue<Int>,
    private val _starred: StarredValue<Int>
) {
    val all: Int
        get() = _all.value
    val unread: Int
        get() = _unread.value

    val starred: Int
        get() = _starred.value

    companion object {
        val DEFAULT = StatusCount(AllValue(0), UnreadValue(0), StarredValue(0))

        fun from(all: Int, unread: Int, starred: Int) =
            StatusCount(AllValue(all), UnreadValue(unread), StarredValue(starred))
    }
}