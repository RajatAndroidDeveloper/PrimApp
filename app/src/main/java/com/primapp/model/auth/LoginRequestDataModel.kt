package com.primapp.model.auth

import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import com.primapp.utils.Validator

class LoginRequestDataModel(
    @SerializedName("email")
    var email: String?,
    @SerializedName("password")
    var password: String?
){
    fun isValidFormData(): ValidationResults {
        if (email.isNullOrEmpty())
            return ValidationResults.EMPTY_EMAIL
        else if (!Validator.isEmailValid(email ?: ""))
            return ValidationResults.EMAIL_NOT_VALID

        if (password.isNullOrEmpty())
            return ValidationResults.EMPTY_PASSWORD

        if (!Validator.isPasswordValid(password))
            return ValidationResults.INVALID_PASSWORD

        return ValidationResults.SUCCESS
    }
}