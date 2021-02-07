package com.revelatestudio.meowso.data.dataholder.auth

import android.net.Uri


data class LoggedInUser(
    val userId: String,
    val displayName: String,
    val email: String,
    val photoUrl: Uri
)