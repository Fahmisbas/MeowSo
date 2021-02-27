package com.revelatestudio.meowso.util

import com.google.firebase.firestore.DocumentSnapshot
import com.revelatestudio.meowso.data.dataholder.auth.LoggedInUser

object MappingHelper {

    fun loggedInUserObjectToMap(loggedInUser: LoggedInUser): HashMap<String, String?> {
        loggedInUser.apply {
            return hashMapOf(
                KEY_UID to uid,
                KEY_NAME to displayName,
                KEY_USERNAME to userName,
                KEY_EMAIl to email,
                KEY_PHOTO_URL to photoUrl,
                KEY_CREATED_DATE to createdDate,
                KEY_PROFILE_DESCRIPTION to profileDescription,
                KEY_FOLLOWING_COUNT to followingCount,
                KEY_FOLLOWERS_COUNT to followersCount,
                KEY_POSTS_COUNT to postsCount
            )
        }
    }

     fun documentSnapshotMapToLoggedInUserObject(snapshot: DocumentSnapshot) : LoggedInUser? {
        snapshot.apply {
            return getString(KEY_UID)?.let { uid ->
                LoggedInUser(
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
            }
        }
     }


    const val KEY_PHOTO_URL = "photo_url"
    const val KEY_USERNAME = "username"
    private const val KEY_UID = "uid"
    private const val KEY_NAME = "name"
    private const val KEY_EMAIl = "email"
    private const val KEY_CREATED_DATE = "created_date"
    private const val KEY_PROFILE_DESCRIPTION = "description"
    private const val KEY_FOLLOWING_COUNT = "following_count"
    private const val KEY_FOLLOWERS_COUNT = "followers_count"
    private const val KEY_POSTS_COUNT = "posts_count"

}