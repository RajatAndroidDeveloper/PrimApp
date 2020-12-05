package com.primapp.model.category
import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel


data class ParentCategoryResponseModel(
    @SerializedName("content")
    val content: Content
): BaseDataModel()

data class Content(
    @SerializedName("count")
    val count: Int,
    @SerializedName("next")
    val next: String,
    @SerializedName("previous")
    val previous: Any?,
    @SerializedName("results")
    val results: List<ParentCategoryResult>?
)

data class ParentCategoryResult(
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
    @SerializedName("id")
    val id: Int,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("total_active_community")
    val totalActiveCommunity: Int,
    @SerializedName("total_active_member_all_community")
    val totalActiveMemberAllCommunity: Int,
    @SerializedName("udate")
    val udate: String
)