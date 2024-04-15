package com.lowae.agrreader.utils.ext

import androidx.compose.foundation.lazy.LazyListScope

enum class LazyListContentType {
    HEADER,
    FOOTER
}

class LazyListExt {
    companion object
}

val LazyListScope.FOOTER: LazyListContentType
    get() = LazyListContentType.FOOTER