package com.revelatestudio.meowso.ui.explore

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.databinding.FragmentExploreBinding

class ExploreFragment : Fragment(R.layout.fragment_explore) {

    private lateinit var binding: FragmentExploreBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExploreBinding.bind(view)

    }

    companion object {

        fun newInstance() = ExploreFragment()
    }
}