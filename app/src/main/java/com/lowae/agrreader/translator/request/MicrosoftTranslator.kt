package com.lowae.agrreader.translator.request

import androidx.annotation.Keep
import androidx.datastore.preferences.core.stringPreferencesKey
import com.lowae.agrreader.translator.TranslateRequest
import com.lowae.agrreader.translator.TranslateResponse
import com.lowae.agrreader.utils.GsonUtils
import com.lowae.agrreader.utils.RLog
import com.lowae.agrreader.utils.ext.KvDataStore
import com.lowae.agrreader.utils.ext.decodeBase64
import com.lowae.agrreader.utils.ext.get
import com.lowae.agrreader.utils.ext.map
import com.lowae.agrreader.utils.ext.put
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.executeAsync
import org.json.JSONArray
import org.json.JSONObject

data object MicrosoftTranslator : TranslatorApi() {

    private val tokenFetcher = MicrosoftTokenFetcher(okHttpClient)

    private val extraLanguages = mapOf("auto" to "", "zh-CN" to "zh-Hans", "zh-TW" to "zh-Hant")

    override val languages: Map<String, String> =
        super.languages.mapValues { extraLanguages[it.key] ?: it.key }

    override suspend fun translate(request: TranslateRequest): TranslateResponse {
        val token = tokenFetcher.getAuthToken()
        val url =
            "https://api-edge.cognitive.microsofttranslator.com/translate".toHttpUrl().newBuilder()
                .addQueryParameter("api-version", "3.0")
                .addQueryParameter("from", languages[request.from])
                .addQueryParameter("to", languages[request.to])
                .build()
        val requestBody =
            """[{"Text":${GsonUtils.toJson(request.text)}}]""".toRequestBody("application/json".toMediaTypeOrNull())

        val httpRequest = Request.Builder().url(url)
            .method("POST", requestBody)
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", "Bearer $token")
            .build()

        /**
         * [{"translations":[{"text":"这是一个翻译器","to":"zh-Hans"}]}]
         */
        val json = okHttpClient.newCall(httpRequest).executeAsync().body.string()
        val translation = JSONArray(json).getJSONObject(0).getJSONArray("translations")
            .map { it.getString("text") }.joinToString(separator = " ") { it }
        return TranslateResponse(translation)
    }
}

private class MicrosoftTokenFetcher(private val okHttpClient: OkHttpClient) {

    companion object {
        private const val URL_MICROSOFT_AUTH = "https://edge.microsoft.com/translate/auth"
        private val KEY_MS_AUTH = stringPreferencesKey("KEY_MS_AUTH")
    }


    suspend fun getAuthToken(): String {
        val cache = GsonUtils.fromJson<TokenCache>(KvDataStore.get(KEY_MS_AUTH).orEmpty())
        return if (cache != null && cache.token.isNotEmpty() && cache.exp * 1000 > System.currentTimeMillis() + 1000) {
            cache.token
        } else {
            val responseToken =
                okHttpClient.newCall(Request.Builder().url(URL_MICROSOFT_AUTH).build())
                    .executeAsync().body.string()
            RLog.d("MicrosoftTokenFetcher", "token: $responseToken")
            val exp = JSONObject(responseToken.split(".")[1].decodeBase64()).getLong("exp")
            KvDataStore.put(KEY_MS_AUTH, GsonUtils.toJson(TokenCache(responseToken, exp)))
            responseToken
        }
    }

    @Keep
    private class TokenCache(
        val token: String = "",
        val exp: Long = 0L
    )
}