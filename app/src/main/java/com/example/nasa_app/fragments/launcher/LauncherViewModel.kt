package com.example.nasa_app.fragments.launcher

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nasa_app.api.nasa.ArticlesService
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.extensions.toDateString
import com.example.nasa_app.extensions.toLocalDateString
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import com.example.nasa_app.utils.analitics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val articlesService: ArticlesService,
    private val analyticsTracker: AnalyticsTracker,
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    val activityFinish = MutableSharedFlow<Unit>(1)
    val isSyncFlow = MutableStateFlow(false)


    fun fetchArticles() {
        viewModelScope.launch {
            if (firebaseAuth.currentUser == null || firebaseAuth.currentUser?.isEmailVerified == false) {
                firebaseAuth.signOut()
                analyticsTracker.setUserId(null)
                viewModelScope.launch {
                    delay(1_000)
                    navigate(LauncherFragmentDirections.goToLogin())
                }
            } else {
                isSyncFlow.value = true
                val userId = firebaseAuth.currentUser?.uid
                analyticsTracker.setUserId(userId)
                userId?.let {
                    makeRequest {
                        val apiArticles = articlesService.getSavedArticles().body ?: emptyList()
                        val apiDates = apiArticles.map {
                            it.date.toLocalDateString()
                        }
                        Log.d("MyLog", "Saved dates ${apiDates}")
                        val savedDates = appDatabase.nasaArticlesDao().getSavedDates()
                        val toDelete = savedDates.filter { it !in apiDates }
                        val toSave = apiArticles.filter { it.date.toLocalDateString() !in savedDates }
                        Log.d("MyLog", "Saved locally: $savedDates")
                        Log.d("MyLog", "To Delete: $toDelete")
                        Log.d("MyLog", "To download: $toSave")
                        for (date in toDelete) {
                            appDatabase.nasaArticlesDao().deleteByDate(date)
                        }
                        for (article in toSave) {
                            appDatabase.nasaArticlesDao().saveArticle(article)
                        }
                        goToMainActivity()
                    }
                }
            }
        }
    }

    fun goToMainActivity() {
        navigate(LauncherFragmentDirections.goToMainActivity())
        viewModelScope.launch {
            activityFinish.emit(Unit)
        }
    }
}