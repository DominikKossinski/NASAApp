package com.example.nasa_app.fragments.article

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.api.nasa.NasaService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import pl.kossa.myflights.architecture.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nasaService: NasaService
) : BaseViewModel() {

    val date = savedStateHandle.get<String>("date")!!
    val article = MutableStateFlow<NasaArticle?>(null)

    init {
        fetchArticle()
    }

    fun fetchArticle() {
        makeRequest {
            Log.d("MyLog", "Date $date")
            val response = nasaService.getArticle(BuildConfig.NASA_API_KEY, date)
            Log.d("MyLog", "Response: ${response.body}")
            response.body?.let { article.value = it }
        }
    }
}