package com.primapp.retrofit

import com.primapp.model.auth.*
import com.primapp.retrofit.base.BaseDataModel
import retrofit2.http.*

interface ApiService {

    @GET(ApiConstant.REFERENCE_DATA)
    suspend fun getReferenceData(@Query("entity") entity: String): ReferenceResponseDataModel

    @POST(ApiConstant.SIGN_UP)
    suspend fun signUp(@Body signUpRequestDataModel: SignUpRequestDataModel): BaseDataModel

    @POST(ApiConstant.VERIFY_USER)
    suspend fun verifyUser(@Body signUpRequestDataModel: SignUpRequestDataModel): VerifyUserResponseModel

    @POST(ApiConstant.LOGIN_USER)
    suspend fun verifyUser(@Body loginRequestDataModel: LoginRequestDataModel): VerifyUserResponseModel

    @PUT(ApiConstant.FORGOT_USERNAME)
    suspend fun forgotUsername(@Body forgotDataRequestModel: ForgotDataRequestModel): ForgotDataResponseModel

    @PUT(ApiConstant.FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body forgotDataRequestModel: ForgotDataRequestModel): ForgotDataResponseModel

    @POST(ApiConstant.FORGOT_USERNAME_VERIFY)
    suspend fun forgotUsernameVerify(
        @Path("userId") userId: String,
        @Body verifyOTPRequestModel: VerifyOTPRequestModel
    ): BaseDataModel

}