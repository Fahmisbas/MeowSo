package com.revelatestudio.meowso.ui.profile

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.revelatestudio.meowso.data.repository.AppRepository
import com.revelatestudio.meowso.ui.auth.signin.SignInActivity
import com.revelatestudio.meowso.util.navigateToActivity

class ProfileViewModel(private val repository: AppRepository) : ViewModel() {

    fun signOut(auth: FirebaseAuth, activity: FragmentActivity) {
        auth.signOut()
        activity.finish()
        activity.navigateToActivity(
            origin = activity,
            destination = SignInActivity::class.java
        )
    }
}