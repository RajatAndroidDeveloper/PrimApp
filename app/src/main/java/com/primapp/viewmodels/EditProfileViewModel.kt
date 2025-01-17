package com.primapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.primapp.PrimApp
import com.primapp.R
import com.primapp.model.auth.ReferenceResponseDataModel
import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.profile.EditProfileRequestModel
import com.primapp.repository.ProfileRepository
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class EditProfileViewModel @Inject constructor(
    errorFields: ErrorFields,
    application: Application,
    val repo: ProfileRepository
) : AndroidViewModel(application) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val editProfileRequestModel = MutableLiveData<EditProfileRequestModel>()

    init {
        errorFieldsLiveData.value = errorFields
        editProfileRequestModel.value = EditProfileRequestModel("", "", null, null, "", null)
    }

    fun validateData(): Boolean {
        val error = errorFieldsLiveData.value
        error?.errorFirstName = null
        error?.errorLastName = null
        error?.errorGender = null
        error?.errorCountry = null
        errorFieldsLiveData.value = error!!

        Log.i("anshul", "validating")

        val result = editProfileRequestModel.value?.isValidFormData()

        Log.i("anshul", "$result")

        when (result) {
            ValidationResults.EMPTY_FIRSTNAME -> {
                errorFieldsLiveData.value?.errorFirstName =
                    context.getString(R.string.valid_empty_firstname)
            }

            ValidationResults.EMPTY_LASTNAME -> {
                errorFieldsLiveData.value?.errorLastName =
                    context.getString(R.string.valid_empty_lastname)
            }

            ValidationResults.EMPTY_COUNTRY -> {
                errorFieldsLiveData.value?.errorCountry =
                    context.getString(R.string.valid_country)
            }


            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success Data : ${Gson().toJson(editProfileRequestModel.value)}")
                return true
            }
        }
        return false
    }

    // get reference data
    private var _referenceLiveData = MutableLiveData<Resource<ReferenceResponseDataModel>>()
    var referenceLiveData: LiveData<Resource<ReferenceResponseDataModel>> = _referenceLiveData

    fun getReferenceData(type: String) = viewModelScope.launch {
        _referenceLiveData.postValue(Resource.loading(null))
        _referenceLiveData.postValue(repo.getReferenceData(type))
    }

    // get reference data
    private var _editProfileLiveData = MutableLiveData<Resource<VerifyUserResponseModel>>()
    var editProfileLiveData: LiveData<Resource<VerifyUserResponseModel>> = _editProfileLiveData

    fun editProfile(userId: Int) = viewModelScope.launch {
        _editProfileLiveData.postValue(Resource.loading(null))
        _editProfileLiveData.postValue(repo.editProfile(userId, editProfileRequestModel.value!!))
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
        xAmzAlgorithm: String,
        xAmzCredential: String,
        xAmzDate: String,
        xAmzSignature: String,
        file: MultipartBody.Part?
    ) = viewModelScope.launch {
        _uploadAWSLiveData.postValue(Event(Resource.loading(null)))
        _uploadAWSLiveData.postValue(
            Event(repo.uploadtoAWS(url, key, accessKey,amzSecurityToken, policy, signature, xAmzAlgorithm, xAmzCredential, xAmzDate, xAmzSignature, file))
        )
    }

}