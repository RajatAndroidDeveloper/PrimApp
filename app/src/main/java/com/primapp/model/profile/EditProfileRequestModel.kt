package com.primapp.model.profile
import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import com.primapp.utils.Validator
import java.io.Serializable


data class EditProfileRequestModel(
    @SerializedName("first_name")
    var firstName: String?,
    @SerializedName("last_name")
    var lastName: String?,
    @SerializedName("gender")
    var gender: Int?,
    @SerializedName("country_iso_code")
    var countryIsoCode: String?,
    @SerializedName("profile_summary")
    var profileSummary: String?,
    @SerializedName("user_image_file")
    var userImageFile: Any?
): Serializable {
    fun isValidFormData(): ValidationResults {
        if (firstName.isNullOrEmpty())
            return ValidationResults.EMPTY_FIRSTNAME

        if (lastName.isNullOrEmpty())
            return ValidationResults.EMPTY_LASTNAME

        if (countryIsoCode.isNullOrEmpty())
            return ValidationResults.EMPTY_COUNTRY

        return ValidationResults.SUCCESS
    }

}