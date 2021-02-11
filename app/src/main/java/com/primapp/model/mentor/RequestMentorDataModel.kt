package com.primapp.model.mentor
import com.google.gson.annotations.SerializedName


data class RequestMentorDataModel(
    @SerializedName("mentor_id")
    val mentorId: Int,
    @SerializedName("mentee_message")
    val menteeMessage: String?
)