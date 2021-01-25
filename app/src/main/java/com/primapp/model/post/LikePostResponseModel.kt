package com.primapp.model.post

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel

data class LikePostResponseModel(
    @SerializedName("content")
    val content: LikePostContent
) : BaseDataModel()

data class LikePostContent(
    @SerializedName("community_id")
    val communityId: Int,
    @SerializedName("like_id")
    val likeId: Int,
    @SerializedName("post_id")
    val postId: Int,
    @SerializedName("user_id")
    val userId: Int
)