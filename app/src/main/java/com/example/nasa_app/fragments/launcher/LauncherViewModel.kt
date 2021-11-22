package com.example.nasa_app.fragments.launcher

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.api.nasa.NasaService
import com.example.nasa_app.api.server.UsersService
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val usersService: UsersService,
    private val nasaService: NasaService,
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    val activityFinish = MutableSharedFlow<Unit>(0)


    fun fetchArticles() {
        makeRequest {
            if (firebaseAuth.currentUser == null || firebaseAuth.currentUser?.isEmailVerified == false) {
                firebaseAuth.signOut()
                delay(1_000)
                navigate(LauncherFragmentDirections.goToLogin())
            } else {
                val response = usersService.getArticles()
                val apiDates = response.body ?: emptyList()
                Log.d("MyLog", "Articles: ${response.body}")
                val savedDates = appDatabase.nasaArticlesDao().getSavedDates()
                Log.d("MyLog", "Saved locally: $savedDates")
                val toDelete = savedDates.filter { it !in apiDates }
                Log.d("MyLog", "To Delete: $toDelete")
                for (date in toDelete) {
                    appDatabase.nasaArticlesDao().deleteByDate(date)
                }
                val toDownload = apiDates.filter { it !in savedDates }
                Log.d("MyLog", "To download: $toDownload")
                for (date in toDownload) {
                    //TODO progress bar
                    val nasaResponse = nasaService.getArticle(BuildConfig.NASA_API_KEY, date)
                    nasaResponse.body?.let { appDatabase.nasaArticlesDao().saveArticle(it) }
                }
                goToMainActivity()
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