package com.primapp.repository

import com.primapp.model.auth.ReferenceResponseDataModel
import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.model.aws.PresignedURLRequest
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.profile.EditProfileRequestModel
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import com.primapp.utils.RetrofitUtils
import okhttp3.MultipartBody
import retrofit2.Response
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

    suspend fun generatePresignedURL(fileName: String): Resource<PresignedURLResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.generatePresignedURL(PresignedURLRequest(fileName)))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun uploadtoAWS(
        url: String,
        key: String,
        accessKey: String,
        amzSecurityToken: String?,
        policy: String,
        signature: String,
        file: MultipartBody.Part?
    ): Resource<Response<Unit>> {
        return try {
            responseHandler.handleResponse(
                apiService.uploadToAWS(
                    url,
                    RetrofitUtils.getRequestBody(key),
                    RetrofitUtils.getRequestBody(accessKey),
                    RetrofitUtils.getRequestBody(amzSecurityToken),
                    RetrofitUtils.getRequestBody(policy),
                    RetrofitUtils.getRequestBody(signature),
                    file
                ))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}