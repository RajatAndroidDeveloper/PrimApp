package com.primapp.repository

import com.primapp.model.portfolio.AddBenefitRequest
import com.primapp.model.portfolio.AddBenefitResponse
import com.primapp.model.portfolio.UserPortfolioResponse
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import javax.inject.Inject

class PortfolioRepository @Inject constructor(
    val apiService: ApiService,
    private val responseHandler: ResponseHandler
) {

    suspend fun getPortfolioData(userId: Int): Resource<UserPortfolioResponse> {
        return try {
            responseHandler.handleResponse(apiService.getPortfolio(userId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun addBenefits(addBenefitRequest: AddBenefitRequest): Resource<AddBenefitResponse> {
        return try {
            responseHandler.handleResponse(apiService.addPortfolioBenefit(addBenefitRequest))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}