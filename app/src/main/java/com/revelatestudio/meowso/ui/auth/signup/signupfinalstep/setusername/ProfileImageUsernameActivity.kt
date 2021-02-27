package com.revelatestudio.meowso.ui.auth.signup.signupfinalstep.setusername

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.revelatestudio.meowso.R
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.data.dataholder.auth.ProfileDetailFormState
import com.revelatestudio.meowso.data.dataholder.auth.UserIdAvailability
import com.revelatestudio.meowso.databinding.ActivitySetUsernameBinding
import com.revelatestudio.meowso.ui.ViewModelFactory
import com.revelatestudio.meowso.ui.navigation.NavigationActivity
import com.revelatestudio.meowso.util.*


class ProfileImageUsernameActivity : AppCompatActivity() {


    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
    private lateinit var binding: ActivitySetUsernameBinding
    private lateinit var profileImageUsernameViewModel: ProfileImageUsernameViewModel


    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(this@ProfileImageUsernameActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newUser = intent.getParcelableExtra<LoggedInUser?>(EXTRA_LOGGED_IN_NEW_USER_PROFILE)
        if (newUser != null) {

            initViewModel(newUser.uid)

            if (newUser.photoUrl == null) {
                loadProfileImage(R.drawable.dummycat)
            }

            binding.edtUsername.afterTextChanged {
                profileImageUsernameViewModel.usernameDataChanged(binding.edtUsername.text.toString())
            }

            observeChanges(newUser)
            profilePictureOnClick(newUser)
            btnApplyOnClick(newUser)
        }
    }

    private fun observeChanges(newUser: LoggedInUser) {
        observe(profileImageUsernameViewModel.profileDetailFormState, ::validateUsername)
        observe(profileImageUsernameViewModel.isUsernameAvailable, ::checkUsernameAvailability)
        observe(profileImageUsernameViewModel.isUploadImageSuccessful, ::imageUploadIsSuccess)
        observeIsUserProfileUpdated(newUser)

    }

    private fun imageUploadIsSuccess(isSuccessful: Boolean) {
        if (isSuccessful) {
            showToast("Profile Image is Updated")
        } else showToast(resources.getString(R.string.something_went_wrong))
        binding.load.gone()
    }

    private fun checkUsernameAvailability(result: UserIdAvailability?) {
        if (result != null) {
            when (result.isAvailable) {
                true -> profileImageUsernameViewModel.updateUsername(result.id)
                false -> binding.edtUsername.error =
                    resources.getString(R.string.username_unavailable)
                null -> showToast(resources.getString(R.string.something_went_wrong))
            }
        }
    }

    private fun validateUsername(state: ProfileDetailFormState) {
        val formState = state ?: return
        with(binding) {
            btnApply.isEnabled = formState.isDataValid
            if (formState.username != null) {
                edtUsername.error = getString(formState.username)
            }
        }
    }

    private fun btnApplyOnClick(newUser: LoggedInUser) {
        binding.btnApply.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            profileImageUsernameViewModel.checkUsernameAvailability(username)
            newUser.photoUrl?.let { uri ->
                profileImageUsernameViewModel.uploadPhoto(
                    uri,
                    StorageCategory.PROFILE_PICTURE,
                    newUser.uid + "_" + "MeowSo"
                )
            }
        }
    }

    private fun profilePictureOnClick(newUser: LoggedInUser) {
        binding.ivProfilePicture.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }
        setCroppedImageUri(newUser)
    }

    private fun observeIsUserProfileUpdated(newUser: LoggedInUser) {
        profileImageUsernameViewModel.isUserAuthProfileUpdated.observe(this, { isUpdated ->
            if (isUpdated) {
                navigateToNavigationActivity(newUser)
            }
        })
    }


    private fun setCroppedImageUri(newUser: LoggedInUser) {
        cropActivityResultLauncher =
            registerForActivityResult(cropActivityResultContract) { croppedImageUri ->
                if (croppedImageUri != null) {
                    loadProfileImage(croppedImageUri)
                    newUser.photoUrl = croppedImageUri.toString()
                } else newUser.photoUrl = null
            }
    }

    private fun initViewModel(uid: String) {
        val factory = ViewModelFactory.getInstance()
        profileImageUsernameViewModel =
            ViewModelProvider(this, factory)[ProfileImageUsernameViewModel::class.java]
        profileImageUsernameViewModel.setUserUid(uid)
    }

    private fun navigateToNavigationActivity(newUser: LoggedInUser) {
        Intent(this, NavigationActivity::class.java).apply {
            putExtra(NavigationActivity.EXTRA_LOGGED_IN_EXISTED_USER_PROFILE, newUser)
            startActivity(this)
        }
    }

    private fun loadProfileImage(image: Any?) {
        binding.ivProfilePicture.loadImage(image, getProgressDrawable(this))

    }

    companion object {
        const val EXTRA_LOGGED_IN_NEW_USER_PROFILE = "extra_new_user_profile"
    }
}