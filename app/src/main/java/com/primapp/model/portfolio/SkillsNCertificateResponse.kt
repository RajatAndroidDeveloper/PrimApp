package com.primapp.model.portfolio

import com.google.gson.annotations.SerializedName

data class SkillsNCertificateResponse(
    @SerializedName("content")
    val content: ArrayList<SkillsCertificateData>?
)

data class AddSkillsRequest(
    @SerializedName("skills_id")
    val skills_id: List<Int>
)

data class AddSkillsResponse(
    @SerializedName("content")
    val content: SkillsCertificateData
)