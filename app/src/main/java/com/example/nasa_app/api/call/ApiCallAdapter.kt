package com.example.nasa_app.api.call

import com.example.nasa_app.api.models.ApiErrorBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
import java.lang.reflect.Type

class ApiCallAdapter<S: Any>(
    private val successType: Type,
    private val errorBodyConverter: Converter<ResponseBody, ApiErrorBody>
) : CallAdapter<S, Call<ApiResponse<S>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<S>): Call<ApiResponse<S>> {
        return ApiCall(call, errorBodyConverter)
    }
}