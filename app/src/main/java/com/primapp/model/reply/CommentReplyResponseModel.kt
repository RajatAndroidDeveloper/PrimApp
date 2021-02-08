package com.primapp.model.reply

import com.google.gson.annotations.SerializedName
import com.primapp.model.comment.User
import com.primapp.retrofit.base.BaseDataModel

data class CommentReplyResponseModel(
    @SerializedName("content")
    val content: Content
) : BaseDataModel()

data class Content(
    @SerializedName("count")
    val count: Int,
    @SerializedName("next_page")
    val nextPage: Any?,
    @SerializedName("prev_page")
    val prevPage: Any?,
    @SerializedName("results")
    val results: List<ReplyData>
)

data class ReplyData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cdate")
    val cdate: String,
    @SerializedName("comment")
    val comment: Int,
    @SerializedName("is_like")
    var isLike: Boolean,
    @SerializedName("reply_text")
    val replyText: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("like_count")
    var likeCount: Long? = 0L
)
