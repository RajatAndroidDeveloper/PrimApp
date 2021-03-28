package com.primapp.model.auth
import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import com.primapp.utils.Validator


data class PasswordVerificationRequestModel(
    @SerializedName("email")
    var email: String?,
    @SerializedName("code")
    var code: String?,
    @SerializedName("old_pwd")
    var oldPassword: String?,
    @SerializedName("new_pwd")
    var password: String?,
    @SerializedName("pwd_confirm")
    var confirmPassword: String?
){
    fun isValidFormData(): ValidationResults {
        if (password.isNullOrEmpty())
            return ValidationResults.EMPTY_PASSWORD
        else if (!Validator.isPasswordValid(password))
            return ValidationResults.INVALID_PASSWORD
        else if (confirmPassword.isNullOrEmpty())
            return ValidationResults.EMPTY_CONFIRM_PASSWORD
        else if (!Validator.isPasswordValid(confirmPassword))
            return ValidationResults.INVALID_CONFIRM_PASSWORD
        else if (!password.equals(confirmPassword, true)) {
            return ValidationResults.PASS_NOT_MATCH
        }

        return ValidationResults.SUCCESS
    }

    fun isValidChangePasswordFormData():ValidationResults{
        if(oldPassword.isNullOrEmpty())
            return ValidationResults.EMPTY_OLD_PASSWORD
        else if(!Validator.isPasswordValid(oldPassword))
            return ValidationResults.INVALID_OLD_PASSWORD
        else if (password.isNullOrEmpty())
            return ValidationResults.EMPTY_PASSWORD
        else if (!Validator.isPasswordValid(password))
            return ValidationResults.INVALID_PASSWORD
        else if (confirmPassword.isNullOrEmpty())
            return ValidationResults.EMPTY_CONFIRM_PASSWORD
        else if (!Validator.isPasswordValid(confirmPassword))
            return ValidationResults.INVALID_CONFIRM_PASSWORD
        else if (!password.equals(confirmPassword, true)) {
            return ValidationResults.PASS_NOT_MATCH
        }

        return ValidationResults.SUCCESS
    }
}