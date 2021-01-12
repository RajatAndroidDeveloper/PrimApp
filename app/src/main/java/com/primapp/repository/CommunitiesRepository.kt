package com.primapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.primapp.model.aws.PresignedURLRequest
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.category.*
import com.primapp.model.community.*
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import com.primapp.ui.communities.data.CommunitiesPageDataSource
import com.primapp.ui.communities.data.ParentCategoriesPageDataSource
import com.primapp.utils.RetrofitUtils
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class CommunitiesRepository @Inject constructor(
    private val apiService: ApiService,
    private val responseHandler: ResponseHandler
) {

    suspend fun getParentCategoryList(offset: Int, limit: Int): Resource<ParentCategoryResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.getParentCategories(offset, limit))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    fun getParentCategoryList(): LiveData<PagingData<ParentCategoryResult>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                ParentCategoriesPageDataSource(apiService)
            }
        ).liveData
    }


    fun getCommunitiesList(
        categoryId: Int,
        query: String?,
        filterBy: String
    ): LiveData<PagingData<CommunityData>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                CommunitiesPageDataSource(apiService, categoryId, query, filterBy)
            }
        ).liveData
    }

    suspend fun createCommunity(
        categoryId: Int,
        createCommunityRequestModel: CreateCommunityRequestModel
    ): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.createCommunity(categoryId, createCommunityRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun joinCommunity(
        communityId: Int,
        userId: Int
    ): Resource<JoinCommunityResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.joinCommunity(communityId, userId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun leaveCommunity(
        communityId: Int,
        userId: Int
    ): Resource<JoinCommunityResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.leaveCommunity(communityId, userId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getCommunityDetails(communityId: Int): Resource<CommunityDetailsResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.getCommunityDetails(communityId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun editCommunity(
        communityId: Int,
        editCommunityRequestModel: EditCommunityRequestModel
    ): Resource<CommunityDetailsResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.editCommunity(communityId, editCommunityRequestModel))
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