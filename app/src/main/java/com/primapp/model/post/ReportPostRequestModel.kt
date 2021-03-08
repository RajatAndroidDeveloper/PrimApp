package com.primapp.model.post
import com.google.gson.annotations.SerializedName


data class ReportPostRequestModel(
    @SerializedName("message")
    val message: String?,
    @SerializedName("report_type")
    val reportType: Int
)