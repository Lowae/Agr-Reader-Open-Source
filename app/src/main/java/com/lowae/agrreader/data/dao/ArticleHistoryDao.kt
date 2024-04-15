package com.lowae.agrreader.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lowae.agrreader.data.model.article.ArticleHistory
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleHistoryDao {

    @Query(
        """
        SELECT * FROM article
        WHERE id IN (SELECT id FROM article_history)
        ORDER BY readingAt DESC
        """
    )
    @Transaction
    suspend fun queryAll(): List<ArticleWithFeed>

    @Query(
        """
        SELECT * FROM article
        WHERE id IN (SELECT id FROM article_history)
        ORDER BY readingAt DESC
        """
    )
    @Transaction
    fun queryAllFlow(): Flow<List<ArticleWithFeed>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: ArticleHistory)

    @Query("DELETE FROM article_history WHERE id = (SELECT id FROM article_history ORDER BY readingAt ASC LIMIT 1)")
    fun deleteOldestRecord()

    @Query("SELECT COUNT(id) FROM article_history")
    fun countHistory(): Int

    @Transaction
    suspend fun insertWithDropOldest(entity: ArticleHistory) {
        if (countHistory() > 50) {
            deleteOldestRecord()
        }
        insert(entity)
    }

}