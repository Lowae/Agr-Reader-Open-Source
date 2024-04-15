package com.lowae.agrreader.data.model.article

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.source.RYDatabase
import java.util.Date

/**
 * TODO: Add class description
 */
@Entity(
    tableName = RYDatabase.TABLE_NAME_ARTICLE,
    foreignKeys = [ForeignKey(
        entity = Feed::class,
        parentColumns = ["id"],
        childColumns = ["feedId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Article(
    @PrimaryKey
    var id: String,
    @ColumnInfo
    var date: Date,
    @ColumnInfo
    var title: String,
    @ColumnInfo
    var translationTitle: String? = null,
    @ColumnInfo
    var author: String? = null,
    @ColumnInfo
    var rawDescription: String,
    @ColumnInfo
    var shortDescription: String,
    @ColumnInfo
    var sourceHtml: String = "",
    @ColumnInfo
    var fullContent: String? = null,
    @ColumnInfo
    var img: String? = null,
    @ColumnInfo
    var link: String,
    @ColumnInfo(index = true)
    var feedId: String,
    @ColumnInfo(index = true)
    var accountId: Int,
    @ColumnInfo
    var isUnread: Boolean = true,
    @ColumnInfo
    var isStarred: Boolean = false,
    @ColumnInfo
    var isReadLater: Boolean = false,
    @ColumnInfo
    var updateAt: Date? = null,
    @ColumnInfo
    var readingAt: Date? = null
) {

    @Ignore
    var dateString: String? = null

    @Ignore
    val key = id + link + isStarred + isUnread + readingAt + hashCode()

    companion object {

        val Mock = Article(
            id = "",
            date = Date(),
            title = "",
            rawDescription = "",
            shortDescription = "",
            link = "",
            feedId = "",
            accountId = 1
        )
    }
}


data class ArticleStatus(
    val id: String,
    val isUnread: Boolean = false,
    val isStarred: Boolean = false
)