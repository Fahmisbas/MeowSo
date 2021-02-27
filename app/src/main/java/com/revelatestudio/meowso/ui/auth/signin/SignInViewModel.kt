package com.revelatestudio.meowso.ui.auth.signin

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.AuthResult
import com.revelatestudio.meowso.data.dataholder.auth.SignInSignUpFormState
import com.revelatestudio.meowso.data.repository.AppRepository

class SignInViewModel(private val repository: AppRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<SignInSignUpFormState>()
    val signInSignUpFormState: LiveData<SignInSignUpFormState> = _loginForm

    private val _loginResult = MutableLiveData<AuthResult>()
    val loginResult: LiveData<AuthResult> = _loginResult

    fun login(activity: SignInActivity, email: String, password: String) {
        repository.login(activity, email, password) { uid ->
            if (uid != null) {
                setLoginResult(uid)
            } else setLoginResult(null)
        }
    }

    private fun setLoginResult(uid: String?) {
        if (uid != null) {
            _loginResult.value = AuthResult(success = uid)
        } else {
            _loginResult.value = AuthResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(email: String, password: String) {
        if (!isEmailValid(email)) {
            _loginForm.value = SignInSignUpFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = SignInSignUpFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = SignInSignUpFormState(isDataValid = true)
        }
    }

    // A placeholder email validation check
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun checkIsUserLoggedIn() {
        repository.checkIsUserLoggedIn() { currentUser ->
            if (currentUser != null) {
                setLoginResult(currentUser.uid)
            }
        }
    }
}