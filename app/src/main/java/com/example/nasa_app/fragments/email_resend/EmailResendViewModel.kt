package com.example.nasa_app.fragments.email_resend

import com.example.nasa_app.R
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.utils.PreferencesHelper
import com.google.firebase.FirebaseNetworkException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmailResendViewModel @Inject constructor(
    preferencesHelper: PreferencesHelper
) : BaseViewModel(preferencesHelper) {

    fun resendEmail() {
        firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
            setToastMessage(R.string.email_resent)
        }?.addOnFailureListener {
            when (it) {
                is FirebaseNetworkException -> setToastMessage(R.string.no_internet_error)
                else -> setToastMessage(R.string.unexpected_error)
            }
        }
    }

    fun navigateToLogin() {
        firebaseAuth.signOut()
        navigate(EmailResendFragmentDirections.goToLogin())
    }
}