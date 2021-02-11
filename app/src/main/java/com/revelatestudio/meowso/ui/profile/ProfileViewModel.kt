package com.revelatestudio.meowso.ui.profile

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.revelatestudio.meowso.data.repository.AppRepository
import com.revelatestudio.meowso.ui.auth.signin.SignInActivity
import com.revelatestudio.meowso.util.navigateToActivity

class ProfileViewModel(private val repository: AppRepository) : ViewModel() {

    fun logout(activity: FragmentActivity) {
        repository.logout {
            activity.finish()
            activity.navigateToActivity(
                origin = activity,
                destination = SignInActivity::class.java
            )
        }
    }
}