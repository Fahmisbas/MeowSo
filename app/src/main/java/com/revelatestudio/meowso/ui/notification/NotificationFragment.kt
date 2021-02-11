package com.revelatestudio.meowso.ui.notification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment(R.layout.fragment_notification) {

    private lateinit var binding: FragmentNotificationBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationBinding.bind(view)
    }

    companion object {
        @Volatile
        private var instance : NotificationFragment? = null
        fun newInstance() =
            instance ?: synchronized(this) {
                instance ?: NotificationFragment()
            }
    }
}