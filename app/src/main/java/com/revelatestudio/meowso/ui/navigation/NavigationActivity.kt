package com.revelatestudio.meowso.ui.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.databinding.ActivityNavigationBinding
import com.revelatestudio.meowso.ui.explore.ExploreFragment
import com.revelatestudio.meowso.ui.home.HomeFragment
import com.revelatestudio.meowso.ui.notification.NotificationFragment
import com.revelatestudio.meowso.ui.profile.ProfileFragment


class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentFragment(HomeFragment.newInstance())

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
                        setCurrentFragment(ProfileFragment.newInstance())
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }
}