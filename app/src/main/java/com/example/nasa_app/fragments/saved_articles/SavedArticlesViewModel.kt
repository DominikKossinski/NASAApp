package com.example.nasa_app.fragments.saved_articles

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nasa_app.api.nasa.ArticlesService
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedArticlesViewModel @Inject constructor(
    private val articlesService: ArticlesService,
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    val savedArticles = MutableStateFlow<List<NasaArticle>>(emptyList())


    init {
        getArticles()
    }

    fun navigateToSavedArticle(date: String) {
        navigate(SavedArticlesFragmentDirections.goToSavedArticle(date))
    }

    fun refreshArticles() {
        isLoadingData.value = true
        val userId = firebaseAuth.currentUser?.uid
        userId?.let {
            // tODO getApi dates from API
            val apiDates = arrayListOf<String>()
            Log.d("MyLog", "Saved dates ${apiDates}")
            viewModelScope.launch {
                val savedDates = appDatabase.nasaArticlesDao().getSavedDates()
                val toDelete = savedDates.filter { it !in apiDates }
                val toDownload = apiDates.filter { it !in savedDates }
                Log.d("MyLog", "Saved locally: $savedDates")
                Log.d("MyLog", "To Delete: $toDelete")
                Log.d("MyLog", "To download: $toDownload")
                for (date in toDelete) {
                    appDatabase.nasaArticlesDao().deleteByDate(date)
                }
                makeRequest {
                    for (date in toDownload) {
                        val nasaResponse =
                            articlesService.getArticle(date)
                        nasaResponse.body?.let {
                            appDatabase.nasaArticlesDao().saveArticle(it)
                        }
                    }
                    savedArticles.value = appDatabase.nasaArticlesDao().getSavedArticles()
                    isLoadingData.value = false
                }
            }
        }
    }


    fun getArticles() {
        viewModelScope.launch {
            savedArticles.value = appDatabase.nasaArticlesDao().getSavedArticles()
        }
    }

}