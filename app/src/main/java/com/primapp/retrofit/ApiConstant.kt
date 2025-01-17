package com.primapp.retrofit

import com.primapp.BuildConfig

object ApiConstant {

    const val BASE_URL = BuildConfig.BASE_URL

    const val ANNOUNCMENTS = "http://admin.prim-technology.com/about-us"
    const val ABOUT_US = "https://www.prim-technology.com/about-us/"
    const val TERMS_OF_SERVICES = "https://www.prim-technology.com/terms-of-service/"
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
    const val DELETE_ACCOUNT = "users/{user-id}"

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
    const val DELETE_COMMENT_DATA = "communities/{community-id}/posts/{post-id}/comments/{comment-id}/"
    const val DELETE_COMMENT_REPLY = "communities/{community-id}/posts/{post-id}/comments/{comment-id}/reply/{reply-id}"

    //Mentor Mentee
    const val REQUEST_MENTOR = "communities/{communityId}/users/{userId}/mentors"
    const val GET_MENTOR_MENTEE_LIST = "user/{userId}/mentor_mentee_list"
    const val GET_MENTOR_MENTEE_UNIQUE_LIST = "mentee-mentor-user-list/{userId}"
    const val DASHBOARD_DETAILS = "contracts/contact-dashboard"

    //Create Contract
    const val CREATE_CONTRACT = "contracts/create-contract"
    const val GET_ALL_CONTRACT = "contracts/view-all-projects"
    const val GET_CONTRACT_DETAILS = "contracts/contract-detail/{contractId}"
    const val AMEND_CONTRACT = "contracts/amend-contract"
    const val MY_OWN_CONTRACTS = "contracts/view-own-contracts"
    const val ACCEPT_CONTRACT = "contracts/accept-contract"
    const val ACCEPT_AMEND_REQUEST = "contracts/accept-amend-request/{contractId}"
    const val GET_TOTAL_EARNINGS = "contract/money-earned"
    const val SUBMIT_CONTRACT_RATINGS = "contract/rate-contract/{contractId}"
    const val GET_CONTRACT_RATINGS = "contract/rate-contract"

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
    const val BENEFIT_SUGGESTIONS = "users/benefits-list/"
    const val ADD_MENTORING_PORTFOLIO = "users/mentoring-portfolio/"
    const val DELETE_MENTORING_PORTFOLIO = "users/mentoring-portfolio/{portfolioId}/"
    const val ADD_EXPERIENCE = "users/experience/"
    const val UPDATE_EXPERIENCE = "users/experience/{experienceId}/"
    const val SKILLS_LIST = "users/skill-list/"
    const val ADD_SKILLS = "users/skills/"
    const val DELETE_SKILL = "users/skills/{skillId}/"
    const val COMMON_COMMUNITIES = "users/{userId}/common_communities"

    //To-do list
    const val GET_TODO_LIST = "user-tasks/"
    const val UPDATE_TODO_TASK = "user-tasks/{todoTaskId}/"
    const val MARK_MULTIPLE_TODOS_COMPLETED = "user-tasks/mark-as-completed"
    const val DELETE_MULTIPLE_TODOS = "user-tasks/mark-as-deleted"

    //Contract Status
    const val CONTRACT_ACCEPTED = "ACCEPTED"
}