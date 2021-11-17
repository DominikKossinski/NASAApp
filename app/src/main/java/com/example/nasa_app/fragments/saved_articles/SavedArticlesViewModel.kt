package com.example.nasa_app.fragments.saved_articles

import com.example.nasa_app.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.kossa.myflights.architecture.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SavedArticlesViewModel @Inject constructor(
    private val appDatabase: AppDatabase
) : BaseViewModel() {

    val savedArticles = appDatabase.nasaArticlesDao().getSavedArticles()

    fun navigateToSavedArticle(date: String) {
        navigate(SavedArticlesFragmentDirections.goToSavedArticle(date))
    }

}