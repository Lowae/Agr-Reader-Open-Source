package com.lowae.component.base.paging

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

class DatabasePaging<T : Any>(
    private val pageSize: Int = 10,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
    private val onLoad: suspend (limit: Int, offset: Int) -> List<T>
) {
    val flow = MutableStateFlow(emptyList<T>())
    val pagingState: MutableStateFlow<PagingState<T>> =
        MutableStateFlow(
            PagingState.NotLoading(
                emptyList(),
                startOfPagination = true,
                endOfPaginationReached = false
            )
        )

    private var page = AtomicInteger(0)

    suspend fun load(refresh: Boolean = false) {
        if (refresh.not()) {
            if (pagingState.value is PagingState.Loading || pagingState.value.endOfPaginationReached) return
        } else {
            page.set(0)
        }
        Log.d("DatabasePaging", "load: $page")
        pagingState.emit(PagingState.Loading)
        val state = try {
            val startOfPagination = page.get() == 0
            val result = withContext(coroutineContext) {
                onLoad(
                    pageSize, page.getAndIncrement() * pageSize
                )
            }
            if (result.size < pageSize) {
                PagingState.NotLoading(result, startOfPagination, true)
            } else {
                PagingState.NotLoading(result, startOfPagination, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            PagingState.Error(e)
        }
        pagingState.emit(state)
    }

}