package com.revelatestudio.meowso.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.revelatestudio.meowso.databinding.ActivitySplashScreenBinding
import com.revelatestudio.meowso.ui.ViewModelFactory
import com.revelatestudio.meowso.ui.navigation.NavigationActivity
import com.revelatestudio.meowso.ui.navigation.NavigationActivity.Companion.EXTRA_LOGGED_IN_PROFILE

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var splashScreenViewModel: SplashScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        navigateToNavigationActivity()

    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance()
        splashScreenViewModel = ViewModelProvider(this, factory)[SplashScreenViewModel::class.java]
    }

    private fun navigateToNavigationActivity() {
        val uid = intent.getStringExtra(EXTRA_USER_UID)
        if (uid != null) {
            splashScreenViewModel.getLoggedInUserData(uid).observe(this, { data ->
                if (data != null) {
                    Intent(this@SplashScreenActivity, NavigationActivity::class.java).apply {
                        putExtra(EXTRA_LOGGED_IN_PROFILE, data)
                        startActivity(this)
                    }
                }
                finish()
            })
        }
    }

    companion object {
        const val EXTRA_USER_UID = "extra_user_uid"
    }

}