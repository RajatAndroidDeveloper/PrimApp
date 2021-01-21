package com.primapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.primapp.model.aws.PresignedURLRequest
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.community.CommunityDetailsResponseModel
import com.primapp.model.community.JoinCommunityResponseModel
import com.primapp.model.community.JoinedCommunityListModel
import com.primapp.model.post.CreatePostRequestModel
import com.primapp.model.post.PostListResult
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import com.primapp.ui.post.source.PostListPageDataSource
import com.primapp.utils.RetrofitUtils
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val apiService: ApiService,
    private val responseHandler: ResponseHandler
) {

    fun getPostList(): LiveData<PagingData<PostListResult>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                PostListPageDataSource(apiService)
            }
        ).liveData
    }

    suspend fun getJoinedCommunity(): Resource<JoinedCommunityListModel> {
        return try {
            responseHandler.handleResponse(apiService.getJoinedCommunityList())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun createPost(
        communityId: Int,
        userId: Int,
        createPostRequestModel: CreatePostRequestModel
    ): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.createPost(communityId, userId, createPostRequestModel))
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