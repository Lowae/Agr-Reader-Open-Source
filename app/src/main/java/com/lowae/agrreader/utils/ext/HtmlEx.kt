package com.lowae.agrreader.utils.ext

import android.os.Build
import android.text.Html
import android.text.Spanned

fun String.fromHtmlCompact(): Spanned? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    }
}