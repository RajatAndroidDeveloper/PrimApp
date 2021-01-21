package com.primapp.model.post
import com.google.gson.annotations.SerializedName


data class CreatePostRequestModel(
    @SerializedName("file_type")
    val fileType: String?,
    @SerializedName("post_content_file")
    val postContentFile: String?,
    @SerializedName("post_text")
    var postText: String?
)