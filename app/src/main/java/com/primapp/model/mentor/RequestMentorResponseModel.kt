package com.primapp.model.mentor

import com.google.gson.annotations.SerializedName
import com.primapp.model.auth.UserData
import com.primapp.model.community.CommunityData
import com.primapp.model.members.Community


data class RequestMentorResponseModel(
    @SerializedName("content")
    val content: Content
)

data class Content(
    @SerializedName("id")
    val id: Int,
    @SerializedName("mentee")
    val mentee: UserData,
    @SerializedName("mentee_message")
    val menteeMessage: String,
    @SerializedName("mentor")
    val mentor: UserData,
    @SerializedName("status")
    val status: String,
    @SerializedName("community")
    val community: CommunityData,
)