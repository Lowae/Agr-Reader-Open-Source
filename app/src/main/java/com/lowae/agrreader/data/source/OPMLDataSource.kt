package com.lowae.agrreader.data.source

import android.content.Context
import be.ceau.opml.OpmlParser
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.model.group.GroupWithFeed
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.repository.OpmlRepository
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.FeedIdGenerator
import com.lowae.agrreader.utils.ext.GroupIdGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.*
import javax.inject.Inject

class OPMLDataSource @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
) {

    @Throws(Exception::class)
    suspend fun parseFileInputStream2(
        inputStream: InputStream,
        allGroups: List<Group>,
        defaultGroup: Group,
    ): List<GroupWithFeed> {
        return withContext(ioDispatcher) {
            val accountId = CurrentAccountId
            val opml = OpmlParser().parse(inputStream)
            val groupFeedsMap: HashMap<Group, MutableList<Feed>> =
                hashMapOf<Group, MutableList<Feed>>()
            groupFeedsMap[defaultGroup] = mutableListOf()

            opml.body.outlines.forEach { out ->
                if (out.subElements.isNullOrEmpty()) {
                    if (out.attributes["xmlUrl"] == null) {
                        // 说明是Group
                        val groupName = out.attributes["title"] ?: out.text ?: return@forEach
                        if (groupName == defaultGroup.name) {
                            return@forEach
                        } else {
                            val newGroup = Group(
                                id = GroupIdGenerator.id(),
                                name = groupName,
                                accountId = accountId,
                            )
                            groupFeedsMap[newGroup] = mutableListOf()
                        }
                    } else {
                        // 说明是Feed
                        val rssTitle =
                            out.attributes["title"] ?: out.text
                            ?: context.getString(R.string.unknown)
                        val rssUrl = out.attributes["xmlUrl"] ?: return@forEach
                        val newFeed = Feed(
                            id = FeedIdGenerator.id(rssUrl),
                            name = rssTitle,
                            url = rssUrl,
                            groupId = defaultGroup.id,
                            accountId = accountId,
                            isNotification = out.attributes[OpmlRepository.EXTRA_NOTIFICATION].toBoolean(),
                            sourceType = out.attributes[OpmlRepository.EXTRA_SOURCE_TYPE]?.toIntOrNull()
                                ?: Feed.SOURCE_TYPE_FULL_CONTENT,
                        )
                        groupFeedsMap.getOrPut(defaultGroup) { mutableListOf() }.add(newFeed)
                    }
                } else {
                    val groupName = out.attributes["title"] ?: out.text ?: return@forEach
                    val foundGroup = allGroups.find { it.name == groupName }
                    val group = if (foundGroup != null) {
                        if (groupFeedsMap.containsKey(foundGroup).not()) {
                            groupFeedsMap[foundGroup] = mutableListOf()
                        }
                        foundGroup
                    } else {
                        val newGroup = Group(
                            id = GroupIdGenerator.id(),
                            name = groupName,
                            accountId = accountId,
                        )
                        groupFeedsMap[newGroup] = mutableListOf()
                        newGroup
                    }
                    out.subElements.forEach feeds@{ feedOutline ->
                        val rssTitle = feedOutline.attributes["title"] ?: feedOutline.text
                        ?: context.getString(R.string.unknown)
                        val rssUrl = feedOutline.attributes["xmlUrl"] ?: return@feeds
                        val newFeed = Feed(
                            id = FeedIdGenerator.id(rssUrl),
                            name = rssTitle,
                            url = rssUrl,
                            groupId = group.id,
                            accountId = accountId,
                            isNotification = out.attributes[OpmlRepository.EXTRA_NOTIFICATION].toBoolean(),
                            sourceType = out.attributes[OpmlRepository.EXTRA_SOURCE_TYPE]?.toIntOrNull()
                                ?: Feed.SOURCE_TYPE_FULL_CONTENT,
                        )
                        groupFeedsMap.getOrPut(group) { mutableListOf() }.add(newFeed)
                    }
                }
            }
            groupFeedsMap.map { GroupWithFeed(it.key, it.value) }
        }
    }
}
