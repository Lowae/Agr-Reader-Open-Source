package com.lowae.agrreader.data.model.general

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.ChromeReaderMode
import androidx.compose.material.icons.outlined.MarkAsUnread
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.ChromeReaderMode
import androidx.compose.material.icons.rounded.MarkAsUnread
import androidx.compose.ui.graphics.vector.ImageVector
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.general.Filter.All
import com.lowae.agrreader.data.model.general.Filter.Starred
import com.lowae.agrreader.data.model.general.Filter.Unread
import com.lowae.agrreader.utils.ext.getString

/**
 * Indicates filter conditions.
 *
 * - [All]: all items
 * - [Unread]: unread items
 * - [Starred]: starred items
 */
enum class Filter {
    Starred,
    Unread,
    All;

    fun isStarred(): Boolean = this == Starred
    fun isUnread(): Boolean = this == Unread
    fun isAll(): Boolean = this == All

}

inline val Filter.title: String
    get() = when (this) {
        Starred -> getString(R.string.starred)
        Unread -> getString(R.string.unread)
        All -> getString(R.string.all)
    }


inline val Filter.iconOutline: ImageVector
    get() = when (this) {
        Starred -> Icons.Outlined.Bookmarks
        Unread -> Icons.Outlined.MarkAsUnread
        All -> Icons.Outlined.ChromeReaderMode
    }

inline val Filter.iconFilled: ImageVector
    get() = when (this) {
        Starred -> Icons.Rounded.Bookmarks
        Unread -> Icons.Rounded.MarkAsUnread
        All -> Icons.Rounded.ChromeReaderMode
    }
