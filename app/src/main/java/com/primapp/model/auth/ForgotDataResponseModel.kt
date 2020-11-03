package com.primapp.model.auth

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel


data class ForgotDataResponseModel(
    @SerializedName("content")
    val content: ForgotDataContent
) : BaseDataModel()

data class ForgotDataContent(
    @SerializedName("user_id")
    val userId: Int
)