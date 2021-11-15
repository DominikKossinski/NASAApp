package com.example.nasa_app.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.api.nasa.NasaService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArticlesRepository(
    private val nasaService: NasaService
) {

    fun getArticles(): Flow<PagingData<NasaArticle>> {
        return Pager(
            config = PagingConfig(1, enablePlaceholders = true),
            pagingSourceFactory = { ArticlesPagingSource(nasaService) }
        ).flow
    }
}