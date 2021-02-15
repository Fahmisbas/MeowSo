package com.revelatestudio.meowso.data.dataholder.auth

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val confirmationPasswordError : Int? = null,
    val isDataValid: Boolean = false
)