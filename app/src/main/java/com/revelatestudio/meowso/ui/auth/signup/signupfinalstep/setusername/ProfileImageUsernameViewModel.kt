package com.revelatestudio.meowso.ui.auth.signup.signupfinalstep.setusername

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.ProfileDetailFormState
import com.revelatestudio.meowso.data.dataholder.auth.UserIdAvailability
import com.revelatestudio.meowso.data.repository.AppRepository
import java.util.regex.Pattern

class ProfileImageUsernameViewModel(private val repository: AppRepository) : ViewModel() {

    private val _isUploadImageSuccessful = MutableLiveData<Boolean>()
    val isUploadImageSuccessful: LiveData<Boolean> = _isUploadImageSuccessful

    private val _isUserAuthProfileUpdated = MutableLiveData<Boolean>()
    val isUserAuthProfileUpdated: LiveData<Boolean> = _isUserAuthProfileUpdated

    private val _isUsernameAvailable = MutableLiveData<UserIdAvailability?>()
    val isUsernameAvailable: LiveData<UserIdAvailability?> = _isUsernameAvailable

    private val _profileDetailFormState = MutableLiveData<ProfileDetailFormState>()
    val profileDetailFormState: LiveData<ProfileDetailFormState> = _profileDetailFormState

    private var uid: String? = null


    fun setUserUid(uid: String) {
        this.uid = uid
    }

    fun usernameDataChanged(username: String) {
        if (!isUsernameValid(username)) {
            _profileDetailFormState.value =
                ProfileDetailFormState(username = R.string.invalid_username)
        } else {
            _profileDetailFormState.value = ProfileDetailFormState(isDataValid = true)
        }
    }

    fun uploadPhoto(photoUri: String, category: String, fileName: String) {
        repository.uploadPhoto(photoUri, category, fileName, successCallback = { isSuccessful ->
            _isUploadImageSuccessful.value = isSuccessful == true
        }, downloadUrlCallback = { downloadUrl ->
            if (downloadUrl != null) {
                updatePhotoUrl(downloadUrl)
            }
        })
    }


    fun updateUsername(userName: String?) {
        if (userName != null) {
            uid?.let {
                repository.updateUserName(it, userName) { isSuccessful ->
                    _isUserAuthProfileUpdated.value = isSuccessful == true
                }
            }
        }
    }

    private fun updatePhotoUrl(photoUrl: String) {
        uid?.let {
            repository.setUserAuthProfile(null, photoUrl) {}
            repository.updatePhotoUrl(it, photoUrl) { isSuccessful ->
                _isUserAuthProfileUpdated.value = isSuccessful == true
            }
        }
    }

    fun checkUsernameAvailability(username: String) {
        repository.checkUsernameAvailability(username, isAvailableCallback = { isAvailable ->
            if (isAvailable) {
                _isUsernameAvailable.value = UserIdAvailability(id = username, isAvailable = true)
            } else _isUsernameAvailable.value = UserIdAvailability(null, isAvailable = false)
        }, onError = { isError ->
            if (isError == true) {
                _isUsernameAvailable.value = UserIdAvailability(null, isAvailable = null)
            }
        })
    }

    private fun isUsernameValid(username: String): Boolean {
        val regex = "^[A-Za-z]\\w{5,29}$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(username)
        return matcher.matches()
    }
}