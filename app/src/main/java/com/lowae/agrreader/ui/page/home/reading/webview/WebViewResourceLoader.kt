package com.lowae.agrreader.ui.page.home.reading.webview

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import androidx.annotation.WorkerThread
import com.lowae.agrreader.data.model.preference.BasicFontsPreference
import com.lowae.agrreader.ui.page.home.reading.ReadingWebConfiguration
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.ExternalFonts
import okhttp3.OkHttpClient
import java.util.concurrent.ConcurrentHashMap

@WorkerThread
class WebViewResourceLoader(
    val okHttpClient: OkHttpClient
) {

    companion object {
        private const val TAG = "WebViewResourceLoader"
    }


    var interceptionResource = false
    private var webConfiguration = ReadingWebConfiguration()
    private val runningMaps = ConcurrentHashMap<Uri, FetchWebResourceResponse>()

    fun shouldInterceptRequest(
        context: Context,
        baseUrl: String,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return if (interceptionResource) {
            RLog.d(
                TAG,
                "shouldInterceptRequest: ${Thread.currentThread()}, ${okHttpClient.dispatcher.runningCallsCount()}"
            )
            FetchWebResourceResponse(okHttpClient).interceptRequest(baseUrl, request)
                .also { response ->
                    runningMaps[request.url] = response
                }
        } else {
            null
        }
    }

    fun cancelAll() {
        runningMaps.forEach {
            it.value.cancel()
        }
        runningMaps.clear()
    }

    fun updateWebConfig(webConfig: ReadingWebConfiguration) {
        this.webConfiguration = webConfig
    }

}