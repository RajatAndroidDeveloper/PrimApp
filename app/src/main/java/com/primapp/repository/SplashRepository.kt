package com.primapp.repository

import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.model.post.ReportPostRequestModel
import com.primapp.model.rewards.RewardsResponseModel
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.BaseDataModel
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

    suspend fun reportPost(
        communityId: Int,
        postId: Int,
        reportPostRequestModel: ReportPostRequestModel
    ): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.reportPost(communityId, postId, reportPostRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getRewardsData(
    ): Resource<RewardsResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.getRewards())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}