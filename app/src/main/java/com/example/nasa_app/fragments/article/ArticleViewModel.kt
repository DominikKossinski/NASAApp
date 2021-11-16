package com.example.nasa_app.fragments.article

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.R
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.api.nasa.NasaService
import com.example.nasa_app.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.kossa.myflights.architecture.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nasaService: NasaService,
    private val appDatabase: AppDatabase
) : BaseViewModel() {

    val date = savedStateHandle.get<String>("date")!!

    val savedArticle = MutableStateFlow<NasaArticle?>(null)
    val article = MutableStateFlow<NasaArticle?>(null)

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
            Log.d("MyLog", "Date $date")
            val response = nasaService.getArticle(BuildConfig.NASA_API_KEY, date)
            Log.d("MyLog", "Response: ${response.body}")
            response.body?.let { article.value = it }
        }
    }

    fun saveArticle() {
        article.value?.let {
            viewModelScope.launch {
                appDatabase.nasaArticlesDao().saveArticle(it)
                setToastMessage(R.string.article_saved)
                getSavedArticle()
            }
        }
    }

    fun deleteArticle() {
        article.value?.let {
            viewModelScope.launch {
                appDatabase.nasaArticlesDao().deleteArticle(it)
                setToastMessage(R.string.article_deleted)
                getSavedArticle()
            }
        }
    }
}