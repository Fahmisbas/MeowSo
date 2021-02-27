package com.revelatestudio.meowso.ui.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.data.repository.AppRepository

class SplashScreenViewModel(private val repository: AppRepository) : ViewModel() {

    fun getLoggedInUserData(uid: String) : LiveData<LoggedInUser> {
        val loggedInUserData = MutableLiveData<LoggedInUser>()
        repository.getLoggedInUserData(uid) { userData ->
            if (userData != null) {
                loggedInUserData.postValue(userData)
            }
        }
        return loggedInUserData
    }
}