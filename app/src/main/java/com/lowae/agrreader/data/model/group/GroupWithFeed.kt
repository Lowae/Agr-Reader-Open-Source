package com.lowae.agrreader.data.model.group

import androidx.room.Embedded
import androidx.room.Relation
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.feed.StatusCount

/**
 * A [group] contains many [feeds].
 */
data class GroupWithFeed(
    @Embedded
    var group: Group,
    @Relation(parentColumn = "id", entityColumn = "groupId")
    var feeds: MutableList<Feed>,
) {

    fun calculateGroupStatusCount(): StatusCount {
        var all = 0
        var unread = 0
        var starred = 0
        feeds.forEach { feed ->
            all += feed.count.all
            unread += feed.count.unread
            starred += feed.count.starred
        }
        return StatusCount.from(all, unread, starred)
    }

}
