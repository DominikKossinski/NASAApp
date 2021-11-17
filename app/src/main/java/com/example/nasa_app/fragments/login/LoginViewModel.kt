package com.example.nasa_app.fragments.login

import dagger.hilt.android.lifecycle.HiltViewModel
import pl.kossa.myflights.architecture.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {

    fun navigateToCreateAccount() {
        navigate(LoginFragmentDirections.goToCreateAccount())
    }
}