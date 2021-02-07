package com.revelatestudio.meowso.ui.auth.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.AuthResult
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUserView
import com.revelatestudio.meowso.data.dataholder.auth.LoginFormState
import com.revelatestudio.meowso.data.repository.AppRepository

class SignUpViewModel(private val repository: AppRepository) : ViewModel() {

    private val _signUpForm = MutableLiveData<LoginFormState>()
    val signUpFormState: LiveData<LoginFormState> = _signUpForm

    private val _signUpResult = MutableLiveData<AuthResult>()
    val signUpResult: LiveData<AuthResult> = _signUpResult

    fun setSignUpResult(loggedInUser: LoggedInUser?) {
        if (loggedInUser?.userId != null) {
            repository.storeUserInfoIntoFireStoreDB(loggedInUser) { isSuccessful ->
                if (isSuccessful) {
                    loggedInUser.apply {
                        _signUpResult.value = AuthResult(
                            success = LoggedInUserView(
                                displayName = displayName,
                                email = email,
                                photoUrl = photoUrl
                            )
                        )
                    }
                } else {
                    _signUpResult.value = AuthResult(error = R.string.login_failed)
                }
            }
        }
    }

    fun signUpDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _signUpForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _signUpForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _signUpForm.value = LoginFormState(isDataValid = true)
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