package com.example.nasa_app.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.api.nasa.NasaService
import com.example.nasa_app.extensions.getDayBegging
import com.example.nasa_app.extensions.minusDays
import com.example.nasa_app.extensions.toDate
import com.example.nasa_app.extensions.toDateString
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class ArticlesPagingSource(
    private val nasaService: NasaService,
    private val query: String,
    private val loadingFlow: MutableStateFlow<Boolean>,
    private val apiErrorFlow: MutableStateFlow<ApiError?>
) : PagingSource<Date, NasaArticle>() {

    private var emptyResponses = 0

    override suspend fun load(params: LoadParams<Date>): LoadResult<Date, NasaArticle> {
        loadingFlow.value = true
        val endKey = (params.key ?: Date()).getDayBegging()
        val startKey = endKey.minusDays(20)
        val loadResults = try {
            val response =
                nasaService.getArticles(
                    BuildConfig.NASA_API_KEY,
                    startKey.toDateString(),
                    endKey.toDateString()
                )
            val data = (response.body ?: emptyList())
            val filtered = data.sortedByDescending { it.date.time }
                .filter { it.title.lowercase().contains(query.lowercase()) }
            if(filtered.isEmpty()) {
                emptyResponses += 1
            } else {
                emptyResponses = 0
            }
            if(emptyResponses <= MAX_EMPTY_RESPONSES) {
                LoadResult.Page(
                    data = filtered,
                    prevKey = if (endKey.time != Date().getDayBegging().time) endKey else null,
                    nextKey = if (startKey.time >= "1995-06-16".toDate().time) startKey.minusDays(1) else null
                )
            } else {
                LoadResult.Page(
                    data = filtered,
                    prevKey = if (endKey.time != Date().getDayBegging().time) endKey else null,
                    nextKey = null
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        } finally {
            loadingFlow.value = false
        }
        return loadResults
    }

    override fun getRefreshKey(state: PagingState<Date, NasaArticle>): Date {
        return Date()
    }

    companion object {
        private const val MAX_EMPTY_RESPONSES = 3
    }
}