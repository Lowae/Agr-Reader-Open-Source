package com.lowae.agrreader.data.model.general

sealed interface Status<T>

interface All<T> : Status<T>
interface Unread<T> : Status<T>
interface Starred<T> : Status<T>


@JvmInline
value class AllValue<T>(val value: T) : All<T>

@JvmInline
value class UnreadValue<T>(val value: T) : Unread<T>
@JvmInline
value class StarredValue<T>(val value: T) : Starred<T>
