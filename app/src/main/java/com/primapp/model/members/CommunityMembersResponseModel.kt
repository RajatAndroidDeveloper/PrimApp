package com.primapp.model.members

import com.google.gson.annotations.SerializedName


data class CommunityMembersResponseModel(
    @SerializedName("content")
    val content: Content
)

data class Content(
    @SerializedName("count")
    val count: Int,
    @SerializedName("next_page")
    val nextPage: Any?,
    @SerializedName("prev_page")
    val prevPage: Any?,
    @SerializedName("results")
    val results: List<CommunityMembersData>
)

data class CommunityMembersData(
    @SerializedName("cdate")
    val cdate: String?,
    @SerializedName("community")
    val community: Community?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("udate")
    val udate: String?,
    @SerializedName("user")
    val user: User,
    @SerializedName("post") //for post likes
    val post: Int?,
    @SerializedName("status") //for mentor/mentee list
    val status: Int?
)

data class Community(
    @SerializedName("category")
    val category: Category,
    @SerializedName("community_name")
    val communityName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_joined")
    val isJoined: Boolean
)

data class User(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("get_image_url")
    val getImageUrl: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_mentee")
    val isMentee: Boolean,
    @SerializedName("is_mentor")
    val isMentor: Boolean,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("inappropriate_category")
    val inappropriateCategory: String,
    @SerializedName("is_inappropriate")
    val isInappropriate: Boolean,
    @SerializedName("username")
    val username: String,
    @SerializedName("is_mentor_status")
    var mentor_status: Int?,
    @SerializedName("is_loggedIn_user")
    val isLoggedInUser: Boolean
)

data class Category(
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("id")
    val id: Int
)