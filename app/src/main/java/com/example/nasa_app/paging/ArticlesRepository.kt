package com.example.nasa_app.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.api.nasa.ArticlesService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ArticlesRepository(
    private val articlesService: ArticlesService
) {

    fun getArticles(
        query: String,
        loadingFlow: MutableStateFlow<Boolean>,
        apiErrorFlow: MutableStateFlow<ApiError?>
    ): Flow<PagingData<NasaArticle>> {
        return Pager(
            config = PagingConfig(1, enablePlaceholders = true),
            pagingSourceFactory = { ArticlesPagingSource(articlesService, query, loadingFlow, apiErrorFlow) }
        ).flow
    }

}