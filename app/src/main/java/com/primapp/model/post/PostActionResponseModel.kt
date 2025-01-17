package com.primapp.model.post

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel

data class PostActionResponseModel(
    @SerializedName("content")
    val content: PostActionContent,
    @SerializedName("message")
    val message: String?,
) : BaseDataModel()

data class PostActionContent(
    @SerializedName("community_id")
    val communityId: Int,
    @SerializedName("like_id")
    val likeId: Int,
    @SerializedName("post_id")
    val postId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("comment_id")
    val commentId: Int?,
    @SerializedName("reply_id")
    val replyId: Int?,
    @SerializedName("bookmark_id")
    val bookmarkId: Int?
)