package com.lowae.agrreader.ui.page.home.reading.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material3.ColorScheme
import androidx.core.graphics.toColorInt
import androidx.core.view.postDelayed
import com.lowae.agrreader.BuildConfig
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.data.model.entities.ArticleParserResult
import com.lowae.agrreader.data.model.entities.ImageSrcEntity
import com.lowae.agrreader.translator.TranslateAsyncRequest
import com.lowae.agrreader.translator.jsb.AsyncRequestJavascriptInterface
import com.lowae.agrreader.ui.page.home.reading.ReadingConfiguration
import com.lowae.agrreader.ui.page.home.reading.ReadingStylesConfiguration
import com.lowae.agrreader.ui.page.home.reading.ReadingWebConfiguration
import com.lowae.agrreader.utils.GsonUtils
import com.lowae.agrreader.utils.RLog
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("RequiresFeature", "ViewConstructor", "SetJavaScriptEnabled")
class ReadingWebView(
    context: Context,
    private val resourceLoader: WebViewResourceLoader,
) : WebView(context) {

    companion object {
        private const val TAG = "ReadingWebViewContainer"
        private const val PLACEHOLDER_HTML_URL = "file:///android_asset/reader.html"
    }

    private var currentReadingConfiguration = ReadingConfiguration()
    private var isLight = true
    private var isPageFinished = false
    private lateinit var hexColorScheme: HexColorScheme
    private lateinit var articleWithFeed: ArticleWithFeed

    private var webCallback: ReadingWebCallback? = null
    private var translateAsyncRequest: TranslateAsyncRequest? = null
    private val javascriptHelper = ReadingWebViewJavascriptHelper(this)
    private val scope = MainScope()

    init {
        setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        addJavascriptInterface(this, "ReaderJsb")
        settings.apply {
            javaScriptEnabled = true
            isVerticalScrollBarEnabled = true
            domStorageEnabled = true
            //处理http和https混合的问题
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            allowFileAccess = true
            allowContentAccess = true
        }
        webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                RLog.d(TAG, "shouldOverrideUrlLoading: ${request?.url}")
                if (null == request?.url) return super.shouldOverrideUrlLoading(view, request)
                return if (isPageFinished) {
                    webCallback?.onUrlClick(request)
                    true
                } else {
                    super.shouldOverrideUrlLoading(view, request)
                }
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                RLog.d(TAG, "shouldInterceptRequest: ${request?.url}")
                request ?: return null
                return when (request.url) {
                    else -> resourceLoader.shouldInterceptRequest(
                        context, articleWithFeed.article.link, request
                    ) ?: super.shouldInterceptRequest(view, request)
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isPageFinished = false
                RLog.d(TAG, "onPageStarted: $url")
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                RLog.d(TAG, "onPageCommitVisible: $url")
                view?.evaluateJavascript(
                    "document.querySelector('html').attributes.getNamedItem('data-theme').value = '${if (isLight) "light" else "dark"}'",
                    null
                )
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                RLog.d(TAG, "onLoadResource: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                RLog.d(TAG, "onPageFinished: $url")
                isPageFinished = true
                webCallback?.onArticleLoaded()
            }
        }
    }

    val verticalScrollRange: Int
        get() = computeVerticalScrollRange() - computeVerticalScrollExtent()

    override fun destroy() {
        super.destroy()
        scope.cancel()
    }

    @JavascriptInterface
    fun getArticleMeta(): String {
        val jsonMap = buildMap {
            this["title"] = articleWithFeed.article.title
            this["author"] = articleWithFeed.article.author.orEmpty()
            this["date"] = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                .format(articleWithFeed.article.date)
            this["fullContent"] = articleWithFeed.article.fullContent.orEmpty()
            this["link"] = articleWithFeed.article.link
            this["sourceType"] = articleWithFeed.feed.sourceType
            this["sourceHtml"] = articleWithFeed.article.sourceHtml
            this["rawDescription"] = articleWithFeed.article.rawDescription
            this["feedName"] = articleWithFeed.feed.name
        }
        return GsonUtils.toJson(jsonMap).also {
            RLog.d(TAG, "getArticleMeta: $it")
        }
    }

    @JavascriptInterface
    fun onParserResult(content: String) {
        RLog.d(TAG, "onParserResult: $content")
        val parsed = GsonUtils.fromJson<ArticleParserResult>(content)
        if (parsed?.result != ArticleParserResult.RESULT_CODE_SUCCESS || parsed.article.isNullOrEmpty()) {
            RLog.d(TAG, "onParserError: $content")
        } else {
            webCallback?.onArticleParsed(parsed)
        }
        postDelayed(1000) {
            webCallback?.onArticleLoaded()
        }
    }

    @JavascriptInterface
    fun onImageClick(json: String) {
        RLog.d(TAG, "onImageClick: $json")
        post {
            val images = GsonUtils.fromJson<ImageSrcEntity>(json)
            if (images != null) {
                webCallback?.onArticleImageClick(images)
            }
        }
    }

    fun registerCallbacks(callback: ReadingWebCallback) {
        this.webCallback = callback
    }

    fun unRegisterCallbacks() {
        this.webCallback = null
    }

    fun updateStyleConfig(colorScheme: ColorScheme, isLight: Boolean) {
        this.hexColorScheme = HexColorScheme.fromColorScheme(colorScheme)
        this.isLight = isLight
        setBackgroundColor(hexColorScheme.background.toColorInt())
    }

    fun loadArticle(articleWithFeed: ArticleWithFeed, isWeb: Boolean) {
        this.articleWithFeed = articleWithFeed
        resourceLoader.interceptionResource = articleWithFeed.feed.interceptionResource
        stopLoading()
        if (isWeb) {
            loadUrl(this.articleWithFeed.article.link)
        } else {
            loadUrl(PLACEHOLDER_HTML_URL)
        }
        isPageFinished = false
        if (translateAsyncRequest == null) {
            translateAsyncRequest =
                TranslateAsyncRequest(articleWithFeed, scope, resourceLoader.okHttpClient)
            AsyncRequestJavascriptInterface.injectAsyncRequestJsb(this, translateAsyncRequest!!)
        }
    }

    fun updateReadingConfiguration(configuration: ReadingConfiguration) {
        if (currentReadingConfiguration.style != configuration.style) {
            updateReadingStyle(configuration.style)
        }
        if (currentReadingConfiguration.web != configuration.web) {
            updateReadingWebConfig(configuration.web)
        }
        currentReadingConfiguration = configuration
    }

    fun translate() {
        javascriptHelper.injectTranslate()
    }

    private fun updateReadingStyle(readingStyles: ReadingStylesConfiguration) {
        Log.d(TAG, "onUpdate: $readingStyles")
        evaluateJavascript(
            """
            document.body.style.setProperty('--font-size', '${readingStyles.fontSize}px');
            document.body.style.setProperty('--font-weight', '${readingStyles.fontWeight}');
            document.body.style.setProperty('font-size', '${readingStyles.fontSize}px');
            document.body.style.setProperty('font-weight', '${readingStyles.fontWeight}');
            document.body.style.textAlign = '${readingStyles.textAlign}';
            document.body.style.letterSpacing = '${readingStyles.letterSpacing}px';
            document.body.style.lineHeight = ${readingStyles.lineHeight};
        """.trimIndent(), null
        )
    }

    private fun updateReadingWebConfig(webConfig: ReadingWebConfiguration) {
        resourceLoader.updateWebConfig(webConfig)
    }
}