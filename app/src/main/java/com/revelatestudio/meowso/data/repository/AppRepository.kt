package com.revelatestudio.meowso.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.util.getCurrentDateTime
import com.revelatestudio.meowso.util.removeWhiteSpaces
import com.revelatestudio.meowso.util.toStringFormat

class AppRepository(private val firebaseDb: FirebaseFirestore) {

    private val usersCollection = firebaseDb.collection(USERS_COLLECTION)

    fun storeUserInfoIntoFireStoreDB(loggedInUser: LoggedInUser, isSuccessful: (Boolean) -> Unit) {

        loggedInUser.apply {
            val user = hashMapOf(
                KEY_UID to userId,
                KEY_NAME to displayName,
                KEY_USERNAME to userName,
                KEY_EMAIl to email,
                KEY_PHOTO_URL to photoUrl.toString(),
                KEY_CREATED_DATE to getCurrentDateTime().toStringFormat(DATE_FORMAT),
                KEY_PROFILE_DESCRIPTION to "",
            )
            usersCollection.document(userId).set(user)
                .addOnSuccessListener {
                    isSuccessful.invoke(true)
                }
                .addOnFailureListener {
                    isSuccessful.invoke(false)
                }
        }
    }

    fun setDefaultUserProfile(
        currentUser: FirebaseUser,
        catName: String,
        data: (LoggedInUser) -> Unit
    ) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(catName)
            .setPhotoUri(Uri.parse(DEFAULT_PROFILE_PICTURE_URL))
            .build()
        currentUser.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setLoggedInUser(currentUser) { loggedInUser ->
                    data.invoke(loggedInUser)
                }
            }
        }
    }

    fun setLoggedInUser(user: FirebaseUser, data: (LoggedInUser) -> Unit) {
        user.apply {
            val uid = uid
            val displayName = displayName
            val username = displayName?.removeWhiteSpaces()
            val userEmail = email
            val photoUrl = photoUrl
            if (displayName != null && username != null && userEmail != null && photoUrl != null) {
                val loggedInUser = LoggedInUser(uid, displayName, username, userEmail, photoUrl)
                data.invoke(loggedInUser)
            }
        }
    }

    fun getUserProfile(currentUser: FirebaseUser, callback: (Any) -> Unit) {
        val userId = currentUser.uid
        usersCollection.document(userId).addSnapshotListener { value, error ->
            if (value != null) {
                callback.invoke(value)
            }
        }
    }


    companion object {
        private const val USERS_COLLECTION = "users"
        private const val KEY_UID = "uid"
        private const val KEY_NAME = "name"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIl = "email"
        private const val KEY_PHOTO_URL = "photo_url"
        private const val KEY_CREATED_DATE = "created_date"
        private const val KEY_PROFILE_DESCRIPTION = "description"

        private const val default_profile_pic_token = "964cc4b4-682a-4209-900b-ee109f247c6a"
        private val DEFAULT_PROFILE_PICTURE_URL: String
            get() = "https://firebasestorage.googleapis.com/v0/b/meowso-9c708.appspot.com/o/profile%2Fdefault-user-profile-picture.jpg?alt=media&token=$default_profile_pic_token"

        private const val DATE_FORMAT = "dd/MM/yyyy HH:mm:ss"

        @Volatile
        private var instance: AppRepository? = null
        fun getInstance(firebaseDb: FirebaseFirestore) =
            instance ?: synchronized(this) {
                instance ?: AppRepository(firebaseDb)
            }
    }
}