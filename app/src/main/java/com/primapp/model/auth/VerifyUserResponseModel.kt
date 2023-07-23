package com.primapp.model.auth

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel


data class VerifyUserResponseModel(
    @SerializedName("content")
    val content: UserData?
) : BaseDataModel()

data class UserData(
    @SerializedName("date_of_birth")
    val dateOfBirth: Long?,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("device_type")
    val deviceType: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("gender")
    val gender: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("username")
    val userName: String,
    @SerializedName("gender_value")
    val genderValue: String?,
    @SerializedName("get_image_url")
    val userImage: String?,
    @SerializedName("profile_summary")
    val profileSummary: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("country_iso_code")
    val countryIsoCode: String?,
    @SerializedName("joined_community_count")
    var joinedCommunityCount: Int = 0,
    @SerializedName("created_community_count")
    var createdCommunityCount: Int = 0,
    @SerializedName("posts_count")
    var postsCount: Int = 0,
    @SerializedName("mentee_count")
    var menteeCount: Int = 0,
    @SerializedName("mentor_count")
    var mentorCount: Int = 0,
    @SerializedName("total_notifications")
    var notificationsCount: Int = 0,
    @SerializedName("isPortfolioAvailable")
    var isPortfolioAvailable: Boolean = false,
    @SerializedName("todo_notifications_count")
    var todoNotificationsCount: Int = 0,
    @SerializedName("user_online_status")
    var userOnlineStatus: String? = null
)
