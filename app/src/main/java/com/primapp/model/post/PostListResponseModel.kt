package com.primapp.model.post
import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel


data class PostListResponseModel(
    @SerializedName("content")
    val content: Content
):BaseDataModel()

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
    val getImageUrl: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_bookmark")
    val isBookmark: Boolean,
    @SerializedName("is_like")
    val isLike: Boolean,
    @SerializedName("post_comments")
    val postComments: Int,
    @SerializedName("post_content_file")
    val postContentFile: String,
    @SerializedName("post_likes")
    val postLikes: Int,
    @SerializedName("post_text")
    val postText: String,
    @SerializedName("udate")
    val udate: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("file_type")
    val fileType:String?
)

data class Community(
    @SerializedName("community_name")
    val communityName: String,
    @SerializedName("id")
    val id: Int
)

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
    val username: String
)