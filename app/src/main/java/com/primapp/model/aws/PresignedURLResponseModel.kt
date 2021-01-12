package com.primapp.model.aws

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel


data class PresignedURLResponseModel(
    @SerializedName("content")
    val content: Content
) : BaseDataModel()

data class Content(
    @SerializedName("fields")
    val fields: Fields,
    @SerializedName("url")
    val url: String
)

data class Fields(
    @SerializedName("AWSAccessKeyId")
    val aWSAccessKeyId: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("policy")
    val policy: String,
    @SerializedName("signature")
    val signature: String,
    @SerializedName("x-amz-security-token")
    val xAmzSecurityToken: String?
)