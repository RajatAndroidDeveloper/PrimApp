package com.primapp.model.comment

import com.google.gson.annotations.SerializedName


data class CreateCommentRequestModel(
    @SerializedName("comment_text")
    val commentText: String,
    @SerializedName("comment_content_file")
    val commentContentFile: String? = null
)