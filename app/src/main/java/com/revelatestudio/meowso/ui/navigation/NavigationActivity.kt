package com.revelatestudio.meowso.ui.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.databinding.ActivityNavigationBinding
import com.revelatestudio.meowso.ui.ViewModelFactory
import com.revelatestudio.meowso.ui.explore.ExploreFragment
import com.revelatestudio.meowso.ui.home.HomeFragment
import com.revelatestudio.meowso.ui.notification.NotificationFragment
import com.revelatestudio.meowso.ui.profile.ProfileFragment
import com.revelatestudio.meowso.util.showToast


class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding
    private lateinit var navigationViewModel: NavigationViewModel
    private var loggedInUser: LoggedInUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loggedInUser = intent.getParcelableExtra(EXTRA_LOGGED_IN_EXISTED_USER_PROFILE)
        if (loggedInUser == null) {
            showToast(resources.getString(R.string.something_went_wrong))
            finish()
        } else {
            setCurrentFragment(HomeFragment.newInstance())
            initViewModel()

            onNavigationItemSelected()
            onToolbarMenuItemSelected()
        }
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance()
        navigationViewModel = ViewModelProvider(this, factory)[NavigationViewModel::class.java]
    }

    private fun onToolbarMenuItemSelected() {
        with(binding) {
            toolbarProfile.title = resources.getString(R.string.app_name)
            toolbarProfile.inflateMenu(R.menu.menu_profile_toolbar)
            toolbarProfile.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.opt_logout -> navigationViewModel.logout(this@NavigationActivity)
                }
                true
            }
        }
    }

    private fun onNavigationItemSelected() {
        with(binding) {
            bottomNavigation.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        setCurrentFragment(HomeFragment.newInstance())
                        true
                    }
                    R.id.nav_explore -> {
                        setCurrentFragment(ExploreFragment.newInstance())
                        true
                    }

                    R.id.nav_upload -> {

                        true
                    }

                    R.id.nav_notification -> {
                        setCurrentFragment(NotificationFragment.newInstance())
                        true
                    }

                    R.id.nav_profile -> {
                        val user = loggedInUser
                        if (user != null) {
                            setCurrentFragment(ProfileFragment.newInstance(user))
                        } else {
                            showToast("Failed to load data")
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    companion object {
        const val EXTRA_LOGGED_IN_EXISTED_USER_PROFILE = "logged_in_profile"
    }
}