package com.primapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.primapp.model.aws.PresignedURLRequest
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.category.ParentCategoryResponseModel
import com.primapp.model.comment.CommentData
import com.primapp.model.comment.CreateCommentRequestModel
import com.primapp.model.community.JoinedCommunityListModel
import com.primapp.model.post.CreatePostRequestModel
import com.primapp.model.post.PostActionResponseModel
import com.primapp.model.post.PostListResult
import com.primapp.model.post.UpdatePostResponseModel
import com.primapp.model.reply.CreateReplyRequestModel
import com.primapp.model.reply.ReplyData
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import com.primapp.ui.post.comment.PostCommentListDataSource
import com.primapp.ui.post.reply.PostCommentReplyDataSource
import com.primapp.ui.post.source.PostListPageDataSource
import com.primapp.ui.profile.source.UserPostListPageDataSource
import com.primapp.utils.RetrofitUtils
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val apiService: ApiService,
    private val responseHandler: ResponseHandler
) {

    fun getPostList(): LiveData<PagingData<PostListResult>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                PostListPageDataSource(apiService)
            }
        ).liveData
    }

    suspend fun createPost(
        communityId: Int,
        userId: Int,
        createPostRequestModel: CreatePostRequestModel
    ): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(apiService.createPost(communityId, userId, createPostRequestModel))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
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

    fun getUserPostList(type: String, userId: Int): LiveData<PagingData<PostListResult>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                UserPostListPageDataSource(responseHandler, apiService, type, userId)
            }
        ).liveData
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

    suspend fun getParentCategoryList(offset: Int, limit: Int): Resource<ParentCategoryResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.getParentCategories(offset, limit))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getCategoryJoinedCommunity(categoryId: Int): Resource<JoinedCommunityListModel> {
        return try {
            responseHandler.handleResponse(apiService.getCategoryJoinedCommunityList(categoryId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    fun getPostComments(
        communityId: Int,
        userId: Int,
        postId: Int
    ): LiveData<PagingData<CommentData>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = ApiConstant.NETWORK_PAGE_SIZE,
                initialLoadSize = ApiConstant.NETWORK_PAGE_SIZE
            ),
            pagingSourceFactory = {
                PostCommentListDataSource(responseHandler, apiService, communityId, userId, postId)
            }
        ).liveData
    }

    suspend fun createComment(
        communityId: Int,
        userId: Int,
        postId: Int,
        createCommentRequestModel: CreateCommentRequestModel
    ): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(
                apiService.createComment(
                    communityId,
                    userId,
                    postId,
                    createCommentRequestModel
                )
            )
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun likeComment(
        communityId: Int,
        userId: Int,
        postId: Int,
        commentId: Int
    ): Resource<PostActionResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.likeComment(communityId, userId, postId, commentId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun unlikeComment(
        communityId: Int,
        userId: Int,
        postId: Int,
        commentId: Int
    ): Resource<PostActionResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.unlikeComment(communityId, userId, postId, commentId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    fun getPostReplies(communityId: Int, commentId: Int): LiveData<PagingData<ReplyData>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = 100,
                initialLoadSize = 100
            ),
            pagingSourceFactory = {
                PostCommentReplyDataSource(responseHandler, apiService, communityId, commentId)
            }
        ).liveData
    }

    suspend fun createCommentReply(
        communityId: Int,
        userId: Int,
        postId: Int,
        commentId: Int,
        createReplyRequestModel: CreateReplyRequestModel
    ): Resource<BaseDataModel> {
        return try {
            responseHandler.handleResponse(
                apiService.createCommentReply(
                    communityId,
                    userId,
                    postId,
                    commentId,
                    createReplyRequestModel
                )
            )
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun likeReply(
        communityId: Int,
        userId: Int,
        postId: Int,
        commentId: Int,
        replyId: Int
    ): Resource<PostActionResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.likeReply(communityId, userId, postId, commentId, replyId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun unlikeReply(
        communityId: Int,
        userId: Int,
        postId: Int,
        commentId: Int,
        replyId: Int
    ): Resource<PostActionResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.unlikeReply(communityId, userId, postId, commentId, replyId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun updatePost(
        communityId: Int,
        userId: Int,
        postId: Int,
        createPostRequestModel: CreatePostRequestModel
    ): Resource<UpdatePostResponseModel> {
        return try {
            responseHandler.handleResponse(apiService.updatePost(communityId, userId, postId, createPostRequestModel))
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

}