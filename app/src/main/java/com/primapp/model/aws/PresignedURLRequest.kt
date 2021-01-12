package com.primapp.model.aws
import com.google.gson.annotations.SerializedName


data class PresignedURLRequest(
    @SerializedName("object_name")
    val objectName: String
)