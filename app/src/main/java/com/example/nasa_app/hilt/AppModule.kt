package com.example.nasa_app.hilt

import android.content.Context
import androidx.room.Room
import com.example.nasa_app.api.call.ApiResponseAdapterFactory
import com.example.nasa_app.api.nasa.ArticlesService
import com.example.nasa_app.managers.NasaNotificationsManager
import com.example.nasa_app.paging.ArticlesRepository
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import com.example.nasa_app.utils.analitics.AnalyticsTracker
import com.example.nasa_app.utils.interceptors.AuthInterceptor
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
    fun provideAuthInterceptor(preferencesHelper: PreferencesHelper): AuthInterceptor {
        return AuthInterceptor(preferencesHelper)
    }

    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("http://10.0.2.2:8080/")
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideNasaService(retrofit: Retrofit): ArticlesService {
        return retrofit.create(ArticlesService::class.java)
    }

    @Provides
    fun provideArticlesRepository(articlesService: ArticlesService): ArticlesRepository {
        return ArticlesRepository(articlesService)
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