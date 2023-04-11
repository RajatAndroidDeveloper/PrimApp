package com.primapp.repository

import com.primapp.model.contract.CreateContractRequestModel
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
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
}