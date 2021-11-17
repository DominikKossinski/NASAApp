package com.example.nasa_app.fragments.create_account

import android.util.Log
import android.util.Patterns
import com.example.nasa_app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.utils.PreferencesHelper
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    preferencesHelper: PreferencesHelper
) : BaseViewModel(preferencesHelper) {

    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _confirmPassword = MutableStateFlow("")
    private val _regulationsAccepted = MutableStateFlow(false)
    val isCreateAccountButtonEnabled = combine(
        _email,
        _password,
        _confirmPassword,
        _regulationsAccepted
    ) { email, password, confirmPassword, regulationsAccepted ->
        return@combine email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
                && isLoadingData.value.not()
                && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && regulationsAccepted
    }
    val emailError = MutableStateFlow<Int?>(null)
    val passwordError = MutableStateFlow<Int?>(null)

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setConfirmPassword(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }

    fun setRegulationsAccepted(accepted: Boolean) {
        _regulationsAccepted.value = accepted
    }

    fun createAccount() {
        emailError.value = null
        passwordError.value = null
        isLoadingData.value = true
        val email = _email.value
        val password = _password.value
        val confirmPassword = _confirmPassword.value
        when {
            email.isBlank() -> {
                emailError.value = R.string.empty_email_error
                isLoadingData.value = false
            }
            password.isBlank() -> {
                passwordError.value = R.string.empty_password_error
                isLoadingData.value = false
            }
            password != confirmPassword -> {
                passwordError.value = R.string.password_not_matches_error
                isLoadingData.value = false
            }
            else -> {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                    firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                        navigateToEmailResend()
                    }?.addOnFailureListener {
                        when (it) {
                            is FirebaseNetworkException -> setToastMessage(R.string.no_internet_error)
                            else -> {
                                setToastMessage(R.string.unexpected_error)
                            }
                        }
                    }
                }.addOnFailureListener {
                    isLoadingData.value = false
                    when (it) {
                        is FirebaseAuthWeakPasswordException -> {
                            passwordError.value = R.string.to_weak_password_error
                        }
                        is FirebaseAuthUserCollisionException -> {
                            emailError.value = R.string.user_exists_error
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            when {
                                it.message?.contains("email") == true -> {
                                    emailError.value = R.string.not_email_error
                                }
                                else -> {
                                    Log.d("MyLog", "Creating $it")
                                    setToastMessage(R.string.unexpected_error)
                                }
                            }
                        }
                        is FirebaseNetworkException -> setToastMessage(R.string.no_internet_error)
                        else -> {
                            Log.d("MyLog", "Creating account exception: $it")
                        }
                    }
                }
            }
        }
    }

    private fun navigateToEmailResend() {
        navigate(CreateAccountFragmentDirections.goToEmailResend())
    }
}