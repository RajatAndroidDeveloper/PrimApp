package com.primapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.primapp.model.community.JoinedCommunityListModel
import com.primapp.model.post.PostActionResponseModel
import com.primapp.model.post.PostListResult
import com.primapp.repository.PostRepository
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
        val lastResult = postListResultLiveData
//        if (lastResult != null) {
//            return lastResult
//        }
        val newResultLiveData: LiveData<PagingData<PostListResult>> =
            repo.getPostList().cachedIn(viewModelScope)
        postListResultLiveData = newResultLiveData
        return newResultLiveData
    }

    //Get Joined Community
    private var _joinedCommunityLiveData = MutableLiveData<Event<Resource<JoinedCommunityListModel>>>()
    var joinedCommunityLiveData: LiveData<Event<Resource<JoinedCommunityListModel>>> = _joinedCommunityLiveData

    fun getJoinedCommunityList() = viewModelScope.launch {
        _joinedCommunityLiveData.postValue(Event(Resource.loading(null)))
        _joinedCommunityLiveData.postValue(Event(repo.getJoinedCommunity()))
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


}