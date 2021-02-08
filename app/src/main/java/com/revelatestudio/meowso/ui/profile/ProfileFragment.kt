package com.revelatestudio.meowso.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.databinding.FragmentProfileBinding


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
    }

    companion object {

        fun newInstance() =
            ProfileFragment()
    }
}