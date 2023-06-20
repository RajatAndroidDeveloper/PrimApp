package com.primapp.repository

import com.primapp.model.auth.ReferenceResponseDataModel
import com.primapp.model.aws.PresignedURLRequest
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.portfolio.*
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import com.primapp.utils.RetrofitUtils
import okhttp3.MultipartBody
import retrofit2.Response
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

    suspend fun updateBenefits(benefitId: Int, addBenefitRequest: AddBenefitRequest): Resource<AddBenefitResponse> {
        return try {
            responseHandler.handleResponse(apiService.updateBenefit(benefitId, addBenefitRequest))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun deleteBenefits(benefitId: Int): Resource<DeleteGenericResponse> {
        return try {
            responseHandler.handleResponse(apiService.deleteBenefit(benefitId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun addMentoringPortfolio(request: MentoringPortfolioRequest): Resource<AddMentoringPortfolioResponse> {
        return try {
            responseHandler.handleResponse(apiService.addMentoringPortfolio(request))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun deleteMentoringPortfolio(id: Int): Resource<DeleteGenericResponse> {
        return try {
            responseHandler.handleResponse(apiService.deleteMentoringPortfolio(id))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun addPortfolioExperience(request: AddExperienceRequest): Resource<AddExperienceResponse> {
        return try {
            responseHandler.handleResponse(apiService.addPortfolioExperience(request))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getReferenceData(type: String): Resource<ReferenceResponseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.getReferenceData(type))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun deleteExperience(id: Int): Resource<DeleteGenericResponse> {
        return try {
            responseHandler.handleResponse(apiService.deleteExperience(id))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun updateExperience(id: Int, request: AddExperienceRequest): Resource<AddExperienceResponse> {
        return try {
            responseHandler.handleResponse(apiService.updateExperience(id, request))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getBenefitSuggestions(): Resource<BenefitSuggestionResponse> {
        return try {
            responseHandler.handleResponse(apiService.getBenefitSuggestions())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getSkillsList(): Resource<SkillsNCertificateResponse> {
        return try {
            responseHandler.handleResponse(apiService.getSkillsList())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun addSkill(addSkillsRequest: AddSkillsRequest): Resource<AddSkillsResponse> {
        return try {
            responseHandler.handleResponse(apiService.addSkill(addSkillsRequest))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun deleteSkillFromPortfolio(id: Int): Resource<DeleteGenericResponse> {
        return try {
            responseHandler.handleResponse(apiService.deleteSkillFromPortfolio(id))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    //To Upload file/image

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
        xAmzAlgorithm: String,
        xAmzCredential: String,
        xAmzDate: String,
        xAmzSignature: String,
        file: MultipartBody.Part?
    ): Resource<Response<Unit>> {
        return try {
            responseHandler.handleResponse(
                apiService.uploadToAWS(
                    url,
                    RetrofitUtils.getRequestBody(key),
                    RetrofitUtils.getRequestBody(xAmzAlgorithm),
                    RetrofitUtils.getRequestBody(xAmzCredential),
                    RetrofitUtils.getRequestBody(xAmzDate),
                    RetrofitUtils.getRequestBody(policy),
                    RetrofitUtils.getRequestBody(xAmzSignature),
                    file
                )
            )
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    // END To Upload file/image
}