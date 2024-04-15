package com.lowae.agrreader.utils

inline fun String?.ifNullOrBlank(predicate: (String?) -> String) = if (isNullOrBlank()) predicate(this) else this