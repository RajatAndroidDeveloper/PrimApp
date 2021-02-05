package com.primapp.model.reply
import com.google.gson.annotations.SerializedName


data class CreateReplyRequestModel(
    @SerializedName("reply_text")
    val replyText: String
)