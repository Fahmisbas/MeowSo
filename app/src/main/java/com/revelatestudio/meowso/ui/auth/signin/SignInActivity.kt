package com.revelatestudio.meowso.ui.auth.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.databinding.ActivityAuthBinding
import com.revelatestudio.meowso.ui.ViewModelFactory
import com.revelatestudio.meowso.ui.auth.signup.SignUpActivity
import com.revelatestudio.meowso.ui.splashscreen.SplashScreenActivity
import com.revelatestudio.meowso.ui.splashscreen.SplashScreenActivity.Companion.EXTRA_USER_UID
import com.revelatestudio.meowso.util.afterTextChanged
import com.revelatestudio.meowso.util.navigateToActivity
import com.revelatestudio.meowso.util.showToast


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var loginViewModel: SignInViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFirebaseAuth()
        initViewModel()
        observeLoginFormState()
        observeLoginResult()

        with(binding) {
            email.afterTextChanged {
                loginViewModel.loginDataChanged(
                    email.text.toString(),
                    password.text.toString()
                )
            }

            password.apply {
                afterTextChanged {
                    loginViewModel.loginDataChanged(
                        email.text.toString(),
                        password.text.toString()
                    )
                }

                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            login(
                                email.text.toString(),
                                password.text.toString()
                            )
                    }
                    false
                }

                signIn.setOnClickListener {
                    loading.visibility = View.VISIBLE
                    login(
                        email.text.toString(),
                        password.text.toString()
                    )
                }
            }
            signUp.setOnClickListener {
                navigateToActivity(this@SignInActivity, SignUpActivity::class.java)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        // check if the user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            loginViewModel.setLoggedInUser(currentUser)
        }
    }


    private fun initFirebaseAuth() {
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance()
        loginViewModel = ViewModelProvider(this, factory)[SignInViewModel::class.java]
    }

    private fun observeLoginFormState() {
        loginViewModel.loginFormState.observe(this@SignInActivity, Observer { state ->
            val loginState = state ?: return@Observer

            with(binding) {
                // disable login button unless both username / password is valid
                signIn.isEnabled = loginState.isDataValid

                if (loginState.usernameError != null) {
                    email.error = getString(loginState.usernameError)
                }
                if (loginState.passwordError != null) {
                    password.error = getString(loginState.passwordError)
                }
            }
        })
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser
                loginViewModel.setLoggedInUser(currentUser)
            } else {
                loginViewModel.setLoggedInUser(null)
            }
        }
    }

    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(this@SignInActivity, Observer { result ->
            val loginResult = result ?: return@Observer

            binding.loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                navigateToHomeActivity(loginResult.success)

                setResult(Activity.RESULT_OK)
                //Complete and destroy login activity once successful
                finish()
            }
        })
    }

    private fun navigateToHomeActivity(loggedInUser: LoggedInUser) {
        val displayName = loggedInUser.displayName
        showToast("welcome $displayName")

        Intent(this, SplashScreenActivity::class.java).apply {
            putExtra(EXTRA_USER_UID, loggedInUser.uid)
            startActivity(this)
        }
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        showToast(resources.getString(errorString))
    }
}
