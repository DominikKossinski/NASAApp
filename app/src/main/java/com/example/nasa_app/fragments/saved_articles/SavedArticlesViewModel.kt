package com.example.nasa_app.fragments.saved_articles

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nasa_app.api.nasa.ArticlesService
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.extensions.toDateString
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
            makeRequest {
                val apiArticles = articlesService.getSavedArticles().body ?: emptyList()
                val apiDates = apiArticles.map {
                    it.date.toDateString()
                }
                Log.d("MyLog", "Saved dates ${apiDates}")
                val savedDates = appDatabase.nasaArticlesDao().getSavedDates()
                val toDelete = savedDates.filter { it !in apiDates }
                val toSave = apiArticles.filter { it.date.toDateString() !in savedDates }
                Log.d("MyLog", "Saved locally: $savedDates")
                Log.d("MyLog", "To Delete: $toDelete")
                Log.d("MyLog", "To download: $toSave")
                for (date in toDelete) {
                    appDatabase.nasaArticlesDao().deleteByDate(date)
                }
                for (article in toSave) {
                    appDatabase.nasaArticlesDao().saveArticle(article)
                }
                savedArticles.value = appDatabase.nasaArticlesDao().getSavedArticles()
                isLoadingData.value = false
            }
        }
    }


    fun getArticles() {
        viewModelScope.launch {
            savedArticles.value = appDatabase.nasaArticlesDao().getSavedArticles()
        }
    }

}