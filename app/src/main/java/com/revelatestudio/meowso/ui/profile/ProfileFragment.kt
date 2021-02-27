package com.revelatestudio.meowso.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.databinding.FragmentProfileBinding
import com.revelatestudio.meowso.ui.ViewModelFactory

class ProfileFragment() : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var loggedInUser: LoggedInUser? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        if (arguments != null) {
            loggedInUser = arguments?.getParcelable(LOGGED_IN_USER_DATA) as LoggedInUser?
            if (loggedInUser != null) {
                initViewModel()
                displayUserInfo()
                if (loggedInUser?.photoUrl == null) {
                    Glide.with(this).load(R.drawable.dummycat2).into(binding.ivProfilePicture)
                }
            }
        }
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance()
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun displayUserInfo() {
        with(binding) {
            loggedInUser?.apply {
                Glide.with(this@ProfileFragment).load(photoUrl).into(ivProfilePicture)
                tvDisplayName.text = displayName
                tvFollowingCount.text = followersCount
                tvFollowersCount.text = followersCount
                tvPostsCount.text = postsCount
            }
        }
    }

    companion object {
        private const val LOGGED_IN_USER_DATA = "logged_in_user_data"

        fun newInstance(loggedInUser: LoggedInUser): ProfileFragment {
            val fragment = ProfileFragment()
            val bundle = Bundle()
            bundle.putParcelable(LOGGED_IN_USER_DATA, loggedInUser)
            fragment.arguments = bundle
            return fragment
        }
    }
}