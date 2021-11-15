package com.example.nasa_app.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.nasa_app.Article
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.api.nasa.NasaService
import com.example.nasa_app.extensions.getDayBegging
import com.example.nasa_app.extensions.minusDays
import com.example.nasa_app.extensions.toDate
import com.example.nasa_app.extensions.toDateString
import java.lang.Exception
import java.util.*

class ArticlesPagingSource(
    private val nasaService: NasaService
) : PagingSource<Date, NasaArticle>() {


    override suspend fun load(params: LoadParams<Date>): LoadResult<Date, NasaArticle> {
        val endKey = (params.key ?: Date()).getDayBegging()
        val startKey = endKey.minusDays(20)
        Log.d("MyLog", "StartKey: ${startKey.toDateString()} End: ${endKey.toDateString()} Before: ${endKey.before(Date())}")
        return try {
            val response =
                nasaService.getArticles(BuildConfig.NASA_API_KEY, startKey.toDateString(), endKey.toDateString())
            val data = (response.body ?: emptyList())
            LoadResult.Page(
                data = data.sortedByDescending { it.date.time },
                prevKey = if (endKey.time != Date().getDayBegging().time) endKey else null,
                nextKey = if (startKey.time >= "1995-06-16".toDate().time) startKey.minusDays(1) else null
            )
        } catch (e: Exception) {
            //TODO errors
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Date, NasaArticle>): Date {
        return Date()
    }
}