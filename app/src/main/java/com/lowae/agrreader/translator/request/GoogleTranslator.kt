package com.lowae.agrreader.translator.request

import com.lowae.agrreader.translator.TranslateRequest
import com.lowae.agrreader.translator.TranslateResponse
import com.lowae.agrreader.utils.RLog
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import okhttp3.executeAsync
import org.json.JSONArray
import org.jsoup.nodes.Entities

data object GoogleTranslator : TranslatorApi() {

    override val languages: Map<String, String> = super.languages.mapValues { it.key }

    override suspend fun translate(request: TranslateRequest): TranslateResponse {
        val url = "https://clients5.google.com/translate_a/t".toHttpUrl().newBuilder()
            .addQueryParameter("client", "dict-chrome-ex")
            .addQueryParameter("sl", languages[request.from])
            .addQueryParameter("tl", languages[request.to])
            .addQueryParameter("q", request.text)
            .build()

        val httpRequest = Request.Builder().url(url).build()


        val json = okHttpClient.newCall(httpRequest).executeAsync().body.string()
        RLog.d(this.toString(), json)
        val jsonArray = JSONArray(json)
        return when (request.to) {
            "en" -> TranslateResponse(
                Entities.escape(jsonArray.getString(0)))
            else -> TranslateResponse(jsonArray.getJSONArray(0).getString(0))
        }
    }
}