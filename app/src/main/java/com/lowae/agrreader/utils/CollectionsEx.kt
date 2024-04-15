package com.lowae.agrreader.utils

fun <T> Iterable<T>.chunkedAtLeastOne(size: Int): List<List<T>> {
    val fixedSize = if (size > 0) size else 1
    return windowed(fixedSize, fixedSize, partialWindows = true)
}

fun <T, R> Iterable<T>.chunkedAtLeastOne(size: Int, transform: (List<T>) -> R): List<R> {
    val fixedSize = if (size > 0) size else 1
    return windowed(fixedSize, fixedSize, partialWindows = true, transform = transform)
}

fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int) =
    subList(if (fromIndex < 0) 0 else fromIndex, if (toIndex > size) size else toIndex)