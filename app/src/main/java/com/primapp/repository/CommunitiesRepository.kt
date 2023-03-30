package com.primapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.model.aws.PresignedURLRequest
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.category.*
import com.primapp.model.chat.ChatUser
import com.primapp.model.chat.MentorMenteeRelationResponse
import com.primapp.model.community.*
import com.primapp.model.members.CommunityMembersData
import com.primapp.model.members.CommunityMembersResponseModel
import com.primapp.model.mentor.RequestMentorDataModel
import com.primapp.model.mentor.RequestMentorResponseModel
import com.primapp.model.portfolio.UserCommonCommunitiesResponse
import com.primapp.model.post.PostActionResponseModel
import com.primapp.model.post.PostListResult
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import com.primapp.ui.chat.source.UserMentorMenteeChatDataSource
import com.primapp.ui.communities.data.CommunitiesPageDataSource
import com.primapp.ui.communities.data.ParentCategoriesPageDataSource
import com.primapp.ui.communities.details.source.CommunitiesPostListDataSource
import com.primapp.ui.communities.members.CommunityMembersDataSource
import com.primapp.ui.dashboard.MentorMenteeMemberListSearchDataSource
import com.primapp.ui.post.likes.LikesMemberListDataSource
import com.primapp.ui.profile.source.MentorMenteeMemberListDataSource
import com.primapp.ui.profile.source.UserJoinedCommunityPageDataSource
import com.primapp.utils.RetrofitUtils
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class CommunitiesRepository @Inject constructor(
    private val apiService: ApiService,
    private val responseHandler: ResponseHandler
) {

    fun getParentCategoryList(): LiveData<PagingData<ParentCategoryResult>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                ParentCategoriesPageDataSource(apiService)
            }
        ).liveData
    }


    fun getCommunitiesList(
        categoryId: Int,
        query: String?,
        filterBy: String
    ): LiveData<PagingData<CommunityData>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                CommunitiesPageDataSource(apiService, categoryId, query, filterBy)
            }
        ).liveData
    }

    suspend fun createCommunity(
        categoryId: Int,
        createCommunityRequestModel: CreateCommunityRequestModel
    ): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.createCommunity(categoryId, createCommunityRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun joinCommunity(
        communityId: Int,
        userId: Int,
        communityActionRequestModel: CommunityActionRequestModel
    ): Resource<JoinCommunityResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.joinCommunity(communityId, userId, communityActionRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getCommunityDetails(communityId: Int): Resource<CommunityDetailsResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.getCommunityDetails(communityId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun editCommunity(
        communityId: Int,
        editCommunityRequestModel: EditCommunityRequestModel
    ): Resource<CommunityDetailsResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.editCommunity(communityId, editCommunityRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    fun getAllJoinedCommunity(filter: String, userId: Int, type: String): LiveData<PagingData<CommunityData>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                UserJoinedCommunityPageDataSource(responseHandler, apiService, filter, userId, type)
            }
        ).liveData
    }

    fun getCommunitiesPostList(communityId: Int): LiveData<PagingData<PostListResult>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                CommunitiesPostListDataSource(apiService, communityId)
            }
        ).liveData
    }

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
        file: MultipartBody.Part?
    ): Resource<Response<Unit>> {
        return try {
            responseHandler.handleResponse(
                apiService.uploadToAWS(
                    url,
                    RetrofitUtils.getRequestBody(key),
                    RetrofitUtils.getRequestBody(accessKey),
                    RetrofitUtils.getRequestBody(amzSecurityToken),
                    RetrofitUtils.getRequestBody(policy),
                    RetrofitUtils.getRequestBody(signature),
                    file
                )
            )
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    //------ POSTS -----

    suspend fun likePost(
        communityId: Int,
        userId: Int,
        postId: Int
    ): Resource<PostActionResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.likePost(communityId, userId, postId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun unlikePost(
        communityId: Int,
        userId: Int,
        postId: Int
    ): Resource<PostActionResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.unlikePost(communityId, userId, postId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun deletePost(
        communityId: Int,
        userId: Int,
        postId: Int
    ): Resource<PostActionResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.deletePost(communityId, userId, postId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun addBookmark(
        communityId: Int,
        userId: Int,
        postId: Int
    ): Resource<PostActionResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.addBookmarkPost(communityId, userId, postId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun removeBookmark(
        communityId: Int,
        userId: Int,
        postId: Int
    ): Resource<PostActionResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.removeBookmarkPost(communityId, userId, postId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun hidePost(postId: Int): Resource<PostActionResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.hidePost(postId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    //------ END OF POST --------


    fun getCommunityMembers(communityId: Int, search: String?): LiveData<PagingData<CommunityMembersData>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                CommunityMembersDataSource(responseHandler, apiService, communityId, search)
            }
        ).liveData
    }

    fun getPostLikeMembersList(
        communityId: Int,
        postId: Int,
        search: String?
    ): LiveData<PagingData<CommunityMembersData>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                LikesMemberListDataSource(responseHandler, apiService, communityId, postId, search)
            }
        ).liveData
    }

    //------------- Mentor Mentee----------------

    suspend fun requestMentor(
        communityId: Int,
        userId: Int,
        requestMentorDataModel: RequestMentorDataModel
    ): Resource<RequestMentorResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.requestMentor(communityId, userId, requestMentorDataModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    fun getMentorMenteeMemberList(userId: Int, type: String, status: Int): LiveData<PagingData<CommunityMembersData>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                MentorMenteeMemberListDataSource(responseHandler, apiService, userId, type, status)
            }
        ).liveData
    }

   suspend fun getMentorMenteeMemberSearchList(userId: Int, type: String, status: Int, offset: Int, query:String): Resource<CommunityMembersResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.getMentorMenteeList(userId, type, status, offset,200, query))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    //New way to hit api for mentors and mentees list
    suspend fun getMentorsMenteesData(userId: Int, userType: String, status: Int, offset: Int): Resource<CommunityMembersResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.getMentorMenteeList(userId, userType, status, offset,200))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    fun getMentorMenteeUserForChat(userId: Int, search: String?): LiveData<PagingData<ChatUser>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                UserMentorMenteeChatDataSource(responseHandler, apiService, userId, search)
            }
        ).liveData
    }

    suspend fun checkMentorMenteeRelation(userId: Int): Resource<MentorMenteeRelationResponse> {
        return try {
            responseHandler.handleResponse(apiService.checkMentorMenteeRelation(userId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    //---- User profile ----
    suspend fun getUserData(userId: Int): Resource<VerifyUserResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.getUserProfile(userId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    //------Common Communities----

    suspend fun getUserCommonCommunities(userId: Int): Resource<UserCommonCommunitiesResponse> {
        return try {
            responseHandler.handleResponse(apiService.getUserCommonCommunites(userId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}