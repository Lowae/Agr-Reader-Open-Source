package com.lowae.agrreader.data.model.article

/**
 * Provide paginated and inserted separator data types for article list view.
 *
 * @see com.lowae.agrreader.ui.page.home.flow.ArticleList
 */
sealed class ArticleFlowItem {

    /**
     * The [Article] item.
     *
     * @see com.lowae.agrreader.ui.page.home.flow.ArticleItem
     */
    data class Article(val articleWithFeed: ArticleWithFeed) : ArticleFlowItem()

    /**
     * The feed publication date separator between [Article] items.
     *
     * @see com.lowae.agrreader.ui.page.home.flow.StickyHeader
     */
    class Date(val date: java.util.Date, val dateString: String) : ArticleFlowItem()
}