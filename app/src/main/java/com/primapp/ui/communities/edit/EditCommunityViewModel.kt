package com.primapp.ui.communities.edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.primapp.PrimApp
import com.primapp.R
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.community.CommunityDetailsResponseModel
import com.primapp.model.community.CreateCommunityRequestModel
import com.primapp.model.community.EditCommunityRequestModel
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


class EditCommunityViewModel @Inject constructor(
    private val repo: CommunitiesRepository,
    app: Application,
    errorFields: ErrorFields
) : AndroidViewModel(app) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val editCommunityRequestModel = MutableLiveData<EditCommunityRequestModel>()

    init {
        errorFieldsLiveData.value = errorFields
        editCommunityRequestModel.value =
            EditCommunityRequestModel("", "", "")
    }

    fun validateEditCommunity(): Boolean {
        val error = errorFieldsLiveData.value
        error?.errorCommunityName = null
        error?.errorCommunityDescription = null
        errorFieldsLiveData.value = error

        val result = editCommunityRequestModel.value?.isValidFormData()

        when (result) {
            ValidationResults.EMPTY_COMMUNITY_NAME -> {
                errorFieldsLiveData.value?.errorCommunityName =
                    context.getString(R.string.valid_community_name)
            }

            ValidationResults.EMPTY_COMMUNITY_DESCRIPTION -> {
                errorFieldsLiveData.value?.errorCommunityDescription =
                    context.getString(R.string.valid_community_description)
            }

            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success Data : ${Gson().toJson(editCommunityRequestModel.value)}")
                return true
            }
        }

        return false
    }

    // Edit Community
    private var _editCommunityLiveData = MutableLiveData<Event<Resource<CommunityDetailsResponseModel>>>()
    var editCommunityLiveData: LiveData<Event<Resource<CommunityDetailsResponseModel>>> =
        _editCommunityLiveData

    fun editCommunity(communityId: Int) = viewModelScope.launch {
        _editCommunityLiveData.postValue(Event(Resource.loading(null)))
        _editCommunityLiveData.postValue(
            Event(
                repo.editCommunity(communityId, editCommunityRequestModel.value!!)
            )
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
            Event(repo.uploadtoAWS(url, key, accessKey,amzSecurityToken, policy, signature, file))
        )
    }
}