package com.primapp.retrofit

import com.primapp.BuildConfig

object ApiConstant {

    const val BASE_URL = BuildConfig.BASE_URL

    const val ABOUT_US = "http://admin.prim-technology.com/about-us"
    const val PRIM_REWARDS = "http://admin.prim-technology.com/prim-rewards"
    const val SENSITIVE_DATA_DISCLAIMER = "https://admin.prim-technology.com/legel-disclaimer"

    const val NETWORK_PAGE_SIZE = 15

    //Auth
    const val REFERENCE_DATA = "reference-data"
    const val SIGN_UP = "users/signup"
    const val VERIFY_USER = "users/verify"
    const val LOGIN_USER = "users/login"
    const val FORGOT_USERNAME = "users/username"
    const val FORGOT_PASSWORD = "users/password"
    const val FORGOT_USERNAME_VERIFY = "users/{userId}/verify-username"
    const val FORGOT_PASSWORD_VERIFY = "users/{userId}/verify-otp"
    const val RESET_PASSWORD = "users/{userId}/reset-password"
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
    const val CREATED_COMMUNITY_LIST = "created_community_list/{userId}"

    //Profile
    const val EDIT_PROFILE = "users/{userId}"
    const val GET_PROFILE = "users/{userId}"

    //AWS
    const val PRESIGNED_URL = "generate-presigned-url"

    //Post
    const val GET_POST_LIST = "post_list"
    const val CREATE_POST = "communities/{communityId}/users/{userId}/posts"
    const val UPDATE_POST = "communities/{communityId}/users/{userId}/posts/{postId}"
    const val LIKE_POST = "communities/{communityId}/users/{userId}/posts/{postId}/likes"
    const val UNLIKE_POST = "communities/{communityId}/users/{userId}/posts/{postId}/likes"
    const val USER_POST_LIST = "user_post_list/{userId}"
    const val DELETE_POST = "communities/{communityId}/users/{userId}/posts/{postId}"
    const val EDIT_POST = "communities/{communityId}/users/{userId}/posts/{postId}"
    const val COMMUNITY_POST_LIST = "{communityId}/community_post_list"
    const val LIKE_POST_MEMBERS_LIST = "community/{communityId}/post/{postId}/post_liked_user_list"
    const val BOOKMARK_POST_ACTION = "communities/{communityId}/users/{userId}/posts/{postId}/bookmarks"
    const val GET_BOOKMARK_POSTS = "bookmarked_post_list"
    const val REPORT_POST = "community/{communityId}/post/{postId}/report"
    const val HIDE_POST = "hide_post/{postId}"
    const val POST_DETAILS = "community/{communityId}/post/{postId}"
    const val GET_HIDDEN_POSTS = "hide_post"

    //Post Comment
    const val COMMENT_LIST = "communities/{communityId}/users/{userId}/posts/{postId}/comments"
    const val CREATE_COMMENT = "communities/{communityId}/users/{userId}/posts/{postId}/comments"
    const val LIKE_COMMENT = "communities/{communityId}/users/{userId}/posts/{postId}/comments/{commentId}/likes"
    const val GET_POST_COMMENT_REPLIES = "community/{communityId}/comment/{commentId}/replies_list"
    const val CREATE_REPLY = "communities/{communityId}/users/{userId}/posts/{postId}/comments/{commentId}/replies"
    const val LIKE_UNLIKE_REPLY = "communities/{communityId}/users/{userId}/posts/{postId}/comments/{commentId}/replies/{replyId}/likes"

    //Mentor Mentee
    const val REQUEST_MENTOR = "communities/{communityId}/users/{userId}/mentors"
    const val GET_MENTOR_MENTEE_LIST = "user/{userId}/mentor_mentee_list"

    //Notification
    const val GET_NOTIFICATIONS = "user_notifications"
    const val ACCEPT_REJECT_MENTORSHIP = "accept_reject_mentorship"
    const val READ_ALL_NOTIFICATION = "check_all_notifications"

    //Report
    const val REPORT_ISSUE = "report_issue"

    //Chat
    const val GET_MENTOR_MENTEE_LIST_FOR_CHAT = "mentee-mentor-list/{userId}"
    const val CHECK_MENTOR_MENTEE_RELATION = "mentee-mentor-check/{userId}"

    //Rewards
    const val GET_REWARDS = "digital-assets"

    //Reported Post
    const val REPORTED_POST = "community/{communityId}/reported_post"
    const val REPORTED_POST_MEMBERS = "community/{communityId}/reported_post/{postId}/members"
    const val REMOVE_CULPRIT_MEMBER = "community/{communityId}/reported_post/{postId}/remove_member/{userId}"

    //Portfolio
    const val PORTFOLIO_DASHBOARD = "users/portfolio/{userId}"
    const val ADD_BENEFIT = "users/benefits/"
    const val UPDATE_BENEFIT = "users/benefits/{benefitId}/"
    const val ADD_MENTORING_PORTFOLIO = "users/mentoring-portfolio/"
    const val DELETE_MENTORING_PORTFOLIO = "users/mentoring-portfolio/{portfolioId}/"
}