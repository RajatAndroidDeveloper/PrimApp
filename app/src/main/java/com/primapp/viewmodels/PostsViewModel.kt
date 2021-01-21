package com.primapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.primapp.model.community.CommunityDetailsResponseModel
import com.primapp.model.community.JoinedCommunityListModel
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

    fun getParentCategoriesListData(): LiveData<PagingData<PostListResult>> {
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

}