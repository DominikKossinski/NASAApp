package com.example.nasa_app.dialogs

import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import javax.inject.Inject

@HiltViewModel
class ArticleFromDateViewModel @Inject constructor(
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    fun navigateToArticle(date: String) {
        navigate(ArticleFromDateDialogDirections.goToArticle(date))
    }
}