package com.primapp.model.post

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel

data class PostDetailsResponseModel(
    @SerializedName("content")
    val content: PostListResult
) : BaseDataModel()