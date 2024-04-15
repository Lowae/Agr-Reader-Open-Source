package com.lowae.agrreader.utils.ext

import java.util.UUID

private val EMPTY_BYTE_ARRAY = ByteArray(0)

sealed interface IDGenerator {

    fun id(seed: String? = null): String

}

data object GroupIdGenerator : IDGenerator {

    val DEFAULT_ID = UUID.nameUUIDFromBytes(EMPTY_BYTE_ARRAY).toString()

    override fun id(seed: String?): String = UUID.randomUUID().toString()

    fun isDefaultGroupId(groupId: String) = DEFAULT_ID == groupId
}

data object ArticleIdGenerator : IDGenerator {
    override fun id(seed: String?): String = UUID.randomUUID().toString()

}

data object FeedIdGenerator : IDGenerator {
    override fun id(seed: String?): String =
        if (seed.isNullOrEmpty()) {
            UUID.randomUUID().toString()
        } else {
            UUID.nameUUIDFromBytes(seed.encodeToByteArray()).toString()
        }

}