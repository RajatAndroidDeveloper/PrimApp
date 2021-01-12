package com.primapp.retrofit

import com.primapp.BuildConfig

object ApiConstant {

    const val BASE_URL = BuildConfig.BASE_URL

    const val NETWORK_PAGE_SIZE = 10

    //Auth
    const val REFERENCE_DATA = "reference-data"
    const val SIGN_UP = "users/signup"
    const val VERIFY_USER = "users/verify"
    const val LOGIN_USER = "users/login"
    const val FORGOT_USERNAME = "users/username"
    const val FORGOT_PASSWORD = "users/password"
    const val FORGOT_USERNAME_VERIFY = "users/{userId}/verify-username"
    const val FORGOT_PASSWORD_VERIFY = "users/{userId}/verify"
    const val CHANGE_PASSWORD = "users/{userId}/password"
    const val RESEND_OTP = "users/resend-otp"

    //Category and Communities
    const val GET_PARENT_CATEGORY_LIST = "categories"
    const val GET_COMMUNITIES = "categories/{categoryId}/communities"
    const val CREATE_COMMUNITY = "categories/{categoryId}/communities"
    const val JOIN_COMMUNITY = "communities/{communityId}/users/{userId}"
    const val GET_COMMUNITY = "communities/{communityId}"
    const val EDIT_COMMUNITY = "communities/{communityId}"

    //Profile
    const val EDIT_PROFILE = "users/{userId}"

    //AWS
    const val PRESIGNED_URL = "generate-presigned-url"
}