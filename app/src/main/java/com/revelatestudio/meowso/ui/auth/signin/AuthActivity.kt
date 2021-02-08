package com.revelatestudio.meowso.ui.auth.signin

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUserView
import com.revelatestudio.meowso.databinding.ActivityAuthBinding
import com.revelatestudio.meowso.ui.ViewModelFactory
import com.revelatestudio.meowso.ui.auth.signup.SignUpActivity
import com.revelatestudio.meowso.ui.home.HomeActivity
import com.revelatestudio.meowso.util.afterTextChanged
import com.revelatestudio.meowso.util.navigateToActivity
import com.revelatestudio.meowso.util.showToast


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var loginViewModel: AuthViewModel
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
                navigateToActivity(this@AuthActivity, SignUpActivity::class.java)
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
        val firebaseDb = FirebaseFirestore.getInstance()
        val factory = ViewModelFactory.getInstance(firebaseDb)
        loginViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun observeLoginFormState() {
        loginViewModel.loginFormState.observe(this@AuthActivity, Observer { state ->
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
                loginViewModel.setLoginResult(null)
            }
        }
    }

    private fun observeLoginResult() {
        loginViewModel.authResult.observe(this@AuthActivity, Observer { result ->
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

    private fun navigateToHomeActivity(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        showToast("$welcome $displayName")
        navigateToActivity(this, HomeActivity::class.java)

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        showToast(resources.getString(errorString))
    }
}
