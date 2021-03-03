package com.primapp.retrofit

import com.primapp.model.auth.*
import com.primapp.model.aws.PresignedURLRequest
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.category.ParentCategoryResponseModel
import com.primapp.model.comment.CommentListResponseModel
import com.primapp.model.comment.CreateCommentRequestModel
import com.primapp.model.community.*
import com.primapp.model.members.CommunityMembersResponseModel
import com.primapp.model.mentor.RequestMentorDataModel
import com.primapp.model.mentor.RequestMentorResponseModel
import com.primapp.model.notification.NotificationResponseModel
import com.primapp.model.post.CreatePostRequestModel
import com.primapp.model.post.PostActionResponseModel
import com.primapp.model.post.PostListResponseModel
import com.primapp.model.post.UpdatePostResponseModel
import com.primapp.model.profile.EditProfileRequestModel
import com.primapp.model.reply.CommentReplyResponseModel
import com.primapp.model.reply.CreateReplyRequestModel
import com.primapp.retrofit.base.BaseDataModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
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

    @PUT(ApiConstant.EDIT_COMMUNITY)
    suspend fun editCommunity(
        @Path("communityId") communityId: Int,
        @Body editCommunityRequestModel: EditCommunityRequestModel
    ): CommunityDetailsResponseModel

    @POST(ApiConstant.PRESIGNED_URL)
    suspend fun generatePresignedURL(@Body presignedURLRequest: PresignedURLRequest): PresignedURLResponseModel

    @Multipart
    @POST
    @Headers("@: NoAuth")
    suspend fun uploadToAWS(
        @Url url: String,
        @Part("key") key: RequestBody?,
        @Part("AWSAccessKeyId") awsAccessKey: RequestBody?,
        @Part("x-amz-security-token") amzSecurityToken: RequestBody?,
        @Part("policy") policy: RequestBody?,
        @Part("signature") signature: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Response<Unit>

    @GET(ApiConstant.GET_POST_LIST)
    suspend fun getPostList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PostListResponseModel

    @GET(ApiConstant.JOINED_COMMUNITY_LIST)
    suspend fun getJoinedCommunityList(
        @Path("userId") userId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("type") filterType: String
    ): CommunityListResponseModel

    @POST(ApiConstant.CREATE_POST)
    suspend fun createPost(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Body createPostRequestModel: CreatePostRequestModel
    ): BaseDataModel

    @POST(ApiConstant.LIKE_POST)
    suspend fun likePost(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int
    ): PostActionResponseModel

    @DELETE(ApiConstant.UNLIKE_POST)
    suspend fun unlikePost(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int
    ): PostActionResponseModel

    @GET(ApiConstant.USER_POST_LIST)
    suspend fun getUserPostList(
        @Path("userId") userId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PostListResponseModel

    @GET(ApiConstant.GET_PROFILE)
    suspend fun getUserProfile(
        @Path("userId") userId: Int
    ): VerifyUserResponseModel

    @DELETE(ApiConstant.DELETE_POST)
    suspend fun deletePost(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int
    ): PostActionResponseModel

    @GET(ApiConstant.CATEGORY_JOINED_COMMUNITY_LIST)
    suspend fun getCategoryJoinedCommunityList(
        @Path("categoryId") categoryId: Int
    ): JoinedCommunityListModel

    @POST(ApiConstant.EDIT_POST)
    suspend fun editPost(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int,
        @Body createPostRequestModel: CreatePostRequestModel
    ): BaseDataModel

    @GET(ApiConstant.COMMUNITY_POST_LIST)
    suspend fun getCommunityPostList(
        @Path("communityId") communityId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PostListResponseModel

    @GET(ApiConstant.COMMENT_LIST)
    suspend fun getPostComments(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): CommentListResponseModel

    @POST(ApiConstant.CREATE_COMMENT)
    suspend fun createComment(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int,
        @Body createCommentRequestModel: CreateCommentRequestModel
    ): BaseDataModel

    @GET(ApiConstant.COMMUNITY_MEMBERS_LIST)
    suspend fun getCommunityMembers(
        @Path("communityId") communityId: Int,
        @Query("search") search: String?,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): CommunityMembersResponseModel

    @POST(ApiConstant.LIKE_COMMENT)
    suspend fun likeComment(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int
    ): PostActionResponseModel

    @DELETE(ApiConstant.LIKE_COMMENT)
    suspend fun unlikeComment(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int
    ): PostActionResponseModel

    @GET(ApiConstant.LIKE_POST_MEMBERS_LIST)
    suspend fun getPostLikeMembersList(
        @Path("communityId") communityId: Int,
        @Path("postId") postId: Int,
        @Query("search") search: String?,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): CommunityMembersResponseModel

    @GET(ApiConstant.GET_POST_COMMENT_REPLIES)
    suspend fun getPostCommentReply(
        @Path("communityId") communityId: Int,
        @Path("commentId") commentId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): CommentReplyResponseModel

    @POST(ApiConstant.CREATE_REPLY)
    suspend fun createCommentReply(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int,
        @Body createReplyRequestModel: CreateReplyRequestModel
    ): BaseDataModel

    @POST(ApiConstant.LIKE_UNLIKE_REPLY)
    suspend fun likeReply(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int,
        @Path("replyId") replyId: Int
    ): PostActionResponseModel

    @DELETE(ApiConstant.LIKE_UNLIKE_REPLY)
    suspend fun unlikeReply(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int,
        @Path("replyId") replyId: Int
    ): PostActionResponseModel

    @POST(ApiConstant.REQUEST_MENTOR)
    suspend fun requestMentor(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Body requestMentorDataModel: RequestMentorDataModel
    ): RequestMentorResponseModel

    @GET(ApiConstant.GET_NOTIFICATIONS)
    suspend fun getNotifications(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): NotificationResponseModel

    @PUT(ApiConstant.ACCEPT_REJECT_MENTORSHIP)
    suspend fun acceptRejectMentorship(
        @Query("request_id") requestId: Int,
        @Query("action") action: String,
        @Query("message") message: String?
    ): BaseDataModel

    @PUT(ApiConstant.UPDATE_POST)
    suspend fun updatePost(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int,
        @Body createPostRequestModel: CreatePostRequestModel
    ): UpdatePostResponseModel

    @POST(ApiConstant.BOOKMARK_POST_ACTION)
    suspend fun addBookmarkPost(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int
    ): PostActionResponseModel

    @DELETE(ApiConstant.BOOKMARK_POST_ACTION)
    suspend fun removeBookmarkPost(
        @Path("communityId") communityId: Int,
        @Path("userId") userId: Int,
        @Path("postId") postId: Int
    ): PostActionResponseModel

    @GET(ApiConstant.GET_BOOKMARK_POSTS)
    suspend fun getBookmarkedPost(): PostListResponseModel

    @GET(ApiConstant.GET_MENTOR_MENTEE_LIST)
    suspend fun getMentorMenteeList(
        @Path("userId") userId: Int,
        @Query("users_type") type: String,
        @Query("status") status: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): CommunityMembersResponseModel
}