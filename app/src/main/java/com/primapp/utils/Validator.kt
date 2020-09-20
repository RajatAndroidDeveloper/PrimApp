package com.primapp.utils

import android.util.Patterns

object Validator {
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(pass: String?): Boolean {
        return pass != null && pass.length >= 6
    }
}