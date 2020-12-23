package com.primapp.model.community

import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults


data class EditCommunityRequestModel(
    @SerializedName("community_description")
    var communityDescription: String?,
    @SerializedName("community_name")
    var communityName: String?,
    @SerializedName("status")
    var status: String
) {
    fun isValidFormData(): ValidationResults {
        if (communityName.isNullOrEmpty())
            return ValidationResults.EMPTY_COMMUNITY_NAME
        if (communityDescription.isNullOrEmpty())
            return ValidationResults.EMPTY_COMMUNITY_DESCRIPTION

        return ValidationResults.SUCCESS
    }
}