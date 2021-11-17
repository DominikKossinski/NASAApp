package com.example.nasa_app.api.call

import com.example.nasa_app.api.models.ApiErrorBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResponseAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Call::class.java != getRawType(returnType)) return null
        check(returnType is ParameterizedType) {
            "return type must be parameterized"
        }
        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != ApiResponse::class.java) return null

        check(responseType is ParameterizedType) { "Response must be parameterized as NetworkResponse<Foo> or NetworkResponse<out Foo>" }

        val successBodyType = getParameterUpperBound(0, responseType)

        val errorBodyConverter =
            retrofit.nextResponseBodyConverter<ApiErrorBody>(
                null,
                ApiErrorBody::class.java,
                annotations
            )
        return ApiCallAdapter<Any>(successBodyType, errorBodyConverter)
    }
}