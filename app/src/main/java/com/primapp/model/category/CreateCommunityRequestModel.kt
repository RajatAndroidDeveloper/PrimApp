package com.primapp.model.category
import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import com.primapp.utils.Validator


data class CreateCommunityRequestModel(
    @SerializedName("community_description")
    var communityDescription: String?,
    @SerializedName("community_name")
    var communityName: String?
){
    fun isValidFormData(): ValidationResults {
        if (communityName.isNullOrEmpty())
            return ValidationResults.EMPTY_COMMUNITY_NAME
        if (communityDescription.isNullOrEmpty())
            return ValidationResults.EMPTY_COMMUNITY_DESCRIPTION

        return ValidationResults.SUCCESS
    }
}