package com.revelatestudio.meowso.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.databinding.FragmentProfileBinding
import com.revelatestudio.meowso.ui.ViewModelFactory


class ProfileFragment(private val loggedInUser: LoggedInUser) :
    Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var auth = FirebaseAuth.getInstance()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        displayUserInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        initToolbar()
        toolbarOptionMenu()
    }

    private fun initToolbar() {
        with(binding) {
            toolbarProfile.title = requireActivity().resources.getString(R.string.app_name)
            tvDisplayName.text = loggedInUser.displayName
        }
    }

    private fun toolbarOptionMenu() {
        with(binding) {
            toolbarProfile.inflateMenu(R.menu.menu_profile_toolbar)
            toolbarProfile.setOnMenuItemClickListener { menu ->
                if (menu.itemId == R.id.opt_logout) {
                    // Sign out and Navigate to SignInActivity
                    profileViewModel.signOut(auth, requireActivity())
                }
                true
            }
        }
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance()
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun displayUserInfo() {
        with(binding) {
            loggedInUser.apply {
                Glide.with(this@ProfileFragment)
                    .load(photoUrl)
                    .into(ivProfilePicture)
                tvDisplayName.text = displayName
                tvFollowingCount.text = followersCount
                tvFollowersCount.text = followersCount
                tvPostsCount.text = postsCount
            }
        }
    }


    companion object {
        @Volatile
        private var instance: ProfileFragment? = null

        fun newInstance(loggedInUser: LoggedInUser) =
            instance ?: synchronized(this) {
                instance ?: ProfileFragment(loggedInUser)
            }
    }
}