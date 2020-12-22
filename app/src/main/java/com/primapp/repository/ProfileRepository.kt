package com.primapp.repository

import com.primapp.model.auth.ReferenceResponseDataModel
import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.model.profile.EditProfileRequestModel
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    val apiService: ApiService,
    val responseHandler: ResponseHandler
) {

    suspend fun getReferenceData(type: String): Resource<ReferenceResponseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.getReferenceData(type))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun editProfile(
        userId: Int,
        editProfileRequestModel: EditProfileRequestModel
    ): Resource<VerifyUserResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.editProfile(userId, editProfileRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}