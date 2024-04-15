package com.lowae.agrreader.translator.request

import com.lowae.agrreader.data.model.preference.TranslationOption
import com.lowae.agrreader.translator.TranslateRequest
import com.lowae.agrreader.translator.TranslateResponse
import okhttp3.OkHttpClient

sealed class TranslatorApi {

    companion object {

        const val DEFAULT_SOURCE_LANGUAGE = "auto"
        const val DEFAULT_TARGET_LANGUAGE = "zh-CN"

        val DEFAULT_LANGUAGE = Pair(DEFAULT_SOURCE_LANGUAGE, DEFAULT_TARGET_LANGUAGE)

        val LANGUAGES = mapOf(
            "auto" to "Auto",
            "en" to "English",
            "zh-CN" to "简体中文",
            "zh-TW" to "繁體中文"
        )

        fun toLanguageDesc(language: String) = LANGUAGES[language].orEmpty()

        fun getTranslator(option: TranslationOption): TranslatorApi {
            return when (option) {
                TranslationOption.GOOGLE_FREE -> GoogleTranslator
                TranslationOption.MICROSOFT_FREE -> MicrosoftTranslator
            }
        }

    }

    protected val okHttpClient = OkHttpClient.Builder().build()

    suspend fun request(request: TranslateRequest): TranslateResponse? {
        return try {
            translate(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    open val languages = LANGUAGES

    protected abstract suspend fun translate(request: TranslateRequest): TranslateResponse


}