package com.example.nasa_app.dialogs

import dagger.hilt.android.lifecycle.HiltViewModel
import pl.kossa.myflights.architecture.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ArticleFromDateViewModel @Inject constructor(): BaseViewModel() {

    fun navigateToArticle(date: String) {
        navigate(ArticleFromDateDialogDirections.goToArticle(date))
    }
}