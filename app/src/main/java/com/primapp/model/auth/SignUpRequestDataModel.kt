package com.primapp.model.auth

import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import com.primapp.utils.Validator
import java.io.Serializable


class SignUpRequestDataModel(
    @SerializedName("first_name")
    var firstName: String?,
    @SerializedName("last_name")
    var lastName: String?,
    @SerializedName("user_name")
    var username: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("gender")
    var gender: Int?,
    @SerializedName("date_of_birth")
    var dateOfBirth: String?,
    @SerializedName("country_iso_code")
    var countryIsoCode: String?,
    @SerializedName("password")
    var password: String?,
    var confirmPassword: String?,
    @SerializedName("device_id")
    var deviceId: String?,
    @SerializedName("device_type")
    var deviceType: String?,
    @SerializedName("code")
    var code: String?
):Serializable {
    fun isValidFormData(): ValidationResults {
        if (firstName.isNullOrEmpty())
            return ValidationResults.EMPTY_FIRSTNAME

        if (lastName.isNullOrEmpty())
            return ValidationResults.EMPTY_LASTNAME

        if (username.isNullOrEmpty())
            return ValidationResults.EMPTY_USERNAME
        else if (!Validator.isUsernameValid(username)) {
            return ValidationResults.INVALID_USERNAME
        }

        if (email.isNullOrEmpty())
            return ValidationResults.EMPTY_EMAIL
        else if (!Validator.isEmailValid(email ?: ""))
            return ValidationResults.EMAIL_NOT_VALID

//        if (gender.isNullOrEmpty())
//            return ValidationResults.EMPTY_GENDER
//
//        if (dateOfBirth.isNullOrEmpty())
//            return ValidationResults.EMPTY_DOB

        if (countryIsoCode.isNullOrEmpty())
            return ValidationResults.EMPTY_COUNTRY

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
}