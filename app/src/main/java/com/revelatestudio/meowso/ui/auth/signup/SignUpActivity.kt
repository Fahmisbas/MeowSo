package com.revelatestudio.meowso.ui.auth.signup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.databinding.ActivitySignUpBinding
import com.revelatestudio.meowso.ui.ViewModelFactory
import com.revelatestudio.meowso.ui.splashscreen.SplashScreenActivity
import com.revelatestudio.meowso.util.afterTextChanged
import com.revelatestudio.meowso.util.showToast

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding initialization
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel initialization
        val factory = ViewModelFactory.getInstance()
        signUpViewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]


        //View events
        with(binding) {
            email.afterTextChanged {
                signUpViewModel.signUpDataChanged(
                    email.text.toString(),
                    password.text.toString(),
                    confirmedPassword.text.toString()
                )

            }

            password.apply {
                password.afterTextChanged {
                    signUpViewModel.signUpDataChanged(
                        email.text.toString(),
                        password.text.toString(),
                        confirmedPassword.text.toString()
                    )
                }

                confirmedPassword.afterTextChanged {
                    signUpViewModel.signUpDataChanged(
                        email.text.toString(),
                        password.text.toString(),
                        confirmedPassword.text.toString()
                    )
                }

                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            signUpViewModel.createAccount(
                                this@SignUpActivity,
                                catName.text.toString(),
                                email.text.toString(),
                                password.text.toString()
                            )
                    }
                    false
                }

                signUp.setOnClickListener {
                    loading.visibility = View.VISIBLE
                    signUpViewModel.createAccount(
                        this@SignUpActivity,
                        catName.text.toString(),
                        email.text.toString(),
                        password.text.toString()
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        observeSignUpFormState()
        observeSignUpResult()
    }

    private fun observeSignUpFormState() {
        signUpViewModel.signUpFormState.observe(this@SignUpActivity, Observer { state ->
            val loginState = state ?: return@Observer

            with(binding) {
                // disable login button unless both username / password is valid
                signUp.isEnabled = loginState.isDataValid

                if (loginState.usernameError != null) {
                    email.error = getString(loginState.usernameError)
                }
                if (loginState.passwordError != null) {
                    password.error = getString(loginState.passwordError)
                }
                if (loginState.confirmationPasswordError != null) {
                    confirmedPassword.error = getString(loginState.confirmationPasswordError)
                }
            }
        })
    }


    private fun observeSignUpResult() {
        signUpViewModel.signUpResult.observe(this@SignUpActivity, Observer { result ->
            val loginResult = result ?: return@Observer

            binding.loading.visibility = View.GONE

            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }

            if (loginResult.success != null) {

                navigateToHomeActivity(loginResult.success)
                setResult(RESULT_OK)
                //Complete and destroy login activity once successful
                finish()
            }
        })
    }


    private fun showLoginFailed(@StringRes errorString: Int) {
        showToast(resources.getString(errorString))
    }

    private fun navigateToHomeActivity(loggedInUser: LoggedInUser) {
        val displayName = loggedInUser.displayName
        showToast("welcome $displayName")
        Intent(this, SplashScreenActivity::class.java).apply {
            putExtra(SplashScreenActivity.EXTRA_USER_UID, loggedInUser.uid)
            startActivity(this)
        }
        setResult(Activity.RESULT_OK)
        finish()

    }
}