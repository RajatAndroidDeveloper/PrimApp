package com.primapp.model.chat
import com.google.gson.annotations.SerializedName


data class UserMenteeMentorUserReponseModel(
    @SerializedName("content")
    val content: Content
)

data class Content(
    @SerializedName("count")
    val count: Int,
    @SerializedName("next_page")
    val nextPage: String,
    @SerializedName("prev_page")
    val prevPage: Any?,
    @SerializedName("results")
    val results: List<ChatUser>
)

data class ChatUser(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("get_image_url")
    val getImageUrl: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("username")
    val username: String
)