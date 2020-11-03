package com.primapp.model.auth

import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import com.primapp.utils.Validator

data class ForgotDataRequestModel(
    @SerializedName("email")
    var email: String?
) {
    fun isValidFormData(): ValidationResults {
        if (email.isNullOrEmpty())
            return ValidationResults.EMPTY_EMAIL
        else if (!Validator.isEmailValid(email ?: ""))
            return ValidationResults.EMAIL_NOT_VALID

        return ValidationResults.SUCCESS
    }
}