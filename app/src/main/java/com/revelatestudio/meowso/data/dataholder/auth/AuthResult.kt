package com.revelatestudio.meowso.data.dataholder.auth

/**
 * Authentication result : success (user details) or error message.
 */
data class AuthResult(
    val success: LoggedInUser? = null,
    val error: Int? = null
)