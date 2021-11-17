package com.example.nasa_app.fragments.saved_articles

import com.example.nasa_app.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.utils.PreferencesHelper
import javax.inject.Inject

@HiltViewModel
class SavedArticlesViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    preferencesHelper: PreferencesHelper
) : BaseViewModel(preferencesHelper) {

    val savedArticles = appDatabase.nasaArticlesDao().getSavedArticles()

    fun navigateToSavedArticle(date: String) {
        navigate(SavedArticlesFragmentDirections.goToSavedArticle(date))
    }

}