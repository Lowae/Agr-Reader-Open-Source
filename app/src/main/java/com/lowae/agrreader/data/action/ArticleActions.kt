package com.lowae.agrreader.data.action

import java.util.Date

sealed interface ArticleActions

data class ArticleMarkReadAction(
    val groupId: String? = null,
    val feedId: String? = null,
    val articleId: String? = null,
    val before: Date? = null,
    val isUnread: Boolean = false,
    val latest: Boolean = true
) : ArticleActions

data class ArticleMarkStarAction(
    val articleId: String,
    val isStarred: Boolean
) : ArticleActions