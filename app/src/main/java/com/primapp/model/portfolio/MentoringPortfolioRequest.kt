package com.primapp.model.portfolio

import com.google.gson.annotations.SerializedName

data class MentoringPortfolioRequest(
    @SerializedName("content_file")
    var contentFile: String?,
    @SerializedName("file_type")
    var fileType: String?,
    @SerializedName("thumbnail_file")
    var thumbnailFile: String?
)

data class AddMentoringPortfolioResponse(
    @SerializedName("content")
    val content: MentoringPortfolioData
)