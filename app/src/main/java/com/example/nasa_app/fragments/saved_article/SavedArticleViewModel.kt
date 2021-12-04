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
import com.example.nasa_app.extensions.toDateString
import com.example.nasa_app.utils.PreferencesHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

@HiltViewModel
class SavedArticleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    private val db = Firebase.firestore
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
        savedArticle.value?.let { article ->
            val userId = firebaseAuth.currentUser?.uid ?: return
            val date = article.date.toDateString()
            db.collection(userId).document("articles").collection("articles").document(date)
                .delete()
                .addOnSuccessListener {
                    viewModelScope.launch {
                        appDatabase.nasaArticlesDao().deleteArticle(article)
                        setToastMessage(R.string.article_deleted)
                        navigateBack()
                    }
                }
                .addOnFailureListener {
                    setToastMessage(R.string.unexpected_error)
                }
        }
    }
}