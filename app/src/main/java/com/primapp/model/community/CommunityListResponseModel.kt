package com.primapp.model.community

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel
import java.io.Serializable


data class CommunityListResponseModel(
    @SerializedName("content")
    val content: CommunityListContent
) : BaseDataModel()

data class CommunityListContent(
    @SerializedName("category_description")
    val categoryDescription: String,
    @SerializedName("category_image_file")
    val categoryImageFile: Any?,
    @SerializedName("category_leader")
    val categoryLeader: String,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("cdate")
    val cdate: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("next_page")
    val nextPage: String?,
    @SerializedName("prev_page")
    val prevPage: String?,
    @SerializedName("results")
    val results: List<CommunityData>,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("total_active_community")
    val totalActiveCommunity: Int,
    @SerializedName("total_active_member_all_community")
    val totalActiveMemberAllCommunity: Int,
    @SerializedName("udate")
    val udate: String
)

data class CommunityData(
    @SerializedName("cdate")
    val cdate: String,
    @SerializedName("community_approver")
    val communityApprover: String,
    @SerializedName("community_description")
    var communityDescription: String,
    @SerializedName("get_image_url")
    var imageUrl: String?,
    @SerializedName("community_moderator")
    val communityModerator: String,
    @SerializedName("community_name")
    var communityName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("status")
    var status: String,
    @SerializedName("total_active_member")
    var totalActiveMember: Long,
    @SerializedName("udate")
    var udate: String,
    @SerializedName("is_joined")
    var isJoined: Boolean?,
    @SerializedName("is_createdbyme")
    val isCreatedByMe: Boolean?,
    @SerializedName("community_creater")
    val communityCreator: String?,
    @SerializedName("community_category")
    val communityCategory: String?
) : Serializable