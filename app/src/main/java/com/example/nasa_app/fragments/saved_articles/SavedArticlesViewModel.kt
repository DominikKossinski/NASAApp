package com.example.nasa_app.fragments.saved_articles

import androidx.lifecycle.viewModelScope
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.api.nasa.NasaService
import com.example.nasa_app.api.server.UsersService
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedArticlesViewModel @Inject constructor(
    private val usersService: UsersService,
    private val nasaService: NasaService,
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
        makeRequest {
            val response = usersService.getArticles()
            val apiDates = response.body ?: emptyList()
            val savedDates = appDatabase.nasaArticlesDao().getSavedDates()
            val toDelete = savedDates.filter { it !in apiDates }
            for (date in toDelete) {
                appDatabase.nasaArticlesDao().deleteByDate(date)
            }
            val toDownload = apiDates.filter { it !in savedDates }
            for (date in toDownload) {
                val nasaResponse = nasaService.getArticle(BuildConfig.NASA_API_KEY, date)
                nasaResponse.body?.let { appDatabase.nasaArticlesDao().saveArticle(it) }
            }
            savedArticles.value = appDatabase.nasaArticlesDao().getSavedArticles()
        }
    }

    fun getArticles() {
        viewModelScope.launch {
            savedArticles.value = appDatabase.nasaArticlesDao().getSavedArticles()
        }
    }

}