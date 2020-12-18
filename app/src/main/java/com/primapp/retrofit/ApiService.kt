package com.primapp.retrofit

import com.primapp.model.auth.*
import com.primapp.model.category.*
import com.primapp.model.profile.EditProfileRequestModel
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

    @POST(ApiConstant.FORGOT_PASSWORD_VERIFY)
    suspend fun forgotPasswordVerify(
        @Path("userId") userId: String,
        @Body verifyOTPRequestModel: VerifyOTPRequestModel
    ): BaseDataModel

    @PUT(ApiConstant.CHANGE_PASSWORD)
    suspend fun changePassword(
        @Path("userId") userId: String,
        @Body verifyPasswordRequestModel: PasswordVerificationRequestModel
    ): VerifyUserResponseModel

    @PUT(ApiConstant.RESEND_OTP)
    suspend fun resendOTP(@Body forgotDataRequestModel: ForgotDataRequestModel): BaseDataModel

    @GET(ApiConstant.GET_PARENT_CATEGORY_LIST)
    suspend fun getParentCategories(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): ParentCategoryResponseModel

    @GET(ApiConstant.GET_COMMUNITIES)
    suspend fun getCommunities(
        @Path("categoryId") categoryId: Int,
        @Query("search_community") search: String?,
        @Query("filter_by") filterBy: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): CommunityListResponseModel

    @POST(ApiConstant.CREATE_COMMUNITY)
    suspend fun createCommunity(
        @Path("categoryId") categoryId: Int,
        @Body createCommunityRequestModel: CreateCommunityRequestModel
    ): BaseDataModel

    @POST(ApiConstant.JOIN_COMMUNITY)
    suspend fun joinCommunity(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int
    ): JoinCommunityResponseModel

    @GET(ApiConstant.GET_COMMUNITY)
    suspend fun getCommunityDetails(
        @Path("communityId") communityId: Int
    ): CommunityDetailsResponseModel

    @DELETE(ApiConstant.JOIN_COMMUNITY)
    suspend fun leaveCommunity(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int
    ): JoinCommunityResponseModel

    @PUT(ApiConstant.EDIT_PROFILE)
    suspend fun editProfile(
        @Path("userId") userId: Int,
        @Body editProfileRequestModel: EditProfileRequestModel
    ): VerifyUserResponseModel
}