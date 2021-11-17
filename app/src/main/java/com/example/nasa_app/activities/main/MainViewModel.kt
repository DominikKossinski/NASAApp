package com.example.nasa_app.activities.main

import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.utils.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(preferencesHelper: PreferencesHelper) :
    BaseViewModel(preferencesHelper) {
}