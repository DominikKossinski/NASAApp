package com.example.nasa_app.api.exceptions

import com.example.nasa_app.api.models.ApiError
import java.lang.Exception

class ApiServerException(val apiError: ApiError): Exception()