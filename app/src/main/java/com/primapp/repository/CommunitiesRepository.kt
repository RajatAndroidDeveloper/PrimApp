package com.primapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.primapp.model.auth.ReferenceResponseDataModel
import com.primapp.model.category.CommunityData
import com.primapp.model.category.CommunityListResponseModel
import com.primapp.model.category.ParentCategoryResponseModel
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import com.primapp.ui.communities.data.CommunitiesPageDataSource
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

//    suspend fun getCommunitiesList(
//        categoryId: Int,
//        filterBy: String,
//        offset: Int,
//        limit: Int
//    ): Resource<CommunityListResponseModel> {
//        return try {
//            responseHandler.handleResponse(apiService.getCommunities(categoryId, filterBy, offset, limit))
//        } catch (e: Exception) {
//            responseHandler.handleException(e)
//        }
//    }

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
}