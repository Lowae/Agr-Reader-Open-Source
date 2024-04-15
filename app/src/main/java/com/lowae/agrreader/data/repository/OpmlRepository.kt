package com.lowae.agrreader.data.repository

import be.ceau.opml.OpmlWriter
import be.ceau.opml.entity.Body
import be.ceau.opml.entity.Head
import be.ceau.opml.entity.Opml
import be.ceau.opml.entity.Outline
import com.lowae.agrreader.data.dao.AccountDao
import com.lowae.agrreader.data.dao.GroupDao
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.module.ApplicationScope
import com.lowae.agrreader.data.source.OPMLDataSource
import com.lowae.agrreader.utils.NoOp1
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.GroupIdGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supports import and export from OPML files.
 */
@Singleton
class OpmlRepository @Inject constructor(
    @ApplicationScope
    private val scope: CoroutineScope,
    private val groupDao: GroupDao,
    private val accountDao: AccountDao,
    private val rssRepository: RssRepository,
    private val OPMLDataSource: OPMLDataSource,
) {

    companion object {
        const val EXTRA_SOURCE_TYPE = "sourceType"
        const val EXTRA_NOTIFICATION = "notification"
    }

    /**
     * Imports OPML file.
     *
     * @param [inputStream] input stream of OPML file
     */
    @Throws(Exception::class)
    private suspend fun parserFeedFromFile(inputStream: InputStream): List<Feed> {
        val defaultGroup =
            groupDao.queryById(getDefaultGroupId()) ?: return emptyList()
        val allGroups = groupDao.queryAll(CurrentAccountId)
        val groupWithFeedList =
            OPMLDataSource.parseFileInputStream2(inputStream, allGroups, defaultGroup)
        val needInsertFeeds = mutableListOf<Feed>()
        val needInsertGroups = mutableListOf<Group>()
        groupWithFeedList.forEach { groupWithFeed ->
            if (allGroups.find { it.name == groupWithFeed.group.name } == null) {
                needInsertGroups.add(groupWithFeed.group)
            }
            groupWithFeed.feeds.forEach { feed ->
                feed.groupId = groupWithFeed.group.id
                if (rssRepository.get().isFeedExist(feed.url).not()) {
                    needInsertFeeds.add(feed)
                }
            }
        }
        rssRepository.get().addGroup(needInsertGroups)
        rssRepository.get().addFeed(needInsertFeeds)
        return needInsertFeeds
    }

    fun parserFeedFromFile2(inputStream: InputStream, onFailure: (Throwable) -> Unit = NoOp1): Job {
        return scope.launch {
            try {
                RLog.d("parserFeedFromFile2", "parser feed start")
                val feeds = parserFeedFromFile(inputStream)
                RLog.d("parserFeedFromFile2", "parser feed end: ${feeds.size}")
                rssRepository.get().sync()
            } catch (e: Exception) {
                onFailure(e)
                e.printStackTrace()
            }
        }
    }

    /**
     * Exports OPML file.
     */
    @Throws(Exception::class)
    suspend fun saveToString(accountId: Int): String {
        return OpmlWriter().write(
            Opml(
                "2.0",
                Head(
                    accountDao.queryById(accountId)?.name,
                    Date().toString(), null, null, null,
                    null, null, null, null,
                    null, null, null, null,
                ),
                Body(groupDao.queryAllGroupWithFeed(accountId).map {
                    Outline(
                        mapOf(
                            "text" to it.group.name,
                            "title" to it.group.name,
                        ),
                        it.feeds.map { feed ->
                            Outline(
                                mapOf(
                                    "text" to feed.name,
                                    "title" to feed.name,
                                    "xmlUrl" to feed.url,
                                    "htmlUrl" to feed.url,
                                    EXTRA_NOTIFICATION to feed.isNotification.toString(),
                                    EXTRA_SOURCE_TYPE to feed.sourceType.toString(),
                                ),
                                emptyList()
                            )
                        }
                    )
                })
            )
        )!!
    }

    private fun getDefaultGroupId(): String = GroupIdGenerator.DEFAULT_ID
}