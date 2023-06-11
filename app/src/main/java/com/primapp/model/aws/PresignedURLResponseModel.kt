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
    val aWSAccessKeyId: String? = null,
    @SerializedName("key")
    val key: String? = null,
    @SerializedName("policy")
    val policy: String? = null,
    @SerializedName("signature")
    val signature: String? = null,
    @SerializedName("x-amz-security-token")
    val xAmzSecurityToken: String?? = null,
    @field:SerializedName("x-amz-date")
    val xAmzDate: String? = null,
    @SerializedName("x-amz-signature")
    val xAmzSignature: String? = null,
    @SerializedName("x-amz-algorithm")
    val xAmzAlgorithm: String? = null,
    @SerializedName("x-amz-credential")
    val xAmzCredential: String? = null,
)