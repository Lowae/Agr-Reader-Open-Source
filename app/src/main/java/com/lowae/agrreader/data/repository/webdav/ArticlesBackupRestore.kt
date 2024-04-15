package com.lowae.agrreader.data.repository.webdav

import androidx.annotation.Keep
import com.google.gson.reflect.TypeToken
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.data.dao.ArticleDao
import com.lowae.agrreader.data.dao.FeedDao
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.repository.RssHelper
import com.lowae.agrreader.utils.GsonUtils
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.ArticleIdGenerator
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Date

class ArticlesBackupRestore(
    private val accountId: Int,
    private val articleDao: ArticleDao,
    private val feedDao: FeedDao,
    private val rssHelper: RssHelper
) : WebDavBackupRestore {

    override val file: File =
        File("${AgrReaderApp.application.cacheDir}/backup/AgrReader-articles.json")

    override suspend fun backup() {
        val stars =
            articleDao.queryArticleWithFeedWhenIsStarredV2(accountId, true).map { (article, feed) ->
                BackupArticle(
                    date = article.date.time,
                    title = article.title,
                    author = article.author.orEmpty(),
                    img = article.img.orEmpty(),
                    link = article.link,
                    rawDescription = article.rawDescription,
                    feedLink = feed.url
                )
            }
        FileWriter(file).use {
            GsonUtils.gson.toJson(stars, it)
        }
    }

    override suspend fun restore() {
        RLog.d("WebDavBackupRestore", "ArticlesBackupRestore restore")
        val stars = GsonUtils.gson.fromJson<List<BackupArticle>>(
            FileReader(file),
            object : TypeToken<List<BackupArticle>>() {}.type
        )
        articleDao.insertList(stars.mapNotNull { backup ->
            val feedId = feedDao.queryByLink(accountId, backup.feedLink).firstOrNull()?.id
                ?: return@mapNotNull null
            return@mapNotNull Article(
                id = ArticleIdGenerator.id(),
                date = Date(backup.date),
                title = backup.title,
                author = backup.title,
                rawDescription = backup.rawDescription,
                shortDescription = rssHelper.parseArticleShortDescription(backup.rawDescription),
                img = backup.img,
                link = backup.link,
                feedId = feedId,
                accountId = accountId,
                isUnread = false,
                isStarred = true,
                updateAt = Date(),
            )
        })
        RLog.d("WebDavBackupRestore", "ArticlesBackupRestore end")
    }

    @Keep
    data class BackupArticle(
        val date: Long,
        val title: String,
        val author: String,
        val img: String,
        val link: String,
        val rawDescription: String,
        val feedLink: String
    )
}