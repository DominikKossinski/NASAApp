package com.example.nasa_app.fragments.article

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.R
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.api.nasa.NasaService
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.extensions.toDateString
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nasaService: NasaService,
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    private val db = Firebase.firestore

    val date = savedStateHandle.get<String>("date")!!

    val savedArticle = MutableStateFlow<NasaArticle?>(null)
    val articleFlow = MutableStateFlow<NasaArticle?>(null)

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
            response.body?.let { articleFlow.value = it }
        }
    }

    fun saveArticle() {
        articleFlow.value?.let { article ->
            val userId = firebaseAuth.currentUser?.uid ?: return
            val date = article.date.toDateString()
            val articleData = hashMapOf(
                "date" to date
            )
            db.collection(userId).document("articles").collection("articles").document(date)
                .set(articleData)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        appDatabase.nasaArticlesDao().saveArticle(article)
                        setToastMessage(R.string.article_saved)
                        getSavedArticle()
                    }
                }
                .addOnFailureListener {
                    setToastMessage(R.string.unexpected_error)
                }
        }
    }

    fun deleteArticle() {
        articleFlow.value?.let { article ->
            val userId = firebaseAuth.currentUser?.uid ?: return
            val date = article.date.toDateString()
            db.collection(userId).document("articles").collection("articles").document(date)
                .delete()
                .addOnSuccessListener {
                    viewModelScope.launch {
                        appDatabase.nasaArticlesDao().deleteArticle(article)
                        setToastMessage(R.string.article_deleted)
                        getSavedArticle()
                    }
                }
                .addOnFailureListener {
                    setToastMessage(R.string.unexpected_error)
                }
        }
    }
}