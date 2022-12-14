package com.example.nasa_app.utils.interceptors

import com.example.nasa_app.utils.PreferencesHelper
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val preferencesHelper: PreferencesHelper
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${preferencesHelper.token}")
            .build()
        val initialResponse = chain.proceed(originalRequest)
        if (originalRequest.headers["TokenRefreshed"] != null) {
            return initialResponse
        } else {
            return if (initialResponse.code == 403 || initialResponse.code == 401) {
                runBlocking {
                    initialResponse.close()
                    preferencesHelper.refreshToken()
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${preferencesHelper.token}")
                        .addHeader("TokenRefreshed", "TokenRefreshed")
                        .build()
                    chain.proceed(newRequest)
                }
            } else {
                initialResponse
            }
        }
    }
}