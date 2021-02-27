package com.revelatestudio.meowso.ui.splashscreen


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.data.dataholder.auth.UserStatus
import com.revelatestudio.meowso.databinding.ActivitySplashScreenBinding
import com.revelatestudio.meowso.ui.ViewModelFactory
import com.revelatestudio.meowso.ui.auth.signup.signupfinalstep.setusername.ProfileImageUsernameActivity
import com.revelatestudio.meowso.ui.auth.signup.signupfinalstep.setusername.ProfileImageUsernameActivity.Companion.EXTRA_LOGGED_IN_NEW_USER_PROFILE
import com.revelatestudio.meowso.ui.navigation.NavigationActivity
import com.revelatestudio.meowso.ui.navigation.NavigationActivity.Companion.EXTRA_LOGGED_IN_EXISTED_USER_PROFILE
import com.revelatestudio.meowso.util.showToast

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var splashScreenViewModel: SplashScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        loadData()

    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance()
        splashScreenViewModel = ViewModelProvider(this, factory)[SplashScreenViewModel::class.java]
    }

    private fun loadData() {
        val userStatus = intent.getSerializableExtra(EXTRA_USER_STATUS)
        val userUid = intent.getStringExtra(EXTRA_USER_UID)
        if (userUid != null) {
            splashScreenViewModel.getLoggedInUserData(userUid).observe(this, { userData ->
                if (userData != null) {
                    when (userStatus) {
                        UserStatus.NEW_USER -> navigateToProfileUsernameActivity(userData)
                        UserStatus.EXISTED_USER -> navigateToNavigationActivity(userData)
                    }
                } else showToast(resources.getString(R.string.something_went_wrong))

                finish()
            })
        }
    }

    private fun navigateToProfileUsernameActivity(userData: LoggedInUser) {
        Intent(this@SplashScreenActivity, ProfileImageUsernameActivity::class.java).apply {
            putExtra(EXTRA_LOGGED_IN_NEW_USER_PROFILE, userData)
            startActivity(this)
        }
    }

    private fun navigateToNavigationActivity(userData: LoggedInUser) {
        Intent(this@SplashScreenActivity, NavigationActivity::class.java).apply {
            putExtra(EXTRA_LOGGED_IN_EXISTED_USER_PROFILE, userData)
            startActivity(this)
        }
    }

    companion object {
        const val EXTRA_USER_UID = "extra_user_uid"
        const val EXTRA_USER_STATUS = "extra_user_status"
    }

}