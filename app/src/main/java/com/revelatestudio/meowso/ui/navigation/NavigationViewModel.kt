package com.revelatestudio.meowso.ui.navigation


import androidx.lifecycle.ViewModel
import com.revelatestudio.meowso.data.repository.AppRepository
import com.revelatestudio.meowso.ui.auth.signin.SignInActivity
import com.revelatestudio.meowso.util.navigateToActivity

class NavigationViewModel(private val repository: AppRepository) : ViewModel() {

    fun logout(activity: NavigationActivity) {
        repository.logout {
            activity.finish()
            activity.navigateToActivity(
                origin = activity,
                destination = SignInActivity::class.java
            )
        }
    }
}