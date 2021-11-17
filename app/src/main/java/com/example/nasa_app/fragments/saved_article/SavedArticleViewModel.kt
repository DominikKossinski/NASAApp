package com.example.nasa_app.fragments.saved_article

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.nasa_app.R
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.utils.PreferencesHelper
import javax.inject.Inject

@HiltViewModel
class SavedArticleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val appDatabase: AppDatabase,
    preferencesHelper: PreferencesHelper
) : BaseViewModel(preferencesHelper) {

    private val date = savedStateHandle.get<String>("date")!!
    val savedArticle = MutableStateFlow<NasaArticle?>(null)

    init {
        getSavedArticle()
    }

    private fun getSavedArticle() {
        viewModelScope.launch {
            isLoadingData.value = true
            val article = appDatabase.nasaArticlesDao().getSavedArticleByDate(date)
            if (article == null) {
                setToastMessage(R.string.article_not_found)
                navigateBack()
            } else {
                savedArticle.value = article
            }
            isLoadingData.value = false
        }
    }

    fun deleteArticle() {
        savedArticle.value?.let {
            viewModelScope.launch {
                appDatabase.nasaArticlesDao().deleteArticle(it)
                setToastMessage(R.string.article_deleted)
                navigateBack()
            }
        }
    }
}