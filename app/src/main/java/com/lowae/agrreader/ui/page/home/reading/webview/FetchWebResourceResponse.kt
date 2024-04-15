package com.lowae.agrreader.ui.page.home.reading.webview

import android.webkit.MimeTypeMap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import androidx.core.net.toUri
import com.lowae.agrreader.utils.RLog
import okhttp3.Call
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.commonClose
import okhttp3.internal.connection.RealCall
import java.io.InputStream
import java.util.concurrent.Callable
import java.util.concurrent.Future

class FetchWebResourceResponse(
    private val okHttpClient: OkHttpClient,
) : WebResourceResponse(null, null, null) {

    companion object {
        private const val TAG = "FetchWebResourceResponse"
    }

    private var requestCall: Call? = null
    private var fetchDateFuture: Future<InputStream>? = null

    fun interceptRequest(baseUrl: String, request: WebResourceRequest): FetchWebResourceResponse {
        val ext = MimeTypeMap.getFileExtensionFromUrl(request.url.toString())
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        RLog.d(
            TAG,
            "shouldInterceptRequest: ${Thread.currentThread()}, ${okHttpClient.dispatcher.runningCallsCount()}"
        )
        this.mimeType = mime
        this.encoding = "UTF-8"
        this.fetchDateFuture = when (request.url.scheme) {
            "file" -> okHttpClient.dispatcher.executorService.submit(Callable {
                requestFromFileScheme(baseUrl, request)
            })

            else -> okHttpClient.dispatcher.executorService.submit(Callable {
                requestFromUrl(baseUrl, request)
            })
        }
        return this
    }

    override fun getData(): InputStream? {
        return try {
            fetchDateFuture?.get()
        } catch (e: Exception) {
            RLog.d(TAG, e.message.toString())
            null
        }
    }

    private fun requestFromFileScheme(
        baseUrl: String,
        request: WebResourceRequest,
    ): InputStream {
        val appendBaseRequestUrl =
            baseUrl.toUri().buildUpon().path(request.url.path).build().toString()
        val onlyFormatRequestUrl = request.url.buildUpon().scheme("https").build().toString()

        val okhttpRequest = newRequest(baseUrl, request)
        val baseUrlResponse = response(okhttpRequest)
        return if (baseUrlResponse.code != 200) {
            baseUrlResponse.commonClose()
            RLog.d(TAG, "requestFromFileScheme: $onlyFormatRequestUrl")
            response(
                okhttpRequest.newBuilder().url(onlyFormatRequestUrl).build()
            ).body.byteStream()
        } else {
            RLog.d(TAG, "requestFromFileScheme: $appendBaseRequestUrl")
            baseUrlResponse.body.byteStream()
        }
    }

    private fun requestFromUrl(
        baseUrl: String,
        request: WebResourceRequest,
    ): InputStream = response(newRequest(baseUrl, request)).body.byteStream()

    private fun newRequest(
        baseUrl: String,
        request: WebResourceRequest,
    ): Request {
        val headers = request.requestHeaders.also { header ->
            if (header.containsKey("Referer").not()) {
                header["Referer"] = baseUrl
            }
        }.toHeaders()

        return Request.Builder()
            .url(request.url.toString())
            .headers(headers)
            .build()
    }

    private fun response(
        request: Request,
    ): Response = okHttpClient.newCall(request).also {
        requestCall = it
    }.execute()

    fun cancel() {
        RLog.d(TAG, "cancel: ${(requestCall as? RealCall)?.originalRequest?.url}")
        requestCall?.cancel()
        fetchDateFuture?.cancel(true)
    }
}