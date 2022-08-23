package com.primapp.model.rewards

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel
import java.io.Serializable

data class RewardsResponseModel(
    @SerializedName("content")
    val content: RewardsContent
) : BaseDataModel()

data class RewardsContent(
    @SerializedName("account_stage")
    val accountStage: String?,
    @SerializedName("total_coin")
    val totalCoin: Int?,
    @SerializedName("coins_for_community")
    val coinsForCommunity: Int?,
    @SerializedName("coins_for_member")
    val coinsForMember: Int?,
    @SerializedName("coins_for_mentee")
    val coinsForMentee: Int?,
    @SerializedName("community_coins")
    val communityCoins: Int?,
    @SerializedName("community_member_coins")
    val communityMemberCoins: Int?,
    @SerializedName("mentee_coins")
    val menteeCoins: Int?,
) : Serializable