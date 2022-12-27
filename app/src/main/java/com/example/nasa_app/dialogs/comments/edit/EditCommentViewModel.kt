package com.example.nasa_app.dialogs.comments.edit

import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditCommentViewModel @Inject constructor(
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) :
    BaseViewModel(preferencesHelper, appDatabase) {

}
