package com.primapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.primapp.PrimApp
import com.primapp.R
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.category.*
import com.primapp.model.community.CommunityData
import com.primapp.model.community.CommunityDetailsResponseModel
import com.primapp.model.community.CreateCommunityRequestModel
import com.primapp.model.community.JoinCommunityResponseModel
import com.primapp.repository.CommunitiesRepository
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class CommunitiesViewModel @Inject constructor(
    private val repo: CommunitiesRepository,
    app: Application,
    errorFields: ErrorFields
) : AndroidViewModel(app) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val createCommunityRequestDataModel = MutableLiveData<CreateCommunityRequestModel>()

    init {
        errorFieldsLiveData.value = errorFields
        createCommunityRequestDataModel.value =
            CreateCommunityRequestModel("", "")
    }

    fun validateCreateCommunity(): Boolean {
        val error = errorFieldsLiveData.value
        error?.errorCommunityName = null
        error?.errorCommunityDescription = null
        errorFieldsLiveData.value = error

        val result = createCommunityRequestDataModel.value?.isValidFormData()

        when (result) {
            ValidationResults.EMPTY_COMMUNITY_NAME -> {
                errorFieldsLiveData.value?.errorCommunityName =
                    context.getString(R.string.valid_community_name)
            }

            ValidationResults.INVALID_COMMUNITY_NAME_LENGTH -> {
                errorFieldsLiveData.value?.errorCommunityName =
                    context.getString(R.string.valid_community_name_length)
            }

            ValidationResults.EMPTY_COMMUNITY_DESCRIPTION -> {
                errorFieldsLiveData.value?.errorCommunityDescription =
                    context.getString(R.string.valid_community_description)
            }

            ValidationResults.INVALID_COMMUNITY_DESCRIPTION_LENGTH -> {
                errorFieldsLiveData.value?.errorCommunityName =
                    context.getString(R.string.valid_community_description_length)
            }

            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success Data : ${Gson().toJson(createCommunityRequestDataModel.value)}")
                return true
            }
        }

        return false

    }

    // get Parent Category List
    private var parentCategoryResultLiveData: LiveData<PagingData<ParentCategoryResult>>? = null

    fun getParentCategoriesListData(): LiveData<PagingData<ParentCategoryResult>> {
        val lastResult = parentCategoryResultLiveData
        if (lastResult != null) {
            return lastResult
        }
        val newResultLiveData: LiveData<PagingData<ParentCategoryResult>> =
            repo.getParentCategoryList().cachedIn(viewModelScope)
        parentCategoryResultLiveData = newResultLiveData
        return newResultLiveData
    }


    // get Community List in Parent Category
    private var currentQueryValue: String? = null
    private var communityListResultLiveData: LiveData<PagingData<CommunityData>>? = null

    fun getCommunityListData(categoryId: Int, query: String?, filterBy: String): LiveData<PagingData<CommunityData>> {
        val lastResult = communityListResultLiveData
        if (query.equals(currentQueryValue) && lastResult != null) {
            return lastResult
        }
        currentQueryValue = query
        val newResultLiveData: LiveData<PagingData<CommunityData>> =
            repo.getCommunitiesList(categoryId, query, filterBy).cachedIn(viewModelScope)
        communityListResultLiveData = newResultLiveData
        return newResultLiveData
    }

    // Create Community
    private var _createCommunityLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var createCommunityLiveData: LiveData<Event<Resource<BaseDataModel>>> =
        _createCommunityLiveData

    fun createCommunity(categoryId: Int) = viewModelScope.launch {
        _createCommunityLiveData.postValue(Event(Resource.loading(null)))
        _createCommunityLiveData.postValue(
            Event(
                repo.createCommunity(
                    categoryId,
                    createCommunityRequestDataModel.value!!
                )
            )
        )
    }

    //JOin Community API
    private var _joinCommunityLiveData = MutableLiveData<Event<Resource<JoinCommunityResponseModel>>>()
    var joinCommunityLiveData: LiveData<Event<Resource<JoinCommunityResponseModel>>> =
        _joinCommunityLiveData

    fun joinCommunity(communityId: Int, userId: Int) = viewModelScope.launch {
        _joinCommunityLiveData.postValue(Event(Resource.loading(null)))
        _joinCommunityLiveData.postValue(
            Event(repo.joinCommunity(communityId, userId))
        )
    }

    //Leave Community
    fun leaveCommunity(communityId: Int, userId: Int) = viewModelScope.launch {
        _joinCommunityLiveData.postValue(Event(Resource.loading(null)))
        _joinCommunityLiveData.postValue(
            Event(repo.leaveCommunity(communityId, userId))
        )
    }

    //Get Community API
    private var _getCommunityDetailsLiveData = MutableLiveData<Event<Resource<CommunityDetailsResponseModel>>>()
    var getCommunityDetailsLiveData: LiveData<Event<Resource<CommunityDetailsResponseModel>>> =
        _getCommunityDetailsLiveData

    fun getCommunityDetails(communityId: Int) = viewModelScope.launch {
        _getCommunityDetailsLiveData.postValue(Event(Resource.loading(null)))
        _getCommunityDetailsLiveData.postValue(
            Event(repo.getCommunityDetails(communityId))
        )
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

}