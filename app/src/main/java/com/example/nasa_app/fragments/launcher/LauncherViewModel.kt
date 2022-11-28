package com.example.nasa_app.fragments.launcher

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nasa_app.api.nasa.ArticlesService
import com.example.nasa_app.architecture.BaseViewModel
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
                    viewModelScope.launch {
                        // TODO get apiDates from API
                        val apiDates = emptyList<String>()
                        val savedDates = appDatabase.nasaArticlesDao().getSavedDates()
                        val toDelete = savedDates.filter { it !in apiDates }
                        val toDownload = apiDates.filter { it !in savedDates }
                        Log.d("MyLog", "Saved locally: $savedDates")
                        Log.d("MyLog", "To Delete: $toDelete")
                        Log.d("MyLog", "To download: $toDownload")
                        goToMainActivity()
                        for (date in toDelete) {
                            appDatabase.nasaArticlesDao().deleteByDate(date)
                        }
                        makeRequest {
                            for (date in toDownload) {
                                //TODO progress bar
                                val nasaResponse =
                                    articlesService.getArticle(date)
                                nasaResponse.body?.let {
                                    appDatabase.nasaArticlesDao().saveArticle(it)
                                }
                            }
                        }
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