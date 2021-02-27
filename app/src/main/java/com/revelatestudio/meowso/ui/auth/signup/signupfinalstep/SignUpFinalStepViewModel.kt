package com.revelatestudio.meowso.ui.auth.signup.signupfinalstep

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.AuthResult
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.data.dataholder.auth.SignInSignUpFormState
import com.revelatestudio.meowso.data.repository.AppRepository
import com.revelatestudio.meowso.util.getCurrentDateTime
import com.revelatestudio.meowso.util.removeWhiteSpace
import com.revelatestudio.meowso.util.toStringFormat

class SignUpFinalStepViewModel(private val repository: AppRepository) : ViewModel() {

    private val _signUpForm = MutableLiveData<SignInSignUpFormState>()
    val signUpFormState: LiveData<SignInSignUpFormState> = _signUpForm

    private val _signUpResult = MutableLiveData<AuthResult>()
    val signUpResult: LiveData<AuthResult> = _signUpResult


    fun createAccount(
        activity: SignUpFinalStepActivity,
        displayName: String,
        email: String,
        password: String
    ) {
        repository.createAccount(activity, email, password) { currentUser ->
            if (currentUser != null) {
                val defaultUsername =
                    displayName.removeWhiteSpace() + "_" + currentUser.uid.substring(0, 5)
                val loggedInUser =
                    LoggedInUser(currentUser.uid, displayName, defaultUsername, email, null)
                setUserAuthProfile(loggedInUser)
            } else setSignUpResult(null)
        }
    }

    // set default value for user
    private fun setUserAuthProfile(loggedInUser: LoggedInUser?) {
        if (loggedInUser != null) {
            val displayName = loggedInUser.displayName
            if (displayName != null)
                repository.setUserAuthProfile(loggedInUser.displayName, null) { isSuccessful ->
                    if (isSuccessful == true) {
                        setSignUpResult(loggedInUser)
                    } else setSignUpResult(null)
                }
        }
    }

    private fun setSignUpResult(loggedInUser: LoggedInUser?) {
        if (loggedInUser?.uid != null) {
            loggedInUser.createdDate = getCurrentDateTime().toStringFormat(DATE_FORMAT)
            repository.storeLoggedInUserData(loggedInUser) { isSuccessful ->
                if (isSuccessful != null && isSuccessful) {
                    loggedInUser.apply { _signUpResult.value = AuthResult(success = uid) }
                } else {
                    _signUpResult.value = AuthResult(error = R.string.login_failed)
                }
            }
        }
    }

    fun signUpDataChanged(password: String, confirmedPassword: String) {
        if (!isPasswordValid(password)) {
            _signUpForm.value = SignInSignUpFormState(passwordError = R.string.invalid_password)
        } else if (!isPasswordConfirmed(password, confirmedPassword)) {
            _signUpForm.value =
                SignInSignUpFormState(confirmationPasswordError = R.string.invalid_confirmation_password)
        } else {
            _signUpForm.value = SignInSignUpFormState(isDataValid = true)
        }
    }


    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isPasswordConfirmed(password: String, confirmedPassword: String): Boolean {
        return password == confirmedPassword
    }

    companion object {
        private const val DATE_FORMAT = "dd/MM/yyyy HH:mm:ss"

        private const val default_profile_pic_token = "964cc4b4-682a-4209-900b-ee109f247c6a"
        private val DEFAULT_PROFILE_PICTURE_URL: String
            get() = "https://firebasestorage.googleapis.com/v0/b/meowso-9c708.appspot.com/o/profile%2Fdefault-user-profile-picture.jpg?alt=media&token=$default_profile_pic_token"
    }


}