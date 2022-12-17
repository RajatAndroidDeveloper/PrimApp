package com.primapp.model.portfolio

import com.google.gson.annotations.SerializedName

data class AddBenefitRequest(
    @SerializedName("name")
    val name: String
)

data class AddBenefitResponse(
    @SerializedName("content")
    val content: BenefitsData
)

data class BenefitSuggestionResponse(
    @SerializedName("content")
    val content: ArrayList<BenefitsData>?
)