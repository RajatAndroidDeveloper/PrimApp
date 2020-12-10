package com.primapp.model.category

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel


data class JoinCommunityResponseModel(
    @SerializedName("content")
    val content: JoinCommunityResponseContent
) : BaseDataModel()

data class JoinCommunityResponseContent(
    @SerializedName("community_id")
    val communityId: Int,
    @SerializedName("user_id")
    val userId: Int
)