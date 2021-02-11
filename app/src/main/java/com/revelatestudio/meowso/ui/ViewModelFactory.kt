package com.revelatestudio.meowso.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.revelatestudio.meowso.data.repository.AppRepository
import com.revelatestudio.meowso.di.Injection
import com.revelatestudio.meowso.ui.auth.signin.SignInViewModel
import com.revelatestudio.meowso.ui.auth.signup.SignUpViewModel
import com.revelatestudio.meowso.ui.profile.ProfileViewModel
import com.revelatestudio.meowso.ui.splashscreen.SplashScreenViewModel

class ViewModelFactory(private val repository: AppRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
                SignInViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SplashScreenViewModel::class.java) -> {
                SplashScreenViewModel(repository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository())
            }
    }

}