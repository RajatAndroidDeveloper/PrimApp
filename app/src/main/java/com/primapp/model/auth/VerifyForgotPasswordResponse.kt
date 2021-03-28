package com.primapp.model.auth

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel

data class VerifyForgotPasswordResponse(
    @SerializedName("content")
    val content: VerifyForgotPassData
) : BaseDataModel()

data class VerifyForgotPassData(
    @SerializedName("email")
    val email: String,
    @SerializedName("user_id")
    val userId: Int
)