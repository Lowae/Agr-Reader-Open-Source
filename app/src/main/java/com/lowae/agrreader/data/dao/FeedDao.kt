package com.lowae.agrreader.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lowae.agrreader.data.model.feed.Feed

@Dao
interface FeedDao {

    @Query(
        """
        UPDATE feed SET groupId = :targetGroupId
        WHERE groupId = :groupId
        AND accountId = :accountId
        """
    )
    suspend fun updateTargetGroupIdByGroupId(
        accountId: Int,
        groupId: String,
        targetGroupId: String,
    )

    @Query(
        """
        UPDATE feed SET sourceType = :sourceType
        WHERE accountId = :accountId
        AND groupId = :groupId
        """
    )
    suspend fun updateIsFullContentByGroupId(
        accountId: Int,
        groupId: String,
        sourceType: Int,
    )

    @Query(
        """
        UPDATE feed SET isNotification = :isNotification
        WHERE accountId = :accountId
        AND groupId = :groupId
        """
    )
    suspend fun updateIsNotificationByGroupId(
        accountId: Int,
        groupId: String,
        isNotification: Boolean,
    )

    @Query(
        """
        UPDATE feed SET interceptionResource = :interceptionResource
        WHERE accountId = :accountId
        AND groupId = :groupId
        """
    )
    suspend fun updateInterceptionResourceByGroupId(
        accountId: Int,
        groupId: String,
        interceptionResource: Boolean,
    )


    @Query(
        """
        DELETE FROM feed
        WHERE groupId = :groupId
        AND accountId = :accountId
        """
    )
    suspend fun deleteByGroupId(accountId: Int, groupId: String)

    @Query(
        """
        SELECT (id) FROM feed
        WHERE accountId = :accountId
        AND groupId = :groupId
        """
    )
    suspend fun queryAllFeedIdsByGroupId(accountId: Int, groupId: String): List<String>

    @Query(
        """
        SELECT * FROM feed
        WHERE groupId = :groupId
        """
    )
    suspend fun queryAllByGroupId(groupId: String): List<Feed>

    @Query(
        """
        DELETE FROM feed
        WHERE accountId = :accountId
        """
    )
    suspend fun deleteByAccountId(accountId: Int)

    @Query(
        """
        SELECT * FROM feed
        WHERE id = :id
        """
    )
    suspend fun queryById(id: String): Feed?

    @Query(
        """
        SELECT * FROM feed
        WHERE accountId = :accountId
        """
    )
    suspend fun queryAll(accountId: Int): List<Feed>

    @Query(
        """
        SELECT * FROM feed
        WHERE accountId = :accountId
        and url = :url
        """
    )
    suspend fun queryByLink(accountId: Int, url: String): List<Feed>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg feed: Feed)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(groups: List<Feed>): List<Long>

    @Update
    suspend fun update(vararg feed: Feed)

    @Update
    suspend fun update(feeds: List<Feed>)

    @Delete
    suspend fun delete(vararg feed: Feed)

    @Transaction
    suspend fun insertOrUpdate(feeds: List<Feed>): List<Feed> {
        val inserted = mutableListOf<Feed>()
        feeds.forEach { remote ->
            val local = queryById(remote.id)
            if (local == null) {
                insert(remote)
                inserted.add(remote)
            } else {
                if (remote.icon.isNullOrEmpty()) {
                    remote.icon = local.icon
                }
                remote.isNotification = local.isNotification
                remote.interceptionResource = local.interceptionResource
                remote.sourceType = local.sourceType
                remote.translationLanguage = local.translationLanguage
                update(remote)
            }
        }
        return inserted
    }
}
