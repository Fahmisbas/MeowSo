package com.revelatestudio.meowso.data.dataholder.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LoggedInUser(
    val uid: String,
    val displayName: String?,
    val userName: String?,
    val email: String?,
    val photoUrl: String?,
    val profileDescription: String? = "",
    var createdDate : String = "",
    val followingCount: String? = INITIAL_COUNT_VALUE,
    val followersCount: String? = INITIAL_COUNT_VALUE,
    val postsCount: String? = INITIAL_COUNT_VALUE
) : Parcelable {

    companion object {
        private const val INITIAL_COUNT_VALUE = "0"
    }

}


