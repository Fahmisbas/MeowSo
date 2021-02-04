package com.revelatestudio.meowso.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUserView
import com.revelatestudio.meowso.databinding.ActivityAuthBinding
import com.revelatestudio.meowso.ui.home.HomeActivity
import com.revelatestudio.meowso.util.afterTextChanged


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

        binding.email.afterTextChanged {
            loginViewModel.loginDataChanged(
                binding.email.text.toString(),
                binding.password.text.toString()
            )
        }

        binding.password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        login(
                            binding.email.text.toString(),
                            binding.password.text.toString()
                        )
                }
                false
            }

            binding.signIn.setOnClickListener {
                binding.loading.visibility = View.VISIBLE
                login(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                )
            }
        }
    }

    private fun initFirebaseAuth() {
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
    }

    private fun initViewModel() {
        loginViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            user.email?.let { email ->
                val loggedInUserView = LoggedInUserView(user.uid)
                navigateToHomeActivity(loggedInUserView)
            }
        }
    }


    private fun observeLoginFormState() {
        loginViewModel.loginFormState.observe(this@AuthActivity, Observer { state ->
            val loginState = state ?: return@Observer

            // disable login button unless both username / password is valid
            binding.signIn.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                binding.email.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                binding.password.error = getString(loginState.passwordError)
            }
        })
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser
                currentUser?.let { user ->
                    user.email?.let {
                        loginViewModel.setLoginResult(LoggedInUser(user.uid, it))
                    }
                }
            } else {
                loginViewModel.setLoginResult(null)
            }
        }
    }
    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(this@AuthActivity, Observer { result ->
            val loginResult = result ?: return@Observer

            binding.loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                navigateToHomeActivity(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

    }


    private fun navigateToHomeActivity(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(applicationContext, "$welcome $displayName", Toast.LENGTH_LONG).show()

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)

        setResult(Activity.RESULT_OK)

        finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show()
    }
}



