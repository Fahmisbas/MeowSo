package com.revelatestudio.meowso.data.dataholder.auth

/**
 * Data validation state of the login form.
 */
data class SignInSignUpFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val confirmationPasswordError: Int? = null,
    val isDataValid: Boolean = false
)