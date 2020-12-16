package com.primapp.model.category

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel


data class JoinCommunityResponseModel(
    @SerializedName("content")
    val content: CommunityData
) : BaseDataModel()