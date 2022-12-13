package com.example.nasa_app.fragments.article

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.nasa_app.R
import com.example.nasa_app.api.nasa.ArticlesService
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.api.nasa.SaveArticleRequest
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.extensions.toDateString
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val articlesService: ArticlesService,
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    val date = savedStateHandle.get<String>("date")!!

    val savedArticle = MutableStateFlow<NasaArticle?>(null)
    val articleFlow = MutableStateFlow<NasaArticle?>(null)

    init {
        getSavedArticle()
        fetchArticle()
    }

    private fun getSavedArticle() {
        viewModelScope.launch {
            savedArticle.value = appDatabase.nasaArticlesDao().getSavedArticleByDate(date)
        }
    }

    fun fetchArticle() {
        makeRequest {
            val response = articlesService.getArticle(date)
            response.body?.let { articleFlow.value = it }
        }
    }

    fun saveArticle() {
        articleFlow.value?.let { article ->
            makeRequest {
                articlesService.postSavedArticle(SaveArticleRequest(article.date))
                appDatabase.nasaArticlesDao().saveArticle(article)
                setToastMessage(R.string.article_saved)
                getSavedArticle()
            }
        }
    }

    fun deleteArticle() {
        articleFlow.value?.let { article ->
            makeRequest {
                articlesService.deleteSavedArticle(article.date.toDateString())
                appDatabase.nasaArticlesDao().deleteArticle(article)
                setToastMessage(R.string.article_deleted)
                getSavedArticle()
            }
        }
    }
}