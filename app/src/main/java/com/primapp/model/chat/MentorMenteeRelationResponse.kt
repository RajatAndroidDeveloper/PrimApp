package com.primapp.model.chat
import com.google.gson.annotations.SerializedName
import com.primapp.model.community.CommunityData
import com.primapp.retrofit.base.BaseDataModel


data class MentorMenteeRelationResponse(
    @SerializedName("content")
    val content: List<CommunityContent>
):BaseDataModel()

data class CommunityContent(
    @SerializedName("community")
    val community: CommunityData
)