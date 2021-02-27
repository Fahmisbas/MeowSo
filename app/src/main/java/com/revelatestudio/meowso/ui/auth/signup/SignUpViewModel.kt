package com.revelatestudio.meowso.ui.auth.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.SignInSignUpFormState
import com.revelatestudio.meowso.data.dataholder.auth.UserIdAvailability
import com.revelatestudio.meowso.data.repository.AppRepository

class SignUpViewModel(private val repository: AppRepository) : ViewModel() {

    private val _signUpFormState = MutableLiveData<SignInSignUpFormState>()
    val signUpFormState: LiveData<SignInSignUpFormState> = _signUpFormState

    private val _isEmailAvailable = MutableLiveData<UserIdAvailability?>()
    val isEmailAvailable: LiveData<UserIdAvailability?> = _isEmailAvailable

    fun signUpDataChanged(email: String) {
        if (!isEmailValid(email)) {
            _signUpFormState.value = SignInSignUpFormState(emailError = R.string.invalid_email)
        } else {
            _signUpFormState.value = SignInSignUpFormState(isDataValid = true)
        }
    }

    // A placeholder email validation check
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun checkEmailAvailability(email: String) {
        repository.checkEmailAvailability(email, isAvailableCallback = { isAvailable ->
            if (isAvailable != null) {
                if (isAvailable) {
                    _isEmailAvailable.value = UserIdAvailability(id = email, isAvailable = true)
                } else if (!isAvailable) {
                    _isEmailAvailable.value = UserIdAvailability(id = null, isAvailable = false)
                } else {
                    _isEmailAvailable.value = UserIdAvailability(id = null, isAvailable = null)
                }
            }
        }, onError = {
            _isEmailAvailable.value = null
        })
    }
}