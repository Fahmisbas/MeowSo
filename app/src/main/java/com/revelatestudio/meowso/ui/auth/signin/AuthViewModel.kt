package com.revelatestudio.meowso.ui.auth.signin

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.AuthResult
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.data.dataholder.auth.LoginFormState
import com.revelatestudio.meowso.data.repository.AppRepository

class AuthViewModel(private val repository: AppRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _loginResult

    fun setLoggedInUser(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            repository.setLoggedInUser(currentUser) {
                setLoginResult(it)
            }
        }
    }

    fun setLoginResult(loggedInUser: LoggedInUser?) {
        if (loggedInUser != null) {
            _loginResult.value = AuthResult(success = loggedInUser)
        } else {
            _loginResult.value = AuthResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}