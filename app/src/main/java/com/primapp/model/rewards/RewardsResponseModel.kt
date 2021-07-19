package com.primapp.model.rewards

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel

data class RewardsResponseModel(
    @SerializedName("content")
    val content: RewardsContent
):BaseDataModel()

data class RewardsContent(
    @SerializedName("account_stage")
    val accountStage: String?,
    @SerializedName("total_coin")
    val totalCoin: Int?
)