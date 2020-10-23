package com.primapp.model.auth
import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults


data class VerifyOTPRequestModel(
    @SerializedName("code")
    var code: String?
){
    fun isValidFormData(): ValidationResults {
        if (code.isNullOrEmpty())
            return ValidationResults.EMPTY_FIELD

        return ValidationResults.SUCCESS
    }
}