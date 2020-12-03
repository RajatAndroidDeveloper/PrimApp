package com.primapp.repository

import com.primapp.model.auth.ReferenceResponseDataModel
import com.primapp.model.category.ParentCategoryResponseModel
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import javax.inject.Inject

class CommunitiesRepository @Inject constructor(
    private val apiService: ApiService,
    private val responseHandler: ResponseHandler
) {

    suspend fun getParentCategoryList(): Resource<ParentCategoryResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.getParentCategories())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

}