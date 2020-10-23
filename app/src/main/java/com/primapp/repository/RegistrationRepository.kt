package com.primapp.repository

import android.util.Log
import com.primapp.model.auth.ReferenceResponseDataModel
import com.primapp.model.auth.SignUpRequestDataModel
import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import javax.inject.Inject

class RegistrationRepository @Inject constructor(
    val apiService: ApiService,
    val responseHandler: ResponseHandler
) {
    var token = ""

    init {
        fetchAccessToken()
    }

    suspend fun getReferenceData(type:String): Resource<ReferenceResponseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.getReferenceData(type))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun signUpUser(signUpRequestModel: SignUpRequestDataModel): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.signUp(signUpRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun verifyUser(signUpRequestModel: SignUpRequestDataModel): Resource<VerifyUserResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.verifyUser(signUpRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }


    private fun fetchAccessToken() {
//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.i(MyFirebaseMessagingService.TAG, "getInstanceId failed", task.exception)
//                    return@OnCompleteListener
//                }
//
//                // Get new Instance ID token
//                task.result?.let {
//                    token = it.token
//                }
//
//                Log.i(MyFirebaseMessagingService.TAG, "Repo Token = " + token)
//            })

    }
}