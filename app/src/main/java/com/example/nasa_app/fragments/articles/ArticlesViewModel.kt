package com.example.nasa_app.fragments.articles

import com.example.nasa_app.paging.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.utils.PreferencesHelper
import javax.inject.Inject

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    preferencesHelper: PreferencesHelper
) : BaseViewModel(preferencesHelper) {

    private var _query = MutableStateFlow("")
    val articles = _query.flatMapLatest {
        articlesRepository.getArticles(it, isLoadingData, apiErrorFlow)
    }


    fun navigateToArticle(date: String) {
        navigate(ArticlesFragmentDirections.goToArticle(date))
    }

    fun showArticleAddDialog() {
        navigate(ArticlesFragmentDirections.showArticleAddDialog())
    }

    fun setSearchQuery(query: String) {
        _query.value = query
    }
}