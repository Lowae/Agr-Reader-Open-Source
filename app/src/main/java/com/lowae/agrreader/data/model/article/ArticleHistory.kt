package com.lowae.agrreader.data.model.article

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lowae.agrreader.data.source.RYDatabase

@Entity(tableName = RYDatabase.TABLE_NAME_ARTICLE_HISTORY)
data class ArticleHistory(
    @PrimaryKey
    var id: String,
    @ColumnInfo
    var accountId: Int,
    @ColumnInfo(index = true)
    var feedId: String,
    @ColumnInfo
    var readingAt: Long
)