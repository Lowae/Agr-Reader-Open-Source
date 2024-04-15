package com.lowae.agrreader.ui.page.home.reading.webview

import android.webkit.WebResourceRequest
import androidx.annotation.MainThread
import com.lowae.agrreader.data.model.entities.ArticleParserResult
import com.lowae.agrreader.data.model.entities.ImageSrcEntity

interface ReadingWebCallback {

    fun onArticleParsed(result: ArticleParserResult)

    fun onArticleLoaded()

    @MainThread
    fun onArticleImageClick(imageSrcEntity: ImageSrcEntity)

    fun onUrlClick(request: WebResourceRequest?)

}