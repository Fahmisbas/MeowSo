package com.revelatestudio.meowso.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun <T> Activity.navigateToActivity(origin: Activity, destination: Class<T>) {
    Intent(origin, destination).apply {
        startActivity(this)
    }
}

/**
 * Extension function to show Toast message
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Date.toStringFormat(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}