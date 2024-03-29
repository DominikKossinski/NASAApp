package com.example.nasa_app.architecture

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.example.nasa_app.R
import com.example.nasa_app.api.exceptions.ApiServerException
import com.example.nasa_app.api.exceptions.NoInternetException
import com.example.nasa_app.api.exceptions.UnauthorizedException
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

abstract class BaseViewModel(
    protected val preferencesHelper: PreferencesHelper,
    protected val appDatabase: AppDatabase
) : ViewModel() {


    protected val firebaseAuth = FirebaseAuth.getInstance()

    private var tokenRefreshed = false

    val toastMessage = MutableSharedFlow<Int?>(0)
    val isLoadingData = MutableStateFlow(false)
    val apiErrorFlow = MutableStateFlow<ApiError?>(null)
    private val navDirectionFlow = MutableSharedFlow<NavDirections>(0)
    val backFlow = MutableSharedFlow<Unit>(0)
    val signOutFlow = MutableSharedFlow<Unit>(0)


    fun makeRequest(block: suspend () -> Unit) {
        viewModelScope.launch {
            isLoadingData.value = true
            try {
                block.invoke()
            } catch (e: UnauthorizedException) {
                Log.e("MyLog", "Unauthorized")
                if (tokenRefreshed) {
                    firebaseAuth.signOut()
                    signOutFlow.emit(Unit)
                } else {
                    tokenRefreshed = true
                    refreshToken {
                        makeRequest(block)
                    }
                }
            } catch (e: NoInternetException) {
                setToastMessage(R.string.no_internet_error)
            } catch (e: ApiServerException) {
                apiErrorFlow.emit(e.apiError)
            } catch (e: Exception) {
                when (e) {
                    is SocketTimeoutException, is UnknownHostException, is ConnectionShutdownException, is IOException -> {
                        setToastMessage(R.string.no_connection_to_server_error)
                        apiErrorFlow.emit(ApiError(500, null, e))
                    }
                    else -> {
                        e.printStackTrace()
                        setToastMessage(R.string.unexpected_error)
                    }
                }
            } finally {
                isLoadingData.value = false
            }
        }
    }


    protected fun refreshToken(onSuccess: () -> Unit) {
        firebaseAuth.currentUser?.getIdToken(true)?.addOnSuccessListener {
            tokenRefreshed = false
            preferencesHelper.token = it.token
            onSuccess()
        }?.addOnFailureListener {
            when (it) {
                is FirebaseNetworkException -> {
                    setToastMessage(R.string.no_internet_error)
                }
                else -> {
                    Log.d("MyLog", "Token error: $it")
                    setToastMessage(R.string.unexpected_error)
                    firebaseAuth.signOut()
                    preferencesHelper.token = null
                    viewModelScope.launch {
                        signOutFlow.emit(Unit)
                    }
                }
            }
            isLoadingData.value = false
        }
    }

    protected fun navigate(action: NavDirections) {
        viewModelScope.launch {
            navDirectionFlow.emit(action)
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            backFlow.emit(Unit)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        preferencesHelper.token = null
        viewModelScope.launch {
            signOutFlow.emit(Unit)
        }
    }

    fun setToastMessage(stringId: Int?) {
        viewModelScope.launch {
            toastMessage.emit(stringId)
        }
    }

    fun getNavDirectionsFlow() = navDirectionFlow
}