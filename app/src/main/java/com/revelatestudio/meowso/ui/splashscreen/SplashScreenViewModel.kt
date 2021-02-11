package com.revelatestudio.meowso.ui.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.data.repository.AppRepository

class SplashScreenViewModel(private val repository: AppRepository) : ViewModel() {

    private val _loggedInUserData = MutableLiveData<LoggedInUser>()
    val loggedInUser : LiveData<LoggedInUser> = _loggedInUserData

    fun getLoggedInUserData(uid: String) {
        repository.getLoggedInUserData(uid) { userData ->
            _loggedInUserData.postValue(userData)
        }
    }
}