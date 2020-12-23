package com.primapp.model.community

import com.google.gson.annotations.SerializedName
import com.primapp.model.community.CommunityData
import com.primapp.retrofit.base.BaseDataModel


data class CommunityDetailsResponseModel(
    @SerializedName("content")
    val content: CommunityData
) : BaseDataModel()
