package com.primapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.primapp.model.contract.*
import com.primapp.model.post.PostListResult
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import com.primapp.ui.contract.AllContractsDataSource
import com.primapp.ui.post.source.PostListPageDataSource
import javax.inject.Inject

class ContractRepository @Inject constructor(
    private val apiService: ApiService,
    private val responseHandler: ResponseHandler) {
    suspend fun createContract(createContractRequestModel: CreateContractRequestModel): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.createContract(createContractRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
    fun getAllContracts(contractType: String): LiveData<PagingData<ResultsItem>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                AllContractsDataSource(apiService, contractType)
            }
        ).liveData
    }
    suspend fun getContractDetails(contractId: Int): Resource<ContractDetailResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.getContractDetails(contractId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun amendContract(amendContractRequestModel: AmendContractRequestModel): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.amendContract(amendContractRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun acceptContract(acceptContractRequestModel: AcceptContractRequestModel): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.acceptContract(acceptContractRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun acceptAmendRequest(contractId: Int, acceptAmendRequestModel: AcceptAmendRequestModel): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.acceptAmendRequest(contractId, acceptAmendRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}