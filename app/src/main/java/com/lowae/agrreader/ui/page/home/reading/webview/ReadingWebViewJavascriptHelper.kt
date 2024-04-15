package com.lowae.agrreader.ui.page.home.reading.webview


class ReadingWebViewJavascriptHelper(private val webView: ReadingWebView) {

    fun injectTranslate() {
        try {
            webView.evaluateJavascript(
                """
                    var s = document.createElement("script");
                    s.type = "text/javascript";
                    s.src = "https://com.loawe.agrreader/translator.js";
                    document.head.appendChild(s)
                """.trimIndent(),
                null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}