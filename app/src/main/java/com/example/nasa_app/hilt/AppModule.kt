package com.example.nasa_app.hilt

import android.content.Context
import androidx.room.Room
import com.example.nasa_app.api.call.ApiResponseAdapterFactory
import com.example.nasa_app.api.nasa.NasaService
import com.example.nasa_app.api.server.UsersService
import com.example.nasa_app.paging.ArticlesRepository
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Qualifier

@Qualifier
annotation class NasaApiModule

@Qualifier
annotation class FirebaseApiModule

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @NasaApiModule
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

//    @Provides
//    fun providePreferencesHelper(@ApplicationContext applicationContext: Context): PreferencesHelper {
//        return PreferencesHelper(applicationContext)
//    }

    @Provides
    @NasaApiModule
    fun provideRetrofit(@NasaApiModule client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.nasa.gov/")
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideNasaService(@NasaApiModule retrofit: Retrofit): NasaService {
        return retrofit.create(NasaService::class.java)
    }

    @Provides
    fun provideArticlesRepository(nasaService: NasaService): ArticlesRepository {
        return ArticlesRepository(nasaService)
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "articles-db"
        ).build()
    }
}

@InstallIn(SingletonComponent::class)
@Module
object NasaAppModule {

    @Provides
    fun providePreferencesHelper(@ApplicationContext applicationContext: Context): PreferencesHelper {
        return PreferencesHelper(applicationContext)
    }

    @Provides
    @FirebaseApiModule
    fun provideOkHttpClient(preferencesHelper: PreferencesHelper): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${preferencesHelper.token}")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
    }

    @Provides
    @FirebaseApiModule
    fun provideRetrofit(@FirebaseApiModule client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("http://10.0.2.2:5001/nasaapp-334ad/us-central1/app/")
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideUsersService(@FirebaseApiModule retrofit: Retrofit): UsersService {
        return retrofit.create(UsersService::class.java)
    }
}