package com.primapp.model.community
import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel
import java.io.Serializable

data class JoinedCommunityListModel(
    @SerializedName("content")
    val content: List<CommunityData>
):BaseDataModel(),Serializable