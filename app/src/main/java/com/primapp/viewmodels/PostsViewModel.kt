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
import com.primapp.model.post.PostDetailsResponseModel
import com.primapp.model.post.PostListResult
import com.primapp.model.post.ReportedMembersResponseModel
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

    fun getUserPostsListData(type: String, userId: Int): LiveData<PagingData<PostListResult>> {
        val lastResult = postListResultLiveData
        if (lastResult != null) {
            return lastResult
        }
        val newResultLiveData: LiveData<PagingData<PostListResult>> =
            repo.getUserPostList(type, userId).cachedIn(viewModelScope)
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

    fun getCommentsReply(communityId: Int, commentId: Int): LiveData<PagingData<ReplyData>> {
        val lastResult = postReplyListResultLiveData
        if (lastResult != null) {
            return lastResult
        }
        val newResultLiveData: LiveData<PagingData<ReplyData>> =
            repo.getPostReplies(communityId, commentId).cachedIn(viewModelScope)
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

    //Like Reply
    private var _likeReplyLiveData = MutableLiveData<Event<Resource<PostActionResponseModel>>>()
    var likeReplyLiveData: LiveData<Event<Resource<PostActionResponseModel>>> = _likeReplyLiveData

    fun likeReply(communityId: Int, userId: Int, postId: Int, commentId: Int, replyId: Int) = viewModelScope.launch {
        _likeReplyLiveData.postValue(Event(Resource.loading(null)))
        _likeReplyLiveData.postValue(Event(repo.likeReply(communityId, userId, postId, commentId, replyId)))
    }

    //UnLike Reply
    private var _unlikeReplyLiveData = MutableLiveData<Event<Resource<PostActionResponseModel>>>()
    var unlikeReplyLiveData: LiveData<Event<Resource<PostActionResponseModel>>> = _unlikeReplyLiveData

    fun unlikeReply(communityId: Int, userId: Int, postId: Int, commentId: Int, replyId: Int) = viewModelScope.launch {
        _unlikeReplyLiveData.postValue(Event(Resource.loading(null)))
        _unlikeReplyLiveData.postValue(Event(repo.unlikeReply(communityId, userId, postId, commentId, replyId)))
    }

    private var _bookmarkPostLiveData = MutableLiveData<Event<Resource<PostActionResponseModel>>>()
    var bookmarkPostLiveData: LiveData<Event<Resource<PostActionResponseModel>>> = _bookmarkPostLiveData

    fun bookmarkPost(communityId: Int, userId: Int, postId: Int) = viewModelScope.launch {
        _bookmarkPostLiveData.postValue(Event(Resource.loading(null)))
        _bookmarkPostLiveData.postValue(Event(repo.addBookmark(communityId, userId, postId)))
    }

    private var _removeBookmarkPostLiveData = MutableLiveData<Event<Resource<PostActionResponseModel>>>()
    var removeBookmarkLiveData: LiveData<Event<Resource<PostActionResponseModel>>> = _removeBookmarkPostLiveData

    fun removeBookmark(communityId: Int, userId: Int, postId: Int) = viewModelScope.launch {
        _removeBookmarkPostLiveData.postValue(Event(Resource.loading(null)))
        _removeBookmarkPostLiveData.postValue(Event(repo.removeBookmark(communityId, userId, postId)))
    }

    private var _hidePostLiveData = MutableLiveData<Event<Resource<PostActionResponseModel>>>()
    var hidePostLiveData: LiveData<Event<Resource<PostActionResponseModel>>> = _hidePostLiveData

    fun hidePost(postId: Int) = viewModelScope.launch {
        _hidePostLiveData.postValue(Event(Resource.loading(null)))
        _hidePostLiveData.postValue(Event(repo.hidePost(postId)))
    }

    private var _postDetailsLiveData = MutableLiveData<Event<Resource<PostDetailsResponseModel>>>()
    var postDetailsLiveData: LiveData<Event<Resource<PostDetailsResponseModel>>> = _postDetailsLiveData

    fun postDetails(communityId: Int, postId: Int) = viewModelScope.launch {
        _postDetailsLiveData.postValue(Event(Resource.loading(null)))
        _postDetailsLiveData.postValue(Event(repo.postDetails(communityId, postId)))
    }

    private var _reportedMembersLiveData = MutableLiveData<Event<Resource<ReportedMembersResponseModel>>>()
    var reportedMembersLiveData: LiveData<Event<Resource<ReportedMembersResponseModel>>> = _reportedMembersLiveData

    fun reportedPostMembers(communityId: Int, postId: Int) = viewModelScope.launch {
        _reportedMembersLiveData.postValue(Event(Resource.loading(null)))
        _reportedMembersLiveData.postValue(Event(repo.reportedPostMembers(communityId, postId)))
    }

    private var _removeCulpritMembersLiveData = MutableLiveData<Event<Resource<PostActionResponseModel>>>()
    var removeCulpritMembersLiveData: LiveData<Event<Resource<PostActionResponseModel>>> = _removeCulpritMembersLiveData

    fun removeCulpritMembers(communityId: Int, postId: Int, userId:Int) = viewModelScope.launch {
        _removeCulpritMembersLiveData.postValue(Event(Resource.loading(null)))
        _removeCulpritMembersLiveData.postValue(Event(repo.removeCulpritMember(communityId, postId, userId)))
    }

}