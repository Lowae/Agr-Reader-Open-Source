package com.lowae.agrreader.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.model.group.GroupWithFeed
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Query(
        """
        SELECT * FROM `group`
        WHERE id = :id
        """
    )
    suspend fun queryById(id: String): Group?

    @Transaction
    @Query(
        """
        SELECT * FROM `group`
        WHERE accountId = :accountId
        """
    )
    fun queryAllGroupWithFeedAsFlow(accountId: Int): Flow<MutableList<GroupWithFeed>>

    @Transaction
    @Query(
        """
        SELECT * FROM `group`
        WHERE accountId = :accountId
        """
    )
    suspend fun queryAllGroupWithFeed(accountId: Int): List<GroupWithFeed>

    @Query(
        """
        SELECT * FROM `group`
        WHERE accountId = :accountId
        """
    )
    fun queryAllGroup(accountId: Int): Flow<MutableList<Group>>

    @Query(
        """
        DELETE FROM `group`
        WHERE accountId = :accountId
        """
    )
    suspend fun deleteByAccountId(accountId: Int)

    @Query(
        """
        DELETE FROM `group`
        WHERE id = :groupId
        """
    )
    suspend fun deleteById(groupId: String)

    @Query(
        """
        SELECT * FROM `group`
        WHERE accountId = :accountId
        """
    )
    suspend fun queryAll(accountId: Int): List<Group>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg group: Group)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(groups: List<Group>): List<Long>

    @Update
    suspend fun update(vararg group: Group)

    @Update
    suspend fun update(groups: List<Group>)

    @Delete
    suspend fun delete(vararg group: Group)

    @Transaction
    suspend fun insertOrUpdate(groups: List<Group>) {
        val needUpdates = mutableListOf<Group>()
        insertIgnore(groups).forEachIndexed { index, result ->
            if (result == -1L) {
                needUpdates.add(groups[index])
            }
        }
        update(needUpdates)
    }

}
