package com.lowae.agrreader.data.provider.fever

import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.entities.FeverDTO
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.provider.ArticleStatusIds
import com.lowae.agrreader.data.provider.BaseAPI2
import com.lowae.agrreader.data.provider.RssServiceException
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.encodeBase64
import com.lowae.agrreader.utils.ext.getString
import com.lowae.agrreader.utils.ext.md5
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.executeAsync
import java.util.concurrent.ConcurrentHashMap

class FeverAPI private constructor(
    private val serverUrl: String,
    private val apiKey: String,
    private val httpUsername: String? = null,
    private val httpPassword: String? = null,
) : BaseAPI2() {

    private suspend inline fun <reified T> postRequest(query: String?): T {
        val request = Request.Builder()
            .apply {
                if (httpUsername != null) {
                    addHeader(
                        "Authorization",
                        "Basic ${"$httpUsername:$httpPassword".encodeBase64()}"
                    )
                }
            }
            .url("$serverUrl?api&${query ?: ""}")
            .post(FormBody.Builder().add("api_key", apiKey).build())
            .build()
        RLog.d("FeverAPI", "request: ${request.url}")
        val response = client.newCall(request).executeAsync()

        when (response.code) {
            401 -> throw RssServiceException(
                "Client operation unauthorized", RssServiceException.ERR_UNAUTHORIZED
            )

            !in 200..299 -> throw RssServiceException(
                "Client operation forbidden", RssServiceException.ERR_FORBIDDEN
            )
        }

        return toDTO(response.body.string())
    }

    private fun checkAuth(authMap: Map<String, Any>): Int = checkAuth(authMap["auth"] as Int?)

    private fun checkAuth(auth: Int?): Int =
        auth?.takeIf { it > 0 } ?: throw Exception("Unauthorized")

    @Throws
    suspend fun validCredentials(): Int = checkAuth(postRequest<FeverDTO.Common>(null).auth)

    suspend fun getApiVersion(): Long =
        postRequest<Map<String, Any>>(null)["api_version"] as Long?
            ?: throw Exception("Unable to get version")

    suspend fun getGroups(): List<Group> {
        val accountId = CurrentAccountId
        val feverGroups =
            postRequest<FeverDTO.Groups>("groups").apply { checkAuth(auth) }.groups.orEmpty()
        return feverGroups.map {
            Group(
                id = it.id.toString(),
                name = it.title ?: getString(R.string.defaults),
                accountId = accountId,
            )
        }
    }

    suspend fun getFeeds(): FeverDTO.Feeds =
        postRequest<FeverDTO.Feeds>("feeds").apply { checkAuth(auth) }

    suspend fun getFavicons(): Map<Int?, FeverDTO.Favicon> {
        return postRequest<FeverDTO.Favicons>("favicons").apply { checkAuth(auth) }.favicons.associateBy { it.id }
    }

    suspend fun getItems(): FeverDTO.Items =
        postRequest<FeverDTO.Items>("items").apply { checkAuth(auth) }

    suspend fun getItemsSince(id: String): FeverDTO.Items =
        postRequest<FeverDTO.Items>("items&since_id=$id").apply { checkAuth(auth) }

    suspend fun getItemsMax(id: String): FeverDTO.Items =
        postRequest<FeverDTO.Items>("items&max_id=$id").apply { checkAuth(auth) }

    suspend fun getItemsWith(ids: List<String>): FeverDTO.Items =
        postRequest<FeverDTO.Items>("items&with_ids=${ids.joinToString(",")}").apply {
            checkAuth(auth)
        }

    suspend fun getLinks(): FeverDTO.Links =
        postRequest<FeverDTO.Links>("links").apply { checkAuth(auth) }

    suspend fun getLinksWith(offset: Long, days: Long, page: Long): FeverDTO.Links =
        postRequest<FeverDTO.Links>("links&offset=$offset&range=$days&page=$page").apply {
            checkAuth(
                auth
            )
        }

    suspend fun syncArticleStatus(): ArticleStatusIds {
        val itemsByUnread =
            postRequest<FeverDTO.ItemsByUnread>("unread_item_ids").apply { checkAuth(auth) }
        val itemsByStarred =
            postRequest<FeverDTO.ItemsByStarred>("saved_item_ids").apply { checkAuth(auth) }

        return ArticleStatusIds(
            itemsByUnread.unread_item_ids.orEmpty().split(",").toMutableList(),
            itemsByStarred.saved_item_ids.orEmpty().split(",").toMutableList()
        )
    }

    suspend fun markItem(status: FeverDTO.StatusEnum, id: String): FeverDTO.Common =
        postRequest<FeverDTO.Common>("mark=item&as=${status.value}&id=$id").apply { checkAuth(auth) }

    suspend fun markItemsRead(ids: List<String>, isUnread: Boolean) {
        ids.forEach { id ->
            markItem(if (isUnread) FeverDTO.StatusEnum.Unread else FeverDTO.StatusEnum.Read, id)
        }
    }

    private suspend fun markFeedOrGroup(
        act: String,
        status: FeverDTO.StatusEnum,
        id: Long,
        before: Long,
    ): FeverDTO.Common =
        postRequest<FeverDTO.Common>("mark=$act&as=${status.value}&id=$id&before=$before")
            .apply { checkAuth(auth) }

    suspend fun markGroup(status: FeverDTO.StatusEnum, id: Long, before: Long) =
        markFeedOrGroup("group", status, id, before)

    suspend fun markFeed(status: FeverDTO.StatusEnum, id: Long, before: Long) =
        markFeedOrGroup("feed", status, id, before)

    companion object {

        private val instances: ConcurrentHashMap<String, FeverAPI> = ConcurrentHashMap()

        fun getInstance(
            serverUrl: String,
            username: String,
            password: String,
            httpUsername: String? = null,
            httpPassword: String? = null,
        ): FeverAPI = "$username:$password".md5().run {
            instances.getOrPut("$serverUrl$this$httpUsername$httpPassword") {
                FeverAPI(serverUrl, this, httpUsername, httpPassword)
            }
        }
    }
}
