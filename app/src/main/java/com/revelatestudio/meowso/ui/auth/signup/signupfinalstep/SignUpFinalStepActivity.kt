package com.revelatestudio.meowso.ui.auth.signup.signupfinalstep

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.UserStatus
import com.revelatestudio.meowso.databinding.ActivitySignUpFinalStepBinding
import com.revelatestudio.meowso.ui.ViewModelFactory
import com.revelatestudio.meowso.ui.splashscreen.SplashScreenActivity
import com.revelatestudio.meowso.ui.splashscreen.SplashScreenActivity.Companion.EXTRA_USER_STATUS
import com.revelatestudio.meowso.util.afterTextChanged
import com.revelatestudio.meowso.util.gone
import com.revelatestudio.meowso.util.showToast
import com.revelatestudio.meowso.util.visible

class SignUpFinalStepActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpFinalStepBinding
    private lateinit var signUpViewModel: SignUpFinalStepViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpFinalStepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newUserEmail = intent.getStringExtra(EXTRA_USER_EMAIL)

        if (newUserEmail != null) {
            initViewModel()
            with(binding) {
                catName.afterTextChanged {
                    signUpViewModel.signUpDataChanged(
                        password.text.toString(),
                        confirmedPassword.text.toString()
                    )
                }
                password.apply {
                    password.afterTextChanged {
                        signUpViewModel.signUpDataChanged(
                            password.text.toString(),
                            confirmedPassword.text.toString()
                        )
                    }

                    confirmedPassword.afterTextChanged {
                        signUpViewModel.signUpDataChanged(
                            password.text.toString(),
                            confirmedPassword.text.toString()
                        )
                    }

                    setOnEditorActionListener { _, actionId, _ ->
                        when (actionId) {
                            EditorInfo.IME_ACTION_DONE ->
                                signUpViewModel.createAccount(
                                    this@SignUpFinalStepActivity,
                                    catName.text.toString(),
                                    newUserEmail,
                                    password.text.toString()
                                )
                        }
                        false
                    }

                    signUp.setOnClickListener {
                        loading.visible()
                        signUpViewModel.createAccount(
                            this@SignUpFinalStepActivity,
                            catName.text.toString(),
                            newUserEmail,
                            password.text.toString()
                        )
                    }
                }
            }
        } else {
            showToast(resources.getString(R.string.something_went_wrong))
            finish()
        }
    }

    private fun initViewModel() {
        // ViewModel initialization
        val factory = ViewModelFactory.getInstance()
        signUpViewModel = ViewModelProvider(this, factory)[SignUpFinalStepViewModel::class.java]

    }

    override fun onStart() {
        super.onStart()
        observeSignUpFormState()
        observeSignUpResult()
    }

    private fun observeSignUpFormState() {
        signUpViewModel.signUpFormState.observe(this@SignUpFinalStepActivity, Observer { state ->
            val loginState = state ?: return@Observer

            with(binding) {
                // disable login button unless both username / password is valid
                signUp.isEnabled = loginState.isDataValid

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
        signUpViewModel.signUpResult.observe(this@SignUpFinalStepActivity, Observer { result ->
            val loginResult = result ?: return@Observer

            binding.loading.gone()

            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {

                navigateSplashScreenActivity(loginResult.success)
                setResult(RESULT_OK)
                //Complete and destroy login activity once successful
                finish()
            }
        })
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        showToast(resources.getString(errorString))
    }

    private fun navigateSplashScreenActivity(uid: String) {
        Intent(this, SplashScreenActivity::class.java).apply {
            putExtra(SplashScreenActivity.EXTRA_USER_UID, uid)
            putExtra(EXTRA_USER_STATUS, UserStatus.NEW_USER)
            startActivity(this)
        }
    }

    companion object {
        const val EXTRA_USER_EMAIL = "extra_email_user"
    }
}