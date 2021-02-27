package com.revelatestudio.meowso.util

import android.content.Context
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import java.util.*


fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 20f
        centerRadius = 50f
        backgroundColor = android.R.color.background_light
        start()
    }
}