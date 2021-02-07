package com.revelatestudio.meowso.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser
import com.revelatestudio.meowso.util.getCurrentDateTime
import com.revelatestudio.meowso.util.toStringFormat

class AppRepository(private val firebaseDb: FirebaseFirestore) {

    fun storeUserInfoIntoFireStoreDB(loggedInUser: LoggedInUser, isSuccessful: (Boolean) -> Unit) {
        loggedInUser.let { userData ->
            userData.apply {
                val user = hashMapOf(
                    KEY_UID to userId,
                    KEY_NAME to displayName,
                    KEY_EMAIl to email,
                    KEY_PHOTO_URL to photoUrl.toString(),
                    KEY_CREATED_DATE to getCurrentDateTime().toStringFormat(DATE_FORMAT)
                )
                firebaseDb.collection(USER_COLLECTION).document(userId).set(user)
                    .addOnSuccessListener {
                        isSuccessful.invoke(true)
                    }
                    .addOnFailureListener {
                        isSuccessful.invoke(false)
                    }
            }
        }
    }

    companion object {
        private const val USER_COLLECTION = "users"
        private const val KEY_UID = "uid"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIl = "email"
        private const val KEY_PHOTO_URL = "photo_url"
        private const val KEY_CREATED_DATE = "created_date"

        private const val DATE_FORMAT = "dd/MM/yyyy HH:mm:ss"

        @Volatile
        private var instance: AppRepository? = null
        fun getInstance(firebaseDb: FirebaseFirestore) =
            instance ?: synchronized(this) {
                instance ?: AppRepository(firebaseDb)
            }
    }
}