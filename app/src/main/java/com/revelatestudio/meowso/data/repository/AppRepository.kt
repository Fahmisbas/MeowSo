package com.revelatestudio.meowso.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.ui.auth.signin.SignInActivity
import com.revelatestudio.meowso.ui.auth.signup.signupfinalstep.SignUpFinalStepActivity
import com.revelatestudio.meowso.util.MappingHelper
import com.revelatestudio.meowso.util.MappingHelper.KEY_PHOTO_URL
import com.revelatestudio.meowso.util.MappingHelper.KEY_USERNAME

typealias SuccessCallback = (Boolean?) -> Unit
typealias LoggedInUserCallback = (LoggedInUser?) -> Unit
typealias FirebaseUserCallback = (FirebaseUser?) -> Unit
typealias VoidCallback = () -> Unit
typealias UidCallback = (String?) -> Unit

class AppRepository(
    db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    storage: FirebaseStorage
) {

    private val usersCollection = db.collection(USERS_COLLECTION)
    private var storageRef = storage.reference

    fun setUserAuthProfile(
        displayName: String?,
        photoUrl: String?,
        callback: SuccessCallback
    ) {
        var profileUpdates: UserProfileChangeRequest? = null

        val photoUri = if (photoUrl != null) Uri.parse(photoUrl) else null
        if (displayName != null && photoUri != null) {
            profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(photoUri)
                .build()
        } else if (displayName != null && photoUri == null) {
            profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
        } else if (photoUri != null && displayName == null) {
            profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUri)
                .build()
        }

        auth.currentUser?.let { user ->
            if (profileUpdates != null) {
                user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback.invoke(true)
                    } else callback.invoke(false)
                }.addOnFailureListener { err ->
                    err.printStackTrace()
                    callback.invoke(null)
                }
            }
        }
    }

    fun storeLoggedInUserData(loggedInUser: LoggedInUser, callback: SuccessCallback) {
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


    fun getLoggedInUserData(uid: String, callback: LoggedInUserCallback) {
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
        callback: (String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    callback.invoke(currentUser.uid)
                }
            } else callback.invoke(null)

        }.addOnFailureListener { err ->
            callback.invoke(null)
            err.printStackTrace()
        }
    }

    fun createAccount(
        activity: SignUpFinalStepActivity,
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

    fun checkEmailAvailability(
        email: String,
        isAvailableCallback: SuccessCallback,
        onError: SuccessCallback
    ) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = task.result?.signInMethods
                if (user != null && user.isEmpty()) {
                    isAvailableCallback.invoke(true)
                } else isAvailableCallback.invoke(false)
            } else isAvailableCallback.invoke(null)
        }.addOnFailureListener { err ->
            onError.invoke(true)
            err.printStackTrace()
        }
    }

    fun checkUsernameAvailability(
        username: String,
        isAvailableCallback: (Boolean) -> Unit,
        onError: SuccessCallback
    ) {
        val userNameQuery = usersCollection.whereEqualTo(KEY_USERNAME, username)
        userNameQuery.get().addOnSuccessListener { snapshot ->
            isAvailableCallback.invoke(snapshot.documents.size == 0)
        }.addOnFailureListener {
            onError.invoke(true)
            it.printStackTrace()
        }
    }

    fun checkIsUserLoggedIn(callback: FirebaseUserCallback) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            callback.invoke(currentUser)
        }
    }

    fun uploadPhoto(
        imageUri: String,
        category: String,
        fileName: String,
        successCallback: SuccessCallback,
        downloadUrlCallback: (String?) -> Unit
    ) {
        val categoryRef = storageRef.child(category)
        val fileRef = categoryRef.child(fileName)
        val uploadTask = fileRef.putFile(Uri.parse(imageUri))

        downloadUrlCallback.invoke(null)

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                successCallback.invoke(true)
            }
        }

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result
                downloadUrlCallback.invoke(downloadUrl.toString())
            } else {
                successCallback.invoke(false)
            }
        }.addOnFailureListener { err ->
            err.printStackTrace()
            successCallback.invoke(false)
        }
    }


    fun updateUserName(uid: String, username: String, callback: SuccessCallback) {
        usersCollection.document(uid).update(
            mapOf(KEY_USERNAME to username)
        ).addOnSuccessListener {
            callback.invoke(true)
        }.addOnFailureListener {
            it.printStackTrace()
            callback.invoke(false)
        }
    }

    fun updatePhotoUrl(uid: String, photoUrl: String, callback: SuccessCallback) {
        usersCollection.document(uid).update(
            mapOf(KEY_PHOTO_URL to photoUrl)
        ).addOnSuccessListener {
            callback.invoke(true)
        }.addOnFailureListener {
            it.printStackTrace()
            callback.invoke(false)
        }
    }

    companion object {
        const val USERS_COLLECTION = "users"
        private const val DEBUG = "repository"

        @Volatile
        private var instance: AppRepository? = null
        fun getInstance(
            firebaseDb: FirebaseFirestore,
            auth: FirebaseAuth,
            firebaseStorage: FirebaseStorage
        ) =
            instance ?: synchronized(this) {
                instance ?: AppRepository(firebaseDb, auth, firebaseStorage)
            }
    }
}