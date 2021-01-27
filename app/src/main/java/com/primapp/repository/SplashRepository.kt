package com.primapp.repository

import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import javax.inject.Inject

class SplashRepository @Inject constructor(
    val apiService: ApiService,
    private val responseHandler: ResponseHandler
) {
    suspend fun getUserData(userId: Int): Resource<VerifyUserResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.getUserProfile(userId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}