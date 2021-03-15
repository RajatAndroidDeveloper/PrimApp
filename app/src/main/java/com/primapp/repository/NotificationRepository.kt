package com.primapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.primapp.model.notification.NotificationResult
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import com.primapp.ui.notification.source.NotificationDataSource
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val apiService: ApiService,
    private val responseHandler: ResponseHandler
) {
    fun getUserNotifications(notificationType: String?): LiveData<PagingData<NotificationResult>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                NotificationDataSource(responseHandler, apiService, notificationType)
            }
        ).liveData
    }

    suspend fun acceptRejectMentorship(requestId: Int, action: String, message: String?): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.acceptRejectMentorship(requestId, action, message))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}