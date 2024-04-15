package com.lowae.agrreader.data.model.feed

import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.source.RYDatabase
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * TODO: Add class description
 */
@Parcelize
@Entity(
    tableName = RYDatabase.TABLE_NAME_FEED,
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = ["id"],
        childColumns = ["groupId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    )],
)
data class Feed(
    @PrimaryKey
    var id: String,
    @ColumnInfo
    var name: String,
    @ColumnInfo
    var icon: String? = null,
    @ColumnInfo
    var url: String,
    @ColumnInfo
    var siteUrl: String? = null,
    @ColumnInfo
    var description: String? = null,
    @ColumnInfo(index = true)
    var groupId: String,
    @ColumnInfo(index = true)
    var accountId: Int,
    @ColumnInfo
    var isNotification: Boolean = false,
    @ColumnInfo(defaultValue = SOURCE_TYPE_FULL_CONTENT.toString())
    var sourceType: Int = SOURCE_TYPE_FULL_CONTENT,
    @ColumnInfo(defaultValue = "0")
    var interceptionResource: Boolean = false,
    @ColumnInfo
    var translationLanguage: Pair<String, String>? = null
) : Parcelable {

    companion object {
        const val SOURCE_TYPE_WEB = 0
        const val SOURCE_TYPE_FULL_CONTENT = 1
        const val SOURCE_TYPE_RAW_DESCRIPTION = 2

        fun sourceTypeStr(sourceType: Int) =
            when (sourceType) {
                SOURCE_TYPE_WEB -> "内嵌网页"
                SOURCE_TYPE_RAW_DESCRIPTION -> "内容摘要"
                else -> "全文解析"
            }

        val Mock = Feed(
            id = "",
            name = "",
            url = "",
            groupId = "",
            accountId = 1,
        )
    }

    @IgnoredOnParcel
    @Ignore
    var count: StatusCount = StatusCount.DEFAULT

    @IgnoredOnParcel
    @Ignore
    val key = id + name + url + accountId
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Feed

        if (id != other.id) return false
        if (name != other.name) return false
        if (icon != other.icon) return false
        if (url != other.url) return false
        if (siteUrl != other.siteUrl) return false
        if (description != other.description) return false
        if (groupId != other.groupId) return false
        if (accountId != other.accountId) return false
        if (isNotification != other.isNotification) return false
        if (sourceType != other.sourceType) return false
        if (interceptionResource != other.interceptionResource) return false
        if (count != other.count) return false
        if (translationLanguage != other.translationLanguage) return false
        return key == other.key
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + url.hashCode()
        result = 31 * result + (siteUrl?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + groupId.hashCode()
        result = 31 * result + accountId
        result = 31 * result + isNotification.hashCode()
        result = 31 * result + sourceType
        result = 31 * result + interceptionResource.hashCode()
        result = 31 * result + count.hashCode()
        result = 31 * result + key.hashCode()
        result = 31 * result + translationLanguage.hashCode()
        return result
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@IntDef(Feed.SOURCE_TYPE_WEB, Feed.SOURCE_TYPE_FULL_CONTENT, Feed.SOURCE_TYPE_RAW_DESCRIPTION)
annotation class FeedSourceType
