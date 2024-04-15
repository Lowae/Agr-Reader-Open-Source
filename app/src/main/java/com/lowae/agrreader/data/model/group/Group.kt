package com.lowae.agrreader.data.model.group

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.lowae.agrreader.data.model.feed.StatusCount
import com.lowae.agrreader.data.source.RYDatabase
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * TODO: Add class description
 */
@Parcelize
@Entity(tableName = RYDatabase.TABLE_NAME_GROUP)
data class Group(
    @PrimaryKey
    var id: String,
    @ColumnInfo
    var name: String,
    @ColumnInfo(index = true)
    var accountId: Int,
    @ColumnInfo
    var filter: Boolean = true,
    @ColumnInfo(defaultValue = "0")
    var priority: Int = 0,
) : Parcelable {

    @IgnoredOnParcel
    @Ignore
    var count: StatusCount = StatusCount.DEFAULT

    @IgnoredOnParcel
    @Ignore
    val key = id + name + accountId
}
