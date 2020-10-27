package com.primapp.model.auth
import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel


data class VerifyUserResponseModel(
    @SerializedName("content")
    val content: UserData?
):BaseDataModel()

data class UserData(
    @SerializedName("date_of_birth")
    val dateOfBirth: Long,
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
    val userName: String
)
