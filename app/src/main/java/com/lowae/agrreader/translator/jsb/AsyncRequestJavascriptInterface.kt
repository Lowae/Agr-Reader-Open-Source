package com.lowae.agrreader.translator.jsb

import android.webkit.JavascriptInterface
import android.webkit.WebView

class AsyncRequestJavascriptInterface(private val delegate: Delegate) {
    @JavascriptInterface
    fun request(workId: String, params: String, successJsCallback: String, failJsCallback: String) {
        return delegate.request(workId, params, successJsCallback, failJsCallback)
    }

    companion object {
        private const val JAVASCRIPT_INTERFACE_NAME = "AndroidAsyncRequest"

        fun injectAsyncRequestJsb(webView: WebView, asyncRequest: AsyncRequest) {
            webView.addJavascriptInterface(
                createAsyncRequestJavascriptInterface(webView, asyncRequest),
                JAVASCRIPT_INTERFACE_NAME
            )
        }

        private fun createAsyncRequestJavascriptInterface(
            webView: WebView,
            asyncRequest: AsyncRequest
        ) =
            AsyncRequestJavascriptInterface { workId, params, successJsCallback, failJsCallback ->
                val successCallback = AsyncRequest.Callback {
                    webView.evaluateJavascript("$successJsCallback('$it')", null)
                }
                val failCallback = AsyncRequest.Callback {
                    webView.evaluateJavascript("$failJsCallback('$it')", null)
                }
                webView.post {
                    asyncRequest.request(workId, params, successCallback, failCallback)
                }
            }

    }

    fun interface Delegate {
        fun request(
            workId: String,
            params: String,
            successJsCallback: String,
            failJsCallback: String
        )
    }
}