package com.primapp.model.comment

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class CommentListResponseModel(
    @SerializedName("content")
    val content: Content
)

data class Content(
    @SerializedName("count")
    val count: Int,
    @SerializedName("next_page")
    val nextPage: String?,
    @SerializedName("prev_page")
    val prevPage: String?,
    @SerializedName("results")
    val results: List<CommentData>
)

data class CommentData(
    @SerializedName("cdate")
    val cdate: String,
    @SerializedName("comment_content_file")
    val commentContentFile: String,
    @SerializedName("comment_text")
    val commentText: String,
    @SerializedName("get_image_url")
    val getImageUrl: Any?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_like")
    var isLike: Boolean,
    @SerializedName("post")
    val post: Int,
    @SerializedName("udate")
    val udate: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("like_count")
    var likeCount: Long = 0L,
    @SerializedName("get_reply_count")
    var replyCount: Long = 0L
) : Serializable

data class User(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("get_image_url")
    val getImageUrl: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("is_mentor")
    val isMentor: Boolean,
    @SerializedName("is_mentee")
    val isMentee: Boolean
) : Serializable