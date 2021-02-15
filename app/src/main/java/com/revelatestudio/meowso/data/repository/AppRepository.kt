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
import com.revelatestudio.meowso.util.getCurrentDateTime
import com.revelatestudio.meowso.util.removeWhiteSpace
import com.revelatestudio.meowso.util.toStringFormat

typealias SuccessCallback = (Boolean) -> Unit
typealias LoggedInUserCallback = (LoggedInUser?) -> Unit
typealias FirebaseUserCallback = (FirebaseUser?) -> Unit
typealias VoidCallback = () -> Unit

class AppRepository(firebaseDb: FirebaseFirestore, private val auth: FirebaseAuth) {

    private val usersCollection = firebaseDb.collection(USERS_COLLECTION)

    fun setUserAuthProfile(
        currentUser: FirebaseUser,
        catName: String,
        callback: LoggedInUserCallback
    ) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(catName)
            .setPhotoUri(Uri.parse(DEFAULT_PROFILE_PICTURE_URL))
            .build()
        currentUser.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setLoggedInUser(currentUser) { loggedInUser ->
                    callback.invoke(loggedInUser)
                }
            } else callback.invoke(null)
        }.addOnFailureListener { err ->
            err.printStackTrace()
            callback.invoke(null)
        }
    }

    fun setLoggedInUser(user: FirebaseUser, callback: LoggedInUserCallback) {
        user.apply {
            val displayName = displayName
            val username = displayName?.removeWhiteSpace()
            val userEmail = email
            val photoUrl = photoUrl.toString()
            if (displayName != null && username != null && userEmail != null) {
                val loggedInUser = LoggedInUser(uid, displayName, username, userEmail, photoUrl)
                callback.invoke(loggedInUser)
            } else callback.invoke(null)
        }
    }

    fun storeSignUpUserData(loggedInUser: LoggedInUser, callback: SuccessCallback) {
        loggedInUser.createdDate = getCurrentDateTime().toStringFormat(DATE_FORMAT)
        val userData: HashMap<String, String?> = MappingHelper.loggedInUserObjectToMap(loggedInUser)
        val uid = loggedInUser.uid
        usersCollection.document(uid).set(userData)
            .addOnSuccessListener {
                callback.invoke(true)
            }
            .addOnFailureListener { err ->
                err.printStackTrace()
                callback.invoke(false)
            }
    }

    fun getLoggedInUser(uid: String, callback: LoggedInUserCallback) {
        usersCollection.document(uid).get().addOnSuccessListener { document ->
            if (document.exists()) {
                document?.let { snapshot ->
                    val loggedInUser =
                        MappingHelper.documentSnapshotMapToLoggedInUserObject(snapshot)
                    if (loggedInUser != null) {
                        callback.invoke(loggedInUser)
                    }
                }
            } else callback.invoke(null)

        }.addOnFailureListener { err ->
            callback.invoke(null)
            err.printStackTrace()
        }
    }


    fun logout(callback: VoidCallback) {
        auth.signOut()
        callback.invoke()
    }

    fun login(
        activity: SignInActivity,
        email: String,
        password: String,
        callback: FirebaseUserCallback
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    callback.invoke(currentUser)
                }
            } else callback.invoke(null)

        }.addOnFailureListener { err ->
            callback.invoke(null)
            err.printStackTrace()
        }
    }

    fun createAccount(
        activity: SignUpActivity,
        email: String,
        password: String,
        callback: FirebaseUserCallback
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        callback.invoke(currentUser)
                    }
                } else callback.invoke(null)

            }.addOnFailureListener { err ->
                callback.invoke(null)
                err.printStackTrace()
            }
    }

    fun checkEmailAvailability(email: String, callback: SuccessCallback, isTaskFinished : (Boolean) -> Unit, error: (Boolean) -> Unit) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            error.invoke(false)
            isTaskFinished(false)
            if (task.isSuccessful) {
                val user = task.result?.signInMethods
                if (user != null && user.isEmpty()) {
                    callback.invoke(true)
                } else callback.invoke(false)
            }
        }.addOnFailureListener { err ->
            error.invoke(true)
            isTaskFinished.invoke(true)
            err.printStackTrace()
        }
    }


    companion object {
        const val USERS_COLLECTION = "users"
        private const val DATE_FORMAT = "dd/MM/yyyy HH:mm:ss"

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