package com.example.nasa_app.fragments.articles

import android.util.Log
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.api.nasa.NasaService
import com.example.nasa_app.paging.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import pl.kossa.myflights.architecture.BaseViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val nasaService: NasaService,
    private val articlesRepository: ArticlesRepository
) : BaseViewModel() {

    val articles = MutableStateFlow<List<NasaArticle>>(emptyList())

    fun getArticles() = articlesRepository.getArticles()

    init {
//        fetchArticles()
    }

    fun fetchArticles() {
        makeRequest {
            val response =
                nasaService.getArticles(
                    BuildConfig.NASA_API_KEY,
                    "2021   -11-01",
                    "2021-11-11"
                )
            Log.d("MyLog", "Articles: ${response.body?.size}")
            response.body?.let { articles.value = it }
        }
    }

    fun navigateToArticle(date: String) {
        navigate(ArticlesFragmentDirections.goToArticle(date))
    }

    fun showArticleAddDialog() {
        navigate(ArticlesFragmentDirections.showArticleAddDialog())
    }
}