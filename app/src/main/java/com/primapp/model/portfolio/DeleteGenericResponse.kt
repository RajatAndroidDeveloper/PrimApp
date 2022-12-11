package com.primapp.model.portfolio
import com.google.gson.annotations.SerializedName


data class DeleteGenericResponse(
    @SerializedName("content")
    val content: Content
)

data class Content(
    @SerializedName("id")
    val id: Int?
)