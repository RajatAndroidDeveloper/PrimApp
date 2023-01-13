package com.primapp.model.portfolio

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel

data class UserCommonCommunitiesResponse(
    @SerializedName("content")
    val content: CommonCommunitiesContent?
) : BaseDataModel()

data class CommonCommunitiesContent(
    @SerializedName("results")
    val commonCommunityList: ArrayList<CommonCommunites>?
)

data class CommonCommunites(
    @SerializedName("community_description")
    val communityDescription: String?,
    @SerializedName("community_image_file")
    val communityImageFile: String?,
    @SerializedName("community_name")
    val communityName: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_mentee")
    val isMentee: Boolean?,
    @SerializedName("is_mentor")
    val isMentor: Boolean?,
    @SerializedName("is_mentor_status")
    var mentor_status: Int?,
)