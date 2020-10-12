package com.primapp.utils

import android.util.Patterns
import java.util.regex.Pattern

object Validator {

    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    );


    fun isEmailValid(email: String?): Boolean {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    fun isPasswordValid(pass: String?): Boolean {
        return pass != null && pass.length >= 8
    }

    fun isUsernameValid(username: String?): Boolean {
        return username != null && username.matches("^[a-zA-Z][a-zA-Z0-9_.]{3,19}\$".toRegex())
    }
}