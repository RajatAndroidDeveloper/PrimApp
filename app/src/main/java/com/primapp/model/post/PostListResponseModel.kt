package com.primapp.model.post

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel
import java.io.Serializable


data class PostListResponseModel(
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
    val results: List<PostListResult>
)

data class PostListResult(
    @SerializedName("cdate")
    val cdate: String,
    @SerializedName("community")
    val community: Community,
    @SerializedName("get_image_url")
    var imageUrl: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_bookmark")
    var isBookmark: Boolean,
    @SerializedName("is_hidden")
    var isHidden: Boolean,
    @SerializedName("is_like")
    var isLike: Boolean,
    @SerializedName("post_comments")
    val postComments: Int,
    @SerializedName("post_content_file")
    var postContentFile: String?,
    @SerializedName("post_likes")
    var postLikes: Int,
    @SerializedName("post_text")
    var postText: String?,
    @SerializedName("udate")
    val udate: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("file_type")
    var fileType: String?,
    @SerializedName("is_createdbyme")
    val isCreatedByMe: Boolean,
    @SerializedName("thumbnail_file")
    var thumbnailFile: String?,
    @SerializedName("get_thumbnail_url")
    var getThumbnailUrl: String?,
    @SerializedName("is_inappropriate")
    val isInappropriate: Boolean,
    @SerializedName("inappropriate_category")
    val inappropriateCategory: String?,
    @SerializedName("video_mute")
    val videoMute: Boolean = true,
) : Serializable

data class Community(
    @SerializedName("community_name")
    val communityName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("category")
    val category: Category,
    @SerializedName("is_joined")
    val isJoined: Boolean,
    @SerializedName("status")
    val status: String
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
    @SerializedName("is_mentee")
    val isMentee: Boolean,
    @SerializedName("is_mentor")
    val isMentor: Boolean,
    @SerializedName("inappropriate_category")
    val inappropriateCategory: String?,
    @SerializedName("is_inappropriate")
    val isInappropriate: Boolean,
    @SerializedName("user_online_status")
    var userOnlineStatus: String? = null
) : Serializable

data class Category(
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("id")
    val id: Int
) : Serializable