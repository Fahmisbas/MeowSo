package com.revelatestudio.meowso.ui.auth.signup

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUserView
import com.revelatestudio.meowso.databinding.ActivitySignUpBinding
import com.revelatestudio.meowso.ui.ViewModelFactory
import com.revelatestudio.meowso.ui.home.HomeActivity
import com.revelatestudio.meowso.util.afterTextChanged
import com.revelatestudio.meowso.util.navigateToActivity
import com.revelatestudio.meowso.util.showToast

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDb = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        observeSignUpFormState()
        observeSignUpResult()

        with(binding) {
            email.afterTextChanged {
                signUpViewModel.signUpDataChanged(
                    email.text.toString(),
                    password.text.toString()
                )
            }

            password.apply {
                password.afterTextChanged {
                    signUpViewModel.signUpDataChanged(
                        email.text.toString(),
                        password.text.toString()
                    )
                }

                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            createAccount(
                                email.text.toString(),
                                password.text.toString()
                            )
                    }
                    false
                }

                signUp.setOnClickListener {
                    loading.visibility = View.VISIBLE
                    createAccount(
                        email.text.toString(),
                        password.text.toString()
                    )
                }
            }
        }
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance(firebaseDb)
        signUpViewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        updateUserProfile(currentUser)
                    }
                } else {
                    signUpViewModel.setSignUpResult(null)
                }
            }
    }

    private fun updateUserProfile(user: FirebaseUser) {
        with(binding) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(catName.text.toString())
                .setPhotoUri(Uri.parse(DEFAULT_PROFILE_PICTURE_URL))
                .build()
            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.apply {
                        val uid = uid
                        val displayName = displayName
                        val userEmail = email
                        val photoUrl = photoUrl
                        if (displayName != null && userEmail != null && photoUrl != null) {
                            val loggedInUser = LoggedInUser(uid, displayName, userEmail, photoUrl)
                            signUpViewModel.setSignUpResult(loggedInUser)
                        }
                    }
                }
            }
        }
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

    private fun navigateToHomeActivity(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName

        showToast("$welcome $displayName")
        navigateToActivity(this, HomeActivity::class.java)

        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object {
        private const val token = "964cc4b4-682a-4209-900b-ee109f247c6a"
        private val DEFAULT_PROFILE_PICTURE_URL: String
            get() = "https://firebasestorage.googleapis.com/v0/b/meowso-9c708.appspot.com/o/profile%2Fdefault-user-profile-picture.jpg?alt=media&token=$token"
    }
}