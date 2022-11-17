package com.example.nasa_app.fragments.articles

import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.paging.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.extensions.toDateString
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import com.example.nasa_app.utils.analitics.AnalyticsTracker
import javax.inject.Inject

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    private val analyticsTracker: AnalyticsTracker,
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    private var _query = MutableStateFlow("")
    val articles = _query.flatMapLatest {
        articlesRepository.getArticles(it, isLoadingData, apiErrorFlow)
    }


    fun navigateToArticle(article: NasaArticle) {
        analyticsTracker.logClickOpenArticle(article)
        navigate(ArticlesFragmentDirections.goToArticle(article.date.toDateString()))
    }

    fun showArticleAddDialog() {
        navigate(ArticlesFragmentDirections.showArticleAddDialog())
    }

    fun setSearchQuery(query: String) {
        _query.value = query
    }
}