package com.primapp.model.post
import com.google.gson.annotations.SerializedName


data class ReportedMembersResponseModel(
    @SerializedName("content")
    val content: List<ReportedMembers>
)

data class ReportedMembers(
    @SerializedName("id")
    val id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("report_type")
    val reportType: Int,
    @SerializedName("user")
    val user: User
)

data class RemovedUser(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("get_image_url")
    val imageUrl: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("username")
    val username: String
)