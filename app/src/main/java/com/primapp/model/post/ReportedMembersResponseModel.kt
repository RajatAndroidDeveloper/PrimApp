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