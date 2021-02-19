package com.primapp.ui.post.create

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.primapp.PrimApp
import com.primapp.constants.PostFileType
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.category.ParentCategoryResponseModel
import com.primapp.model.community.EditCommunityRequestModel
import com.primapp.model.community.JoinedCommunityListModel
import com.primapp.model.post.CreatePostRequestModel
import com.primapp.model.post.UpdatePostResponseModel
import com.primapp.repository.PostRepository
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class CreatePostViewModel @Inject constructor(
    private val repo: PostRepository,
    app: Application,
    errorFields: ErrorFields
) : AndroidViewModel(app) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val createPostRequestModel = MutableLiveData<CreatePostRequestModel>()

    init {
        errorFieldsLiveData.value = errorFields
        createPostRequestModel.value = CreatePostRequestModel(null, null, null, null)
    }

    //Create post
    private var _createPostLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var createPostLiveData: LiveData<Event<Resource<BaseDataModel>>> = _createPostLiveData

    fun createPost(communityId: Int, userId: Int) = viewModelScope.launch {
        _createPostLiveData.postValue(Event(Resource.loading(null)))
        _createPostLiveData.postValue(Event(repo.createPost(communityId, userId, createPostRequestModel.value!!)))
    }


    //Get Presigned URL
    private var _generatePresignedURLLiveData = MutableLiveData<Event<Resource<PresignedURLResponseModel>>>()
    var generatePresignedURLLiveData: LiveData<Event<Resource<PresignedURLResponseModel>>> =
        _generatePresignedURLLiveData

    fun generatePresignedUrl(fileName: String) = viewModelScope.launch {
        _generatePresignedURLLiveData.postValue(Event(Resource.loading(null)))
        _generatePresignedURLLiveData.postValue(
            Event(repo.generatePresignedURL(fileName))
        )
    }

    //Upload to AWS
    private var _uploadAWSLiveData = MutableLiveData<Event<Resource<Response<Unit>>>>()
    var uploadAWSLiveData: LiveData<Event<Resource<Response<Unit>>>> =
        _uploadAWSLiveData

    fun uploadAWS(
        url: String,
        key: String,
        accessKey: String,
        amzSecurityToken: String?,
        policy: String,
        signature: String,
        file: MultipartBody.Part?
    ) = viewModelScope.launch {
        _uploadAWSLiveData.postValue(Event(Resource.loading(null)))
        _uploadAWSLiveData.postValue(
            Event(repo.uploadtoAWS(url, key, accessKey, amzSecurityToken, policy, signature, file))
        )
    }


    // get Parent Category List
    private var _parentCategoryLiveData = MutableLiveData<Event<Resource<ParentCategoryResponseModel>>>()
    var parentCategoryLiveData: LiveData<Event<Resource<ParentCategoryResponseModel>>> =
        _parentCategoryLiveData

    fun getParentCategoriesList(offset: Int, limit: Int) = viewModelScope.launch {
        _parentCategoryLiveData.postValue(Event(Resource.loading(null)))
        _parentCategoryLiveData.postValue(Event(repo.getParentCategoryList(offset, limit)))
    }

    // get Category Joined community List
    private var _categoryJoinedCommunityLiveData = MutableLiveData<Event<Resource<JoinedCommunityListModel>>>()
    var categoryJoinedCommunityLiveData: LiveData<Event<Resource<JoinedCommunityListModel>>> =
        _categoryJoinedCommunityLiveData

    fun getCategoryJoinedCommunityListData(categoryId: Int) = viewModelScope.launch {
        _categoryJoinedCommunityLiveData.postValue(Event(Resource.loading(null)))
        _categoryJoinedCommunityLiveData.postValue(Event(repo.getCategoryJoinedCommunity(categoryId)))
    }


    private var _updatePostLiveData = MutableLiveData<Event<Resource<UpdatePostResponseModel>>>()
    var updatePostLiveData: LiveData<Event<Resource<UpdatePostResponseModel>>> = _updatePostLiveData
    fun updatePost(communityId: Int, userId: Int, postId: Int) = viewModelScope.launch {
        //Clean up code before sending to update
        val dataToSend = createPostRequestModel.value
        when (dataToSend?.fileType) {
            PostFileType.IMAGE, PostFileType.GIF -> {
                dataToSend.thumbnailFile = null
            }
            null -> {
                dataToSend?.thumbnailFile = null
                dataToSend?.postContentFile = null
            }
        }
        Log.d("anshul_update", "Data to send : ${Gson().toJson(dataToSend)}")
        _updatePostLiveData.postValue(Event(Resource.loading(null)))
        _updatePostLiveData.postValue(Event(repo.updatePost(communityId, userId, postId, dataToSend!!)))
    }

}