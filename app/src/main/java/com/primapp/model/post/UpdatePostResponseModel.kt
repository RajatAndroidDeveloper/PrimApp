package com.primapp.model.post

import com.google.gson.annotations.SerializedName

data class UpdatePostResponseModel(
    @SerializedName("content")
    val content: PostListResult
)