package com.revelatestudio.meowso.ui.auth.signup.userdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.AuthResult
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.data.dataholder.auth.LoginFormState
import com.revelatestudio.meowso.data.repository.AppRepository

class SignUpFinalStepViewModel(private val repository: AppRepository)  : ViewModel() {

    private val _signUpForm = MutableLiveData<LoginFormState>()
    val signUpFormState: LiveData<LoginFormState> = _signUpForm

    private val _signUpResult = MutableLiveData<AuthResult>()
    val signUpResult: LiveData<AuthResult> = _signUpResult


    fun createAccount(activity: SignUpFinalStepActivity, catName: String, email: String, password: String) {
        repository.createAccount(activity, email, password) { currentUser ->
            if (currentUser != null) {
                setUserAuthProfile(currentUser, catName)
            }
        }
    }

    // set default value for user
    private fun setUserAuthProfile(currentUser: FirebaseUser, catName: String) {
        repository.setUserAuthProfile(currentUser, catName) { data ->
            setSignUpResult(data)
        }
    }

    private fun setSignUpResult(loggedInUser: LoggedInUser?) {
        if (loggedInUser?.uid != null) {
            repository.storeSignUpUserData(loggedInUser) { isSuccessful ->
                if (isSuccessful) {
                    loggedInUser.apply { _signUpResult.value = AuthResult(success = this) }
                } else {
                    _signUpResult.value = AuthResult(error = R.string.login_failed)
                }
            }
        }
    }


    fun signUpDataChanged(password: String, confirmedPassword: String) {
        if (!isPasswordValid(password)) {
            _signUpForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else if (!isPasswordConfirmed(password, confirmedPassword)) {
            _signUpForm.value = LoginFormState(confirmationPasswordError = R.string.invalid_confirmation_password)
        } else {
            _signUpForm.value = LoginFormState(isDataValid = true)
        }
    }


    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isPasswordConfirmed(password: String, confirmedPassword: String): Boolean {
        return password == confirmedPassword
    }


}