package com.revelatestudio.meowso.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.revelatestudio.meowso.databinding.ActivityMainBinding
import com.revelatestudio.meowso.ui.auth.signin.AuthActivity
import com.revelatestudio.meowso.util.navigateToActivity
import com.revelatestudio.meowso.util.showToast

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logout.setOnClickListener {
            Firebase.auth.signOut()
            finish()
            showToast("you are logged out")
        }

        binding.name.text = user?.displayName

        Glide.with(this).load(user?.photoUrl).into(binding.ivProfile)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        Firebase.auth.signOut()
        navigateToActivity(this, AuthActivity::class.java)
        finish()
        showToast("you are logged out")
    }
}