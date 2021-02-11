package com.revelatestudio.meowso.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.util.getCurrentDateTime
import com.revelatestudio.meowso.util.removeWhiteSpace
import com.revelatestudio.meowso.util.toStringFormat

class AppRepository(private val firebaseDb: FirebaseFirestore) {

    private val usersCollection = firebaseDb.collection(USERS_COLLECTION)


    fun setInitialUserProfile(
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
                setInitialLoggedInUser(currentUser) { loggedInUser ->
                    data.invoke(loggedInUser)
                }
            }
        }
    }

    fun setInitialLoggedInUser(user: FirebaseUser, data: (LoggedInUser) -> Unit) {
        user.apply {
            val displayName = displayName
            val username = displayName?.removeWhiteSpace()
            val userEmail = email
            val photoUrl = photoUrl.toString()
            if (displayName != null && username != null && userEmail != null) {
                val loggedInUser = LoggedInUser(uid, displayName, username, userEmail, photoUrl)
                data.invoke(loggedInUser)
            }
        }
    }

    fun storeInitialLoggedInUser(loggedInUser: LoggedInUser, isSuccessful: (Boolean) -> Unit) {
        loggedInUser.apply {
            val user = hashMapOf(
                KEY_UID to uid,
                KEY_NAME to displayName,
                KEY_USERNAME to userName,
                KEY_EMAIl to email,
                KEY_PHOTO_URL to photoUrl.toString(),
                KEY_CREATED_DATE to getCurrentDateTime().toStringFormat(DATE_FORMAT),
                KEY_PROFILE_DESCRIPTION to profileDescription,
                KEY_FOLLOWING_COUNT to followingCount,
                KEY_FOLLOWERS_COUNT to followersCount,
                KEY_POSTS_COUNT to postsCount
            )
            usersCollection.document(uid).set(user)
                .addOnSuccessListener {
                    isSuccessful.invoke(true)
                }
                .addOnFailureListener {
                    isSuccessful.invoke(false)
                }
        }
    }

    fun getLoggedInUserData(uid: String, data: (LoggedInUser) -> Unit) {
        usersCollection.document(uid).get().addOnSuccessListener { document ->
            if (document.exists()) {
                document?.let { snapshot ->
                    snapshot.apply {
                        val loggedInUser = LoggedInUser(
                            uid = uid,
                            displayName = getString(KEY_NAME),
                            userName = getString(KEY_USERNAME),
                            email = getString(KEY_EMAIl),
                            photoUrl = getString(KEY_PHOTO_URL),
                            profileDescription = getString(KEY_PROFILE_DESCRIPTION),
                            followersCount = getString(KEY_FOLLOWERS_COUNT),
                            followingCount = getString(KEY_FOLLOWING_COUNT),
                            postsCount = getString(KEY_POSTS_COUNT)
                        )
                        data.invoke(loggedInUser)
                    }
                }
            }
        }
    }

    fun retrieveLoggedInUser(uid: String) {
        usersCollection.document(uid).get().addOnSuccessListener { document ->
            if (document.exists()) {
                document?.let { snapshot ->
                    val loggedInUser = LoggedInUser(
                        uid = uid,
                        displayName = snapshot.getString(KEY_NAME),
                        userName = snapshot.getString(KEY_USERNAME),
                        email = snapshot.getString(KEY_EMAIl),
                        photoUrl = snapshot.getString(KEY_PHOTO_URL),
                        profileDescription = snapshot.getString(KEY_PROFILE_DESCRIPTION),
                        followersCount = snapshot.getString(KEY_FOLLOWERS_COUNT),
                        followingCount = snapshot.getString(KEY_FOLLOWING_COUNT),
                        postsCount = snapshot.getString(KEY_POSTS_COUNT)
                    )
                }
            }
        }
    }

    fun getUserProfile(currentUser: FirebaseUser, callback: (Any) -> Unit) {
        val userId = currentUser.uid

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
        private const val KEY_FOLLOWING_COUNT = "following_count"
        private const val KEY_FOLLOWERS_COUNT = "followers_count"
        private const val KEY_POSTS_COUNT = "posts_count"

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