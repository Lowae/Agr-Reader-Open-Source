package com.lowae.agrreader.data.module

import android.content.Context
import com.lowae.agrreader.data.dao.AccountDao
import com.lowae.agrreader.data.dao.ArticleDao
import com.lowae.agrreader.data.dao.ArticleHistoryDao
import com.lowae.agrreader.data.dao.FeedDao
import com.lowae.agrreader.data.dao.GroupDao
import com.lowae.agrreader.data.source.RYDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides Data Access Objects for database.
 *
 * - [ArticleDao]
 * - [FeedDao]
 * - [GroupDao]
 * - [AccountDao]
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideArticleDao(ryDatabase: RYDatabase): ArticleDao = ryDatabase.articleDao()

    @Provides
    @Singleton
    fun provideArticleHistoryDao(ryDatabase: RYDatabase): ArticleHistoryDao = ryDatabase.articleHistoryDao()


    @Provides
    @Singleton
    fun provideFeedDao(ryDatabase: RYDatabase): FeedDao = ryDatabase.feedDao()

    @Provides
    @Singleton
    fun provideGroupDao(ryDatabase: RYDatabase): GroupDao = ryDatabase.groupDao()

    @Provides
    @Singleton
    fun provideAccountDao(ryDatabase: RYDatabase): AccountDao = ryDatabase.accountDao()

    @Provides
    @Singleton
    fun provideReaderDatabase(@ApplicationContext context: Context): RYDatabase =
        RYDatabase.getInstance(context)
}
