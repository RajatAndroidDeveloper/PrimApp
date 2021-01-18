package com.primapp.model.community

import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import com.primapp.utils.Validator


data class CreateCommunityRequestModel(
    @SerializedName("community_description")
    var communityDescription: String?,
    @SerializedName("community_name")
    var communityName: String?,
    @SerializedName("community_image_file")
    var communityImageFile: String? = null
) {
    fun isValidFormData(): ValidationResults {
        if (communityName.isNullOrEmpty())
            return ValidationResults.EMPTY_COMMUNITY_NAME
        if (!Validator.isValidCommunityNameLength(communityName))
            return ValidationResults.INVALID_COMMUNITY_NAME_LENGTH
        if (communityDescription.isNullOrEmpty())
            return ValidationResults.EMPTY_COMMUNITY_DESCRIPTION
        if (!Validator.isValidCommunityDescriptionLength(communityDescription))
            return ValidationResults.INVALID_COMMUNITY_DESCRIPTION_LENGTH

        return ValidationResults.SUCCESS
    }
}