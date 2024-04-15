package com.lowae.agrreader.data.repository

import android.text.Html
import android.util.Log
import com.lowae.agrreader.data.model.article.Article
import com.lowae.agrreader.data.model.entities.Favicon
import com.lowae.agrreader.data.model.feed.Feed
import com.lowae.agrreader.data.module.IODispatcher
import com.lowae.agrreader.data.module.NoCachedOkHttpClient
import com.lowae.agrreader.data.module.USER_AGENT_STRING
import com.lowae.agrreader.translator.TranslateRequest
import com.lowae.agrreader.translator.request.TranslatorApi
import com.lowae.agrreader.utils.GsonUtils
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.ArticleIdGenerator
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.FeedIdGenerator
import com.lowae.agrreader.utils.ext.isUrl
import com.lowae.agrreader.utils.relativeLinkIntoAbsolute
import com.lowae.agrreader.utils.safeSubList
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.dankito.readability4j.extended.Readability4JExtended
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.executeAsync
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.InputStream
import java.net.URL
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Some operations on RSS.
 */
@Singleton
class RssHelper @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @NoCachedOkHttpClient
    private val okHttpClient: OkHttpClient,
) {

    companion object {
        private const val TAG = "RssHelper"
        private const val MAX_SOURCE_HTML_LENGTH = 1 * 1024 * 1024
    }

    private val htmlRequestSession =
        Jsoup.newSession().timeout(10 * 1000).userAgent(USER_AGENT_STRING).followRedirects(true)

    private val SyndFeed.siteUrl: String?
        get() {
            return this.links?.firstOrNull {
                "alternate" == it.rel && "text/html" == it.type
            }?.href ?: this.link
        }

    suspend fun searchRssFeedFromUrl(url: String): Feed {
        return withContext(ioDispatcher) {
            val accountId = CurrentAccountId
            val syndFeed =
                SyndFeedInput().build(XmlReader(inputStream(okHttpClient, url)))
            val siteUrl = syndFeed.siteUrl ?: url
            val icon = tryGetFavicon(syndFeed).orEmpty()
            Feed(
                id = FeedIdGenerator.id(url),
                name = syndFeed.title!!,
                url = url,
                siteUrl = siteUrl,
                description = syndFeed?.description,
                groupId = "",
                icon = icon,
                accountId = accountId,
                sourceType = Feed.SOURCE_TYPE_FULL_CONTENT
            )
        }
    }

    suspend fun queryRssXml(
        config: FeedSyncPreferenceConfig,
        feed: Feed,
        latestLink: String?,
    ): QueryRssResult {
        val accountId = CurrentAccountId
        val result = QueryRssResult()
        val syndFeed = try {
            SyndFeedInput().apply { isPreserveWireFeed = true }
                .build(XmlReader(inputStream(okHttpClient, feed.url)))
        } catch (e: Exception) {
            e.printStackTrace()
            return result
        }
        val (start, end) = syndFeed.entries.indexOfFirst { it.link == latestLink }
            .let { latestIndex ->
                if (latestIndex == -1) {
                    (syndFeed.entries.size - config.limitCount).coerceAtLeast(0) to syndFeed.entries.size
                } else if (latestIndex < config.limitCount) {
                    0 to latestIndex
                } else {
                    latestIndex - config.limitCount to latestIndex
                }
            }

        result.articles = syndFeed.entries.safeSubList(start, end)
            .mapNotNull { entry -> parseArticle(config, feed, accountId, entry) }
        if (feed.icon == null) {
            result.needUpdateFeed = true
            result.iconUrl = tryGetFavicon(syndFeed).orEmpty()
            RLog.d(TAG, "queryRssXml icon: ${result.iconUrl}")
        }
        return result
    }

    private suspend fun parseArticle(
        config: FeedSyncPreferenceConfig,
        feed: Feed,
        accountId: Int,
        syndEntry: SyndEntry,
    ): Article? {
        return try {
            val title = Html.fromHtml(syndEntry.title).toString()
            val translationTitle = translateArticleTitle(config, feed, title)
            val desc = syndEntry.description?.value
            val link = syndEntry.link ?: ""
            val content = syndEntry.contents
                .takeIf { it.isNotEmpty() }
                ?.let { it.joinToString("\n") { it.value } }
            val rawDescription = (desc ?: content) ?: ""
            var cover: String? = null
            var sourceHtml = content ?: desc
            try {
                val document = optSourceDocument(
                    htmlRequestSession.newRequest().url(link).userAgent(USER_AGENT_STRING).get()
                )
                cover = parseArticleCover(document)
                sourceHtml =
                    document.outputSettings(Document.OutputSettings().prettyPrint(false)).body()
                        .apply {
                            select("style").remove()
                            select("script").remove()
                        }.outerHtml()
                        .takeIf { it.length < MAX_SOURCE_HTML_LENGTH }
                        .orEmpty()
            } catch (e: Exception) {
                RLog.e(TAG, "fetch source html failure: ${e.message}")
            }
            RLog.d("RssHelper", "feed: ${feed.name},title: $title")
            return Article(
                id = ArticleIdGenerator.id(),
                accountId = accountId,
                feedId = feed.id,
                date = syndEntry.publishedDate ?: syndEntry.updatedDate ?: Date(),
                title = title,
                translationTitle = translationTitle,
                author = syndEntry.author,
                rawDescription = rawDescription,
                shortDescription = parseArticleShortDescription(rawDescription),
                sourceHtml = sourceHtml.orEmpty(),
                img = cover,
                link = link,
                updateAt = Date(),
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun translateArticleTitle(
        config: FeedSyncPreferenceConfig,
        feed: Feed,
        title: String
    ): String? {
        if (config.activeCode.isBlank()) {
            return null
        }
        if (feed.translationLanguage == null) {
            return null
        }
        return TranslatorApi.getTranslator(config.translationOption).request(
            TranslateRequest(
                title,
                feed.translationLanguage?.first.orEmpty(),
                feed.translationLanguage?.second.orEmpty()
            )
        )?.result
    }

    fun parseArticleShortDescription(rawDescription: String): String {
        return (Readability4JExtended("", rawDescription)
            .parse().textContent ?: "")
            .take(180)
            .trim()
    }

    fun parseArticleShortDescription(document: Document): String {
        return document.text().take(110).trim()
    }

    fun parseArticleCover(rawDescription: String): String? {
        // From: https://gitlab.com/spacecowboy/Feeder
        // Using negative lookahead to skip data: urls, being inline base64
        // And capturing original quote to use as ending quote
        val regex = """img.*?src=(["'])((?!data).*?)\1""".toRegex(RegexOption.DOT_MATCHES_ALL)
        // Base64 encoded images can be quite large - and crash database cursors
        return regex.find(rawDescription)?.groupValues?.get(2)?.takeIf { !it.startsWith("data:") }
    }

    fun parseArticleCover(document: Document): String? {
        val cover = document.selectFirst("meta[property=\"og:image\"]")?.attr("content")
        return cover.takeIf { it?.isUrl() == true }
    }

    fun parseArticleCover(element: Element): String? {
        val cover = element.select("img").firstOrNull { it.attr("src").isUrl() }?.attr("src")
        return cover.takeIf { it?.isUrl() == true }
    }

    private suspend fun tryGetFavicon(syndFeed: SyndFeed): String? {
        try {
            val syncFeedIcon = syndFeed.image?.url
            if (syncFeedIcon.isNullOrBlank().not()) return syncFeedIcon
            val siteUrl = syndFeed.siteUrl ?: return null
            val siteFaviconUrl = tryGetFaviconByUrl(siteUrl) ?: tryGetFaviconByHtml(siteUrl)
            if (siteFaviconUrl.isNullOrBlank().not()) return siteFaviconUrl
            return null
        } catch (e: Exception) {
            RLog.w(TAG, "tryGetFavicon: ${e.message}")
            return null
        }
    }

    private suspend fun tryGetFaviconByUrl(url: String?): String? {
        if (url.isNullOrEmpty()) return null
        return "$url/favicon.ico".takeIf { favUrl -> checkFaviconIsValid(favUrl) }
    }

    private suspend fun tryGetFaviconByHtml(url: String?): String? {
        if (url.isNullOrEmpty()) return null
        return try {
            val htmlInputStream = response(okHttpClient, url).body.byteStream()
            val doc = Jsoup.parse(htmlInputStream, null, url)
            return (
                    doc.getElementsByAttributeValue("rel", "shortcut icon")
                            + doc.getElementsByAttributeValue("rel", "apple-touch-icon")
                            + doc.getElementsByAttributeValue("rel", "icon")
                    )
                .filter { it.hasAttr("href") }
                .map {
                    relativeLinkIntoAbsolute(
                        base = URL(url),
                        link = it.attr("href"),
                    )
                }.firstOrNull()
        } catch (t: Throwable) {
            Log.e("FeedParser", "Error when fetching feed icon", t)
            null
        }
    }

    private suspend fun inputStream(
        client: OkHttpClient,
        url: String,
    ): InputStream = response(client, url).body.byteStream()

    private suspend fun response(
        client: OkHttpClient,
        url: String,
    ) = client.newCall(Request.Builder().url(url).build()).executeAsync()

    private suspend fun checkFaviconIsValid(iconUrl: String): Boolean {
        return response(okHttpClient, iconUrl).let { response ->
            response.isSuccessful && response.body.contentType()?.type == "image"
        }
    }

    private fun optSourceDocument(document: Document): Document {
        document.select("img[data-original]").forEach {
            val dataOriginal = it.attr("data-original")
            if (dataOriginal.isNotBlank()) {
                it.attr("src", dataOriginal)
            }
        }
        return document
    }
}

class QueryRssResult(
    var needUpdateFeed: Boolean = false,
    var iconUrl: String? = null,
    var articles: List<Article> = emptyList()
)