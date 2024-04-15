package com.lowae.agrreader.ui.page

import com.lowae.agrreader.data.action.ArticleActions
import com.lowae.agrreader.data.action.ArticleMarkReadAction
import com.lowae.agrreader.data.action.ArticleMarkStarAction
import com.lowae.agrreader.data.model.article.ArticleFlowItem
import com.lowae.agrreader.data.model.article.ArticleWithFeed
import com.lowae.agrreader.utils.ext.formatAsFlowString
import java.util.Date

fun List<ArticleFlowItem>.appendPagingData(
    page: List<ArticleWithFeed>,
    insertDateSeparators: Boolean = true
): List<ArticleFlowItem> {
    if (page.isEmpty()) return this
    val articles = sequence {
        this.yieldAll(
            this@appendPagingData.asSequence().filterIsInstance<ArticleFlowItem.Article>()
        )
        this.yieldAll(page.asSequence().map {
            ArticleFlowItem.Article(
                it.apply {
                    article.dateString = article.date.formatAsFlowString()
                }
            )
        })
    }.distinctBy { it.articleWithFeed.article.id }

    return if (insertDateSeparators.not()) {
        articles.toList()
    } else {
        val pagingData = mutableListOf<ArticleFlowItem>()
        var prevDate: String? = null
        articles.forEach { item ->
            val curDate = item.articleWithFeed.article.date.formatAsFlowString(showHour = false)
            if (curDate != prevDate) {
                pagingData.add(
                    ArticleFlowItem.Date(item.articleWithFeed.article.date, curDate)
                )
            }
            prevDate = curDate
            pagingData.add(item)
        }
        pagingData
    }
}

fun List<ArticleFlowItem>.updateByAction(actions: ArticleActions): List<ArticleFlowItem> {
    if (isEmpty()) return this
    return when (actions) {
        is ArticleMarkReadAction -> markAsReadAction(actions)
        is ArticleMarkStarAction -> markAsStarAction(actions)
    }
}

private fun List<ArticleFlowItem>.markAsReadAction(action: ArticleMarkReadAction): MutableList<ArticleFlowItem> {
    val (groupId, feedId, articleId, before, isUnread, latest) = action
    val updates = mutableListOf<Pair<Int, ArticleFlowItem>>()
    when {
        groupId != null -> {
            this.forEachIndexed { index, item ->
                if (item is ArticleFlowItem.Article && item.articleWithFeed.feed.groupId == groupId && checkDate(
                        item,
                        before,
                        latest
                    )
                ) {
                    updates.add(index to item.copyOf(isUnread))
                }
            }
        }

        feedId != null -> {
            this.forEachIndexed { index, item ->
                if (item is ArticleFlowItem.Article && item.articleWithFeed.feed.id == feedId && checkDate(
                        item,
                        before,
                        latest
                    )
                ) {
                    updates.add(index to item.copyOf(isUnread))
                }
            }
        }

        articleId != null -> {
            this.forEachIndexed { index, item ->
                if (item is ArticleFlowItem.Article && item.articleWithFeed.article.id == articleId) {
                    updates.add(index to item.copyOf(isUnread))
                }
            }
        }

        before != null -> {
            this.forEachIndexed { index, item ->
                if (item is ArticleFlowItem.Article && checkDate(item, before, latest)) {
                    updates.add(index to item.copyOf(isUnread))
                }
            }
        }
    }
    return this.toMutableList().also { mutable ->
        updates.forEach {
            mutable[it.first] = it.second
        }
    }
}

private fun List<ArticleFlowItem>.markAsStarAction(action: ArticleMarkStarAction): MutableList<ArticleFlowItem> {
    val updates = mutableListOf<Pair<Int, ArticleFlowItem>>()
    this.forEachIndexed { index, item ->
        if (item is ArticleFlowItem.Article && item.articleWithFeed.article.id == action.articleId) {
            updates.add(index to item.copyOf(isStarred = action.isStarred))
        }
    }
    return this.toMutableList().also { mutable ->
        updates.forEach {
            mutable[it.first] = it.second
        }
    }
}

private fun ArticleFlowItem.Article.copyOf(
    isUnread: Boolean = this.articleWithFeed.article.isUnread,
    isStarred: Boolean = this.articleWithFeed.article.isStarred
): ArticleFlowItem.Article {
    return this.copy(
        articleWithFeed = articleWithFeed.copy(
            article = articleWithFeed.article.copy(
                isUnread = isUnread,
                isStarred = isStarred
            ).apply {
                dateString = articleWithFeed.article.dateString
            }
        )
    )
}

private fun checkDate(
    item: ArticleFlowItem.Article,
    before: Date? = null,
    latest: Boolean
): Boolean {
    if (before == null) return true
    return if (latest) item.articleWithFeed.article.date >= before else item.articleWithFeed.article.date <= before
}