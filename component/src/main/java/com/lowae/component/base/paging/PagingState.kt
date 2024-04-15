package com.lowae.component.base.paging

sealed class PagingState<out T>(
    open val endOfPaginationReached: Boolean
) {
    data class NotLoading<T>(
        val data: List<T>,
        val startOfPagination: Boolean,
        override val endOfPaginationReached: Boolean
    ) : PagingState<T>(endOfPaginationReached)

    data object Loading : PagingState<Nothing>(false)
    data class Error(val throwable: Throwable) : PagingState<Nothing>(false)
}