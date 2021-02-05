package com.primapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.primapp.model.comment.CommentData
import com.primapp.model.comment.CreateCommentRequestModel
import com.primapp.model.post.PostActionResponseModel
import com.primapp.model.post.PostListResult
import com.primapp.model.reply.CreateReplyRequestModel
import com.primapp.model.reply.ReplyData
import com.primapp.repository.PostRepository
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostsViewModel @Inject constructor(
    private val repo: PostRepository,
    app: Application,
    errorFields: ErrorFields
) : AndroidViewModel(app) {


    // get Post List
    private var postListResultLiveData: LiveData<PagingData<PostListResult>>? = null

    fun getPostsList(): LiveData<PagingData<PostListResult>> {
//        val lastResult = postListResultLiveData
//        if (lastResult != null) {
//            return lastResult
//        }
        val newResultLiveData: LiveData<PagingData<PostListResult>> =
            repo.getPostList().cachedIn(viewModelScope)
        postListResultLiveData = newResultLiveData
        return newResultLiveData
    }

    //Like post
    private var _likePostLiveData = MutableLiveData<Event<Resource<PostActionResponseModel>>>()
    var likePostLiveData: LiveData<Event<Resource<PostActionResponseModel>>> = _likePostLiveData

    fun likePost(communityId: Int, userId: Int, postId: Int) = viewModelScope.launch {
        _likePostLiveData.postValue(Event(Resource.loading(null)))
        _likePostLiveData.postValue(Event(repo.likePost(communityId, userId, postId)))
    }

    //UnLike post
    private var _unlikePostLiveData = MutableLiveData<Event<Resource<PostActionResponseModel>>>()
    var unlikePostLiveData: LiveData<Event<Resource<PostActionResponseModel>>> = _unlikePostLiveData

    fun unlikePost(communityId: Int, userId: Int, postId: Int) = viewModelScope.launch {
        _unlikePostLiveData.postValue(Event(Resource.loading(null)))
        _unlikePostLiveData.postValue(Event(repo.unlikePost(communityId, userId, postId)))
    }

    //Delete post
    private var _deletePostLiveData = MutableLiveData<Event<Resource<PostActionResponseModel>>>()
    var deletePostLiveData: LiveData<Event<Resource<PostActionResponseModel>>> = _deletePostLiveData

    fun deletePost(communityId: Int, userId: Int, postId: Int) = viewModelScope.launch {
        _deletePostLiveData.postValue(Event(Resource.loading(null)))
        _deletePostLiveData.postValue(Event(repo.deletePost(communityId, userId, postId)))
    }

    // get User Post List
    private var userPostListResultLiveData: LiveData<PagingData<PostListResult>>? = null

    fun getUserPostsListData(): LiveData<PagingData<PostListResult>> {
        val lastResult = postListResultLiveData
        if (lastResult != null) {
            return lastResult
        }
        val newResultLiveData: LiveData<PagingData<PostListResult>> =
            repo.getUserPostList().cachedIn(viewModelScope)
        userPostListResultLiveData = newResultLiveData
        return newResultLiveData
    }

    // getComment List
    private var postCommentsListResultLiveData: LiveData<PagingData<CommentData>>? = null

    fun getPostCommentsListData(communityId: Int, userId: Int, postId: Int): LiveData<PagingData<CommentData>> {
        val lastResult = postCommentsListResultLiveData
        if (lastResult != null) {
            return lastResult
        }
        val newResultLiveData: LiveData<PagingData<CommentData>> =
            repo.getPostComments(communityId, userId, postId).cachedIn(viewModelScope)
        postCommentsListResultLiveData = newResultLiveData
        return newResultLiveData
    }

    private var _createCommentLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var createCommentLiveData: LiveData<Event<Resource<BaseDataModel>>> = _createCommentLiveData

    fun createComment(communityId: Int, userId: Int, postId: Int, commentText: String) = viewModelScope.launch {
        _createCommentLiveData.postValue(Event(Resource.loading(null)))
        _createCommentLiveData.postValue(
            Event(repo.createComment(communityId, userId, postId, CreateCommentRequestModel(commentText)))
        )
    }

    //Like Comment
    private var _likeCommentLiveData = MutableLiveData<Event<Resource<PostActionResponseModel>>>()
    var likeCommentLiveData: LiveData<Event<Resource<PostActionResponseModel>>> = _likeCommentLiveData

    fun likeComment(communityId: Int, userId: Int, postId: Int, commentId: Int) = viewModelScope.launch {
        _likeCommentLiveData.postValue(Event(Resource.loading(null)))
        _likeCommentLiveData.postValue(Event(repo.likeComment(communityId, userId, postId, commentId)))
    }

    //UnLike Comment
    private var _unlikeCommentLiveData = MutableLiveData<Event<Resource<PostActionResponseModel>>>()
    var unlikeCommentLiveData: LiveData<Event<Resource<PostActionResponseModel>>> = _unlikeCommentLiveData

    fun unlikeComment(communityId: Int, userId: Int, postId: Int, commentId: Int) = viewModelScope.launch {
        _unlikeCommentLiveData.postValue(Event(Resource.loading(null)))
        _unlikeCommentLiveData.postValue(Event(repo.unlikeComment(communityId, userId, postId, commentId)))
    }

    //Replies

    private var postReplyListResultLiveData: LiveData<PagingData<ReplyData>>? = null

    fun getCommentsReply(commentId: Int): LiveData<PagingData<ReplyData>> {
        val lastResult = postReplyListResultLiveData
        if (lastResult != null) {
            return lastResult
        }
        val newResultLiveData: LiveData<PagingData<ReplyData>> =
            repo.getPostReplies(commentId).cachedIn(viewModelScope)
        postReplyListResultLiveData = newResultLiveData
        return newResultLiveData
    }

    private var _createReplyLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var createCommentReplyLiveData: LiveData<Event<Resource<BaseDataModel>>> = _createReplyLiveData

    fun createCommentReply(communityId: Int, userId: Int, postId: Int, commentId: Int, replyText: String) =
        viewModelScope.launch {
            _createReplyLiveData.postValue(Event(Resource.loading(null)))
            _createReplyLiveData.postValue(
                Event(
                    repo.createCommentReply(
                        communityId,
                        userId,
                        postId,
                        commentId,
                        CreateReplyRequestModel(replyText)
                    )
                )
            )
        }


}