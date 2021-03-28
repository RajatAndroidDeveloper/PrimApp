package com.primapp.model.community

import com.google.gson.annotations.SerializedName

data class CommunityActionRequestModel(
    @SerializedName("action")
    val action: String,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("rating")
    val rating: Double? = null
)