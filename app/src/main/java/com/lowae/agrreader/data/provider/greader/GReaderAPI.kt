package com.lowae.agrreader.data.provider.greader

import com.lowae.agrreader.data.model.account.security.GoogleReaderSecurityKey
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.model.entities.GReader
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.model.group.GroupWithFeed
import com.lowae.agrreader.data.module.USER_AGENT_STRING
import com.lowae.agrreader.data.provider.ArticleStatusIds
import com.lowae.agrreader.data.provider.BaseAPI2
import com.lowae.agrreader.data.provider.RssServiceException
import com.lowae.agrreader.data.repository.RssHelper
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.GroupIdGenerator
import com.lowae.agrreader.utils.ext.map
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import okhttp3.Response
import okhttp3.executeAsync
import org.json.JSONObject
import java.util.regex.Pattern


class GReaderAPI(
    private val rssHelper: RssHelper,
    var securityKey: GoogleReaderSecurityKey,
) : BaseAPI2() {


    companion object {

        const val TAG = "GReaderAPI"

        private const val MAX_FETCH_COUNT = 1000

        private const val _ALL_TAG = "user/-/state/com.google/reading-list"
        private const val _READ_TAG = "user/-/state/com.google/read"
        private const val _STAR_TAG = "user/-/state/com.google/starred"

        fun String.ofItemIdToHexId() = String.format("%016x", toLong())
    }

    private val serverUrl: String
        get() = securityKey.serverUrl.orEmpty()
    private val username: String
        get() = securityKey.username.orEmpty()
    private val password: String
        get() = securityKey.password.orEmpty()

    private var auth: String = ""
    private var token: String = ""

    private val authPattern = Pattern.compile("Auth=(.*)")

    private suspend inline fun fetchApi(
        path: String,
        query: List<Pair<String, String>>? = null,
        body: List<Pair<String, String>>? = null,
    ): Response {
        val url = "$serverUrl$path".toHttpUrl().newBuilder().let { builder ->
            query?.forEach {
                builder.addQueryParameter(it.first, it.second)
            }
            builder.setQueryParameter("output", "json")
        }.build()
        val formBody = body?.let { pairs ->
            FormBody.Builder().apply {
                pairs.forEach { (key, value) ->
                    add(key, value)
                }
                add("T", token)
            }.build()
        }
        val response = client.newCall(
            Request.Builder()
                .url(url)
                .apply {
                    if (auth.isNotEmpty()) {
                        addHeader("Authorization", "GoogleLogin auth=$auth")
                    }
                    if (formBody == null) {
                        get()
                    } else {
                        addHeader("Content-Type", "application/x-www-form-urlencoded")
                        addHeader("User-Agent", USER_AGENT_STRING)
                        post(formBody)
                    }
                }
                .build()
        ).executeAsync()

        when (response.code) {
            401 -> throw RssServiceException(
                "Client operation unauthorized",
                RssServiceException.ERR_UNAUTHORIZED
            )

            !in 200..299 -> throw RssServiceException(
                "Client operation forbidden", RssServiceException.ERR_FORBIDDEN
            )
        }

        return response
    }

    private suspend fun fetchPagination(path: String): MutableList<String> {
        val totals = mutableListOf<String>()
        var continuation = ""
        do {
            val requestPath = if (continuation.isEmpty()) path else "$path&c=$continuation"
            val responseStr = fetchApi(requestPath).body.string()
            val json = JSONObject(responseStr)
            val itemRefs = json.optJSONArray("itemRefs")?.map {
                it.optString("id")
            }.orEmpty()
            totals.addAll(itemRefs)
            continuation = json.optString("continuation")
        } while (continuation.isNotEmpty() && totals.size < MAX_FETCH_COUNT)
        return totals
    }

    suspend fun validCredentials() = try {
        val response = fetchApi("/reader/api/0/user-info")
        RLog.d(TAG, "validCredentials")
        response.code == 200
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    suspend fun reAuthenticate() {
        if (validCredentials()) return
        try {
            val bodyString = fetchApi(
                "/accounts/ClientLogin",
                body = listOf(
                    "output" to "json",
                    "Email" to username,
                    "Passwd" to password,
                    "client" to "AgrReader"
                )
            ).body.string()
            try {
                val json = JSONObject(bodyString)
                auth = json.getString("Auth")
            } catch (e: Exception) {
                RLog.w(TAG, "reAuthenticate output json: ${e.message}")
            }
            if (auth.isEmpty()) {
                val authMatcher = authPattern.matcher(bodyString)
                while (authMatcher.find()) {
                    // 获取匹配到的Auth后的内容
                    auth = authMatcher.group(1).orEmpty()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        token = fetchApi("/reader/api/0/token").body.string()
        RLog.d(TAG, "auth: $auth, token: $token")
    }

    suspend fun fetchGroupWithFeeds(): List<GroupWithFeed> {
        val accountId = CurrentAccountId
        val gReaderFeeds = fetchSubscriptions()

        val groupIdFeedsMap = gReaderFeeds.map { gFeed ->
            gFeed.category to Feed(
                id = gFeed.id,
                name = gFeed.title,
                url = gFeed.url,
                siteUrl = gFeed.htmlUrl,
                icon = gFeed.iconUrl,
                groupId = gFeed.category.id.ifEmpty { GroupIdGenerator.DEFAULT_ID },
                accountId = accountId,
            )
        }.groupBy { it.first }
        return groupIdFeedsMap.map { (category, feeds) ->
            GroupWithFeed(
                group = Group(
                    id = category.id,
                    name = category.label,
                    accountId = accountId,
                ),
                feeds = feeds.map { it.second }.toMutableList()
            )
        }
    }


    private suspend fun fetchSubscriptions(): List<GReader.Feeds> {
        val responseStr = fetchApi("/reader/api/0/subscription/list").body.string()
        return gson.fromJson(responseStr, GReader.FeedsResponse::class.java).subscriptions
    }

    suspend fun syncArticleStatus(): ArticleStatusIds {
        val unreadIds =
            fetchPagination("/reader/api/0/stream/items/ids?s=$_ALL_TAG&xt=$_READ_TAG&n=$MAX_FETCH_COUNT")
        val starredIds =
            fetchPagination("/reader/api/0/stream/items/ids?s=$_STAR_TAG&n=$MAX_FETCH_COUNT")
        return ArticleStatusIds(unreadIds.toMutableSet(), starredIds.toMutableSet())
    }

    suspend fun fetchArticles(articleIds: List<String>): List<Article> {
        val accountId = CurrentAccountId
        val articles = buildList(articleIds.size) {
            articleIds.chunked(100).forEach { page ->
                val items = toDTO<GReader.Articles>(
                    fetchApi(
                        "/reader/api/0/stream/items/contents",
                        body = page.map { "i" to it.ofItemIdToHexId() }
                    ).body.string()
                ).items
                items.mapTo(this) { it.convertToArticle(accountId, rssHelper) }
            }
        }
        return articles
    }

    @Deprecated("Some RSS service not supported!")
    suspend fun markAllAsRead(feedIds: List<String>) {
        feedIds.forEach {
            fetchApi("/reader/api/0/mark-all-as-read", body = listOf("s" to it))
        }
    }

    suspend fun markAsRead(articleId: String, isUnread: Boolean) {
        editTag(articleId, _READ_TAG, isUnread.not())
    }

    suspend fun markAsRead(articleIds: List<String>, isUnread: Boolean) {
        editTag(articleIds, _READ_TAG, isUnread.not())
    }

    suspend fun markAsStarred(articleId: String, isStarred: Boolean) {
        editTag(articleId, _STAR_TAG, isStarred)
    }

    suspend fun addGroup(groupName: String): String? {
        val groupId = "user/-/label/$groupName"
        val response =
            fetchApi("/reader/api/0/edit-tag", body = listOf("ac" to "edit", "a" to groupId))
        return if (response.code == 200) groupId else null
    }

    suspend fun addFeed(feedUrl: String) {
        fetchApi("/reader/api/0/subscription/quickadd", body = listOf("quickadd" to feedUrl))
    }

    suspend fun deleteFeed(feed: Feed) {
        fetchApi(
            "/reader/api/0/subscription/edit",
            body = listOf("ac" to "unsubscribe", "s" to feed.id)
        )
    }

    suspend fun updateFeed(feed: Feed) {
        fetchApi(
            "/reader/api/0/subscription/edit",
            body = listOf("ac" to "edit", "s" to feed.id, "t" to feed.name)
        )
    }

    private suspend fun editTag(articleId: String, tag: String, isAdd: Boolean = true) {
        fetchApi("/reader/api/0/edit-tag", body = buildList {
            add("i" to articleId.ofItemIdToHexId())
            if (isAdd) {
                add("a" to tag)
            } else {
                add("r" to tag)
            }
        })
    }

    private suspend fun editTag(articleIds: List<String>, tag: String, isAdd: Boolean = true) {
        articleIds.chunked(1000).forEach { chunk ->
            fetchApi("/reader/api/0/edit-tag", body = buildList {
                this.addAll(chunk.map { "i" to it.ofItemIdToHexId() })
                if (isAdd) {
                    add("a" to tag)
                } else {
                    add("r" to tag)
                }
            })
        }
    }

}