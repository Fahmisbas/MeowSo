package com.revelatestudio.meowso.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.ui.auth.signin.SignInActivity
import com.revelatestudio.meowso.ui.auth.signup.SignUpActivity
import com.revelatestudio.meowso.util.MappingHelper
import com.revelatestudio.meowso.util.removeWhiteSpace

class AppRepository(private val firebaseDb: FirebaseFirestore, private val auth: FirebaseAuth) {

    private val usersCollection = firebaseDb.collection(USERS_COLLECTION)

    fun setUserAuthProfile(
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

    fun storeLoggedInUser(loggedInUser: LoggedInUser, isSuccessful: (Boolean) -> Unit) {
        val userData = MappingHelper.loggedInUserObjectToMap(loggedInUser)
        val uid = loggedInUser.uid
        usersCollection.document(uid).set(userData)
            .addOnSuccessListener {
                isSuccessful.invoke(true)
            }
            .addOnFailureListener {
                isSuccessful.invoke(false)
            }
    }

    fun getLoggedInUser(uid: String, data: (LoggedInUser?) -> Unit) {
        usersCollection.document(uid).get().addOnSuccessListener { document ->
            if (document.exists()) {
                document?.let { snapshot ->
                    val loggedInUser = MappingHelper.documentSnapshotMapToLoggedInUserObject(snapshot)
                    if (loggedInUser != null) {
                        data.invoke(loggedInUser)
                    }
                }
            } else {
                data.invoke(null)
            }
        }
    }


    fun logout(callback: () -> Unit) {
        auth.signOut()
        callback.invoke()
    }

    fun login(
        activity: SignInActivity,
        email: String,
        password: String,
        userData: (FirebaseUser?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    userData.invoke(currentUser)
                }
            } else {
                userData.invoke(null)
            }
        }
    }

    fun createAccount(
        activity: SignUpActivity,
        email: String,
        password: String,
        userData: (FirebaseUser?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        userData.invoke(currentUser)
                    }
                } else {
                    userData.invoke(null)
                }
            }
    }


    companion object {
        const val USERS_COLLECTION = "users"

        private const val default_profile_pic_token = "964cc4b4-682a-4209-900b-ee109f247c6a"
        private val DEFAULT_PROFILE_PICTURE_URL: String
            get() = "https://firebasestorage.googleapis.com/v0/b/meowso-9c708.appspot.com/o/profile%2Fdefault-user-profile-picture.jpg?alt=media&token=$default_profile_pic_token"

        @Volatile
        private var instance: AppRepository? = null
        fun getInstance(firebaseDb: FirebaseFirestore, auth: FirebaseAuth) =
            instance ?: synchronized(this) {
                instance ?: AppRepository(firebaseDb, auth)
            }
    }
}