package com.lowae.agrreader.data.source

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.lowae.agrreader.data.dao.AccountDao
import com.lowae.agrreader.data.dao.ArticleDao
import com.lowae.agrreader.data.dao.ArticleHistoryDao
import com.lowae.agrreader.data.dao.FeedDao
import com.lowae.agrreader.data.dao.GroupDao
import com.lowae.agrreader.data.model.account.Account
import com.lowae.agrreader.data.model.account.AccountTypeConverters
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.model.article.ArticleHistory
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.group.Group
import java.util.Date

@Database(
    entities = [Account::class, Feed::class, Article::class, Group::class, ArticleHistory::class],
    version = 5,
    autoMigrations = [
        AutoMigration(1, 2, RYDatabase.AutoMigrationSpec1_2::class),
        AutoMigration(2, 3, RYDatabase.AutoMigrationSpec2_3::class),
        AutoMigration(3, 4),
        AutoMigration(4, 5),
    ]
)
@TypeConverters(
    RYDatabase.DateConverters::class,
    AccountTypeConverters::class,
    RYDatabase.PairConverters::class
)
abstract class RYDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun feedDao(): FeedDao
    abstract fun articleDao(): ArticleDao
    abstract fun articleHistoryDao(): ArticleHistoryDao
    abstract fun groupDao(): GroupDao

    companion object {

        const val TABLE_NAME_ACCOUNT = "account"
        const val TABLE_NAME_GROUP = "group"
        const val TABLE_NAME_FEED = "feed"
        const val TABLE_NAME_ARTICLE = "article"
        const val TABLE_NAME_ARTICLE_HISTORY = "article_history"

        val ALL_TABLE_NAMES = arrayOf(
            TABLE_NAME_ACCOUNT,
            TABLE_NAME_GROUP,
            TABLE_NAME_FEED,
            TABLE_NAME_ARTICLE,
            TABLE_NAME_ARTICLE_HISTORY
        )

        @Volatile
        private var instance: RYDatabase? = null

        fun getInstance(context: Context): RYDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    RYDatabase::class.java,
                    "Agr_Reader"
                )
                    .build()
                    .also {
                        instance = it
                    }
            }
        }
    }

    class DateConverters {

        @TypeConverter
        fun toDate(dateLong: Long?): Date? {
            return dateLong?.let { Date(it) }
        }

        @TypeConverter
        fun fromDate(date: Date?): Long? {
            return date?.time
        }
    }

    class PairConverters {

        companion object {
            private const val SEPARATOR = "$"
        }

        @TypeConverter
        fun fromPairString(pairString: String?): Pair<String, String>? {
            val (first, last) = pairString?.split(SEPARATOR) ?: return null
            return Pair(first, last)
        }

        @TypeConverter
        fun toPairString(pair: Pair<String, String>?): String? {
            return pair?.let {
                "${it.first}$SEPARATOR${it.second}"
            }
        }
    }

    @DeleteColumn.Entries(
        DeleteColumn(TABLE_NAME_ACCOUNT, "syncInterval"),
        DeleteColumn(TABLE_NAME_ACCOUNT, "syncOnStart"),
        DeleteColumn(TABLE_NAME_ACCOUNT, "syncOnlyOnWiFi"),
        DeleteColumn(TABLE_NAME_ACCOUNT, "syncOnlyWhenCharging"),
        DeleteColumn(TABLE_NAME_ACCOUNT, "keepArchived"),
        DeleteColumn(TABLE_NAME_ACCOUNT, "syncBlockList")
    )
    class AutoMigrationSpec1_2 : AutoMigrationSpec

    @RenameColumn(TABLE_NAME_FEED, "isFullContent", "sourceType")
    class AutoMigrationSpec2_3 : AutoMigrationSpec
}