package com.revelatestudio.meowso.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
    }


    companion object {
        @Volatile
        private var instance : HomeFragment? = null

        fun newInstance() =
            instance ?: synchronized(this) {
                instance ?: HomeFragment()
            }
    }
}