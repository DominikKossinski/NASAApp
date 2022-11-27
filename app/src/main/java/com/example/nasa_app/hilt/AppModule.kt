package com.example.nasa_app.hilt

import android.content.Context
import androidx.room.Room
import com.example.nasa_app.api.call.ApiResponseAdapterFactory
import com.example.nasa_app.api.nasa.NasaService
import com.example.nasa_app.managers.NasaNotificationsManager
import com.example.nasa_app.paging.ArticlesRepository
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import com.example.nasa_app.utils.analitics.AnalyticsTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun providePreferencesHelper(@ApplicationContext applicationContext: Context): PreferencesHelper {
        return PreferencesHelper(applicationContext)
    }


    @Provides
    fun provideOkHttpClient(preferencesHelper: PreferencesHelper): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = preferencesHelper.token
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://10.0.2.2:8080/")
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

    @Provides
    @Singleton
    fun provideAnalyticsTracker(): AnalyticsTracker {
        return AnalyticsTracker()
    }
}