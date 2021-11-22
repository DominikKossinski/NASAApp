package com.example.nasa_app.fragments.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.example.nasa_app.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    val isLoginButtonEnabled = combine(_email, _password) { email, password ->
        return@combine password.isNotBlank() && email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(
            email
        ).matches()
    }
    val emailError = MutableStateFlow<Int?>(null)
    val passwordError = MutableStateFlow<Int?>(null)


    fun navigateToCreateAccount() {
        navigate(LoginFragmentDirections.goToCreateAccount())
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun login() {
        emailError.value = null
        passwordError.value = null
        isLoadingData.value = true
        when {
            _email.value.isBlank() -> {
                emailError.value = R.string.empty_email_error
                isLoadingData.value = false
            }
            _password.value.isBlank() -> {
                passwordError.value = R.string.empty_password_error
                isLoadingData.value = false
            }
            else -> {
                firebaseAuth.signInWithEmailAndPassword(_email.value, _password.value)
                    .addOnSuccessListener {
                        Log.d("MyLog", "Success sign in")
                        if (firebaseAuth.currentUser != null && firebaseAuth.currentUser?.isEmailVerified ?: false) {
                            refreshToken {
                                navigate(LoginFragmentDirections.goToLauncher())
                                isLoadingData.value = false
                            }
                        } else {
                            resendVerificationEmail()
                        }
                    }.addOnFailureListener {
                        when (it) {
                            is FirebaseAuthInvalidUserException -> {
                                emailError.value = R.string.no_user_error
                                _password.value = ""
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                when {
                                    it.message?.contains("email") == true -> {
                                        emailError.value = R.string.not_email_error
                                    }
                                    it.message?.contains("password") == true -> {
                                        passwordError.value = R.string.wrong_password_error
                                    }
                                    else -> {
                                        Log.d("MyLog", "Login exception$it")
                                        setToastMessage(R.string.unexpected_error)
                                    }
                                }
                            }
                            is FirebaseNetworkException -> {
                                setToastMessage(R.string.no_internet_error)
                            }
                            else -> {
                                Log.d("MyLog", "Login exception$it")
                                setToastMessage(R.string.unexpected_error)
                            }

                        }
                        isLoadingData.value = false
                    }
            }
        }
    }

    private fun resendVerificationEmail() {
        firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
            navigateToResendEmail()
        }?.addOnFailureListener {
            when (it) {
                is FirebaseNetworkException -> setToastMessage(R.string.no_internet_error)
                else -> setToastMessage(R.string.unexpected_error)
            }
        }
    }

    private fun navigateToResendEmail() {
        navigate(LoginFragmentDirections.goToEmailResend())
    }
}