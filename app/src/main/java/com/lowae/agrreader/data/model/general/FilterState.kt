package com.lowae.agrreader.data.model.general

import android.os.Parcelable
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.group.Group
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterState(
    val group: Group? = null,
    val feed: Feed? = null,
    val filter: Filter = Filter.Unread,
) : Parcelable