package com.revelatestudio.meowso.ui.auth.signup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.databinding.ActivitySignUpBinding
import com.revelatestudio.meowso.ui.ViewModelFactory
import com.revelatestudio.meowso.ui.auth.signup.signupfinalstep.SignUpFinalStepActivity
import com.revelatestudio.meowso.ui.auth.signup.signupfinalstep.SignUpFinalStepActivity.Companion.EXTRA_USER_EMAIL
import com.revelatestudio.meowso.util.afterTextChanged
import com.revelatestudio.meowso.util.gone
import com.revelatestudio.meowso.util.showToast

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()

        //View events
        with(binding) {
            email.afterTextChanged {
                signUpViewModel.signUpDataChanged(
                    email.text.toString()
                )
            }
            btnNext.setOnClickListener {
                loading.visibility = View.VISIBLE
                signUpViewModel.checkEmailAvailability(
                    email.text.toString()
                )
            }
        }
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance()
        signUpViewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]

    }

    override fun onStart() {
        super.onStart()
        observeSignUpFormState()
        observeEmailAvailability()
    }


    private fun observeEmailAvailability() {
        signUpViewModel.isEmailAvailable.observe(this, { result ->
            if (result != null) {
                when (result.isAvailable) {
                    true -> navigateToSignUpUserDetail(result.id)
                    false -> binding.email.error = getString(R.string.email_unavailable)
                    null -> binding.email.error = getString(R.string.invalid_email)
                }
            } else showToast(resources.getString(R.string.something_went_wrong))
            binding.loading.gone()
        })
    }


    private fun observeSignUpFormState() {
        signUpViewModel.signUpFormState.observe(this@SignUpActivity, Observer { state ->
            val loginState = state ?: return@Observer
            with(binding) {
                // disable login button unless email is valid
                btnNext.isEnabled = loginState.isDataValid
                if (loginState.emailError != null) {
                    email.error = getString(loginState.emailError)
                }
            }
        })
    }

    private fun navigateToSignUpUserDetail(email: String?) {
        Intent(this, SignUpFinalStepActivity::class.java).apply {
            putExtra(EXTRA_USER_EMAIL, email)
            startActivity(this)
        }
        finish()
        setResult(Activity.RESULT_OK)
    }
}