package com.revelatestudio.meowso.data.dataholder.auth

import android.net.Uri

/**
 * User details post authentication that is exposed to the UI
 */

data class LoggedInUserView(
    val displayName: String,
    val email: String,
    val photoUrl: Uri
    //... other data fields that may be accessible to the UI
)