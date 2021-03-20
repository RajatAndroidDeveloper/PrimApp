package com.primapp.model.settings
import com.google.gson.annotations.SerializedName


data class ReportIssueRequestModel(
    @SerializedName("issue_image_file")
    var issueImageFile: String?,
    @SerializedName("message")
    var message: String
)