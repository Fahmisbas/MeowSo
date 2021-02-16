package com.revelatestudio.meowso.ui.auth.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.LoginFormState
import com.revelatestudio.meowso.data.dataholder.auth.UserIdAvailability
import com.revelatestudio.meowso.data.repository.AppRepository

class SignUpViewModel(private val repository: AppRepository) : ViewModel() {

    private val _signUpForm = MutableLiveData<LoginFormState>()
    val signUpFormState: LiveData<LoginFormState> = _signUpForm

    private val _isEmailAvailable = MutableLiveData<UserIdAvailability?>()
    val isEmailAvailable = _isEmailAvailable

    fun signUpDataChanged(email: String) {
        if (!isEmailValid(email)) {
            _signUpForm.value = LoginFormState(usernameError = R.string.invalid_email)
        } else {
            _signUpForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder email validation check
    private fun isEmailValid(email: String): Boolean {
        return if (email.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }

    fun checkEmailAvailability(email: String) {
        repository.checkEmailAvailability(email, isEmailAvailable = { isAvailable ->
            if(isAvailable) {
                _isEmailAvailable.value = UserIdAvailability(id = email, isAvailable = true)
            } else if (!isAvailable){
                _isEmailAvailable.value = UserIdAvailability(id = null, isAvailable = false)
            } else {
                _isEmailAvailable.value = null
            }
        }, onError = {
            _isEmailAvailable.value = null
        })
    }
}