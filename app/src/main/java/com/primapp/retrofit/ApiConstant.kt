package com.primapp.retrofit

import com.primapp.BuildConfig

object ApiConstant {

    const val BASE_URL = BuildConfig.BASE_URL

    const val NETWORK_PAGE_SIZE = 15

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
    const val JOINED_COMMUNITY_LIST = "joined_community_list/{userId}"
    const val CATEGORY_JOINED_COMMUNITY_LIST = "{categoryId}/joined_community_list"
    const val COMMUNITY_MEMBERS_LIST = "{communityId}/community_member_list"

    //Profile
    const val EDIT_PROFILE = "users/{userId}"
    const val GET_PROFILE = "users/{userId}"

    //AWS
    const val PRESIGNED_URL = "generate-presigned-url"

    //Post
    const val GET_POST_LIST = "post_list"
    const val CREATE_POST = "communities/{communityId}/users/{userId}/posts"
    const val LIKE_POST = "communities/{communityId}/users/{userId}/posts/{postId}/likes"
    const val UNLIKE_POST = "communities/{communityId}/users/{userId}/posts/{postId}/likes"
    const val USER_POST_LIST = "user_post_list/{userId}"
    const val DELETE_POST = "communities/{communityId}/users/{userId}/posts/{postId}"
    const val EDIT_POST = "communities/{communityId}/users/{userId}/posts/{postId}"
    const val COMMUNITY_POST_LIST = "{communityId}/community_post_list"
    const val LIKE_POST_MEMBERS_LIST = "community/{communityId}/post/{postId}/post_liked_user_list"

    //Post Comment
    const val COMMENT_LIST = "communities/{communityId}/users/{userId}/posts/{postId}/comments"
    const val CREATE_COMMENT = "communities/{communityId}/users/{userId}/posts/{postId}/comments"
    const val LIKE_COMMENT = "communities/{communityId}/users/{userId}/posts/{postId}/comments/{commentId}/likes"
    const val GET_POST_COMMENT_REPLIES = "community/{communityId}/comment/{commentId}/replies_list"
    const val CREATE_REPLY = "communities/{communityId}/users/{userId}/posts/{postId}/comments/{commentId}/replies"
    const val LIKE_UNLIKE_REPLY = "communities/{communityId}/users/{userId}/posts/{postId}/comments/{commentId}/replies/{replyId}/likes"

    //Mentor Mentee
    const val REQUEST_MENTOR = "communities/{communityId}/users/{userId}/mentors"
}