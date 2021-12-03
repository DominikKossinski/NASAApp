package com.example.nasa_app.hilt

import android.content.Context
import androidx.room.Room
import com.example.nasa_app.api.call.ApiResponseAdapterFactory
import com.example.nasa_app.api.nasa.NasaService
import com.example.nasa_app.managers.NasaNotificationsManager
import com.example.nasa_app.paging.ArticlesRepository
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.nasa.gov/")
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideNasaService(retrofit: Retrofit): NasaService {
        return retrofit.create(NasaService::class.java)
    }

    @Provides
    fun provideArticlesRepository(nasaService: NasaService): ArticlesRepository {
        return ArticlesRepository(nasaService)
    }

    @Provides
    fun providePreferencesHelper(@ApplicationContext applicationContext: Context): PreferencesHelper {
        return PreferencesHelper(applicationContext)
    }

    @Provides
    fun provideNasaNotificationsManager(
        @ApplicationContext applicationContext: Context,
        preferencesHelper: PreferencesHelper
    ): NasaNotificationsManager {
        return NasaNotificationsManager(applicationContext, preferencesHelper)
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "articles-db"
        ).build()
    }
}