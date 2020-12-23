package com.primapp.model.community

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel


data class JoinCommunityResponseModel(
    @SerializedName("content")
    val content: CommunityData
) : BaseDataModel()