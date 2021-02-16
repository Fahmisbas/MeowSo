package com.revelatestudio.meowso.data.dataholder.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserIdAvailability(
    val id : String? = null,
    val isAvailable : Boolean? = null
) : Parcelable