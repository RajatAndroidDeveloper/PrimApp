package com.primapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.primapp.PrimApp
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.settings.ReportIssueRequestModel
import com.primapp.repository.ProfileRepository
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class ReportIssueViewModel @Inject constructor(
    errorFields: ErrorFields,
    application: Application,
    val repo: ProfileRepository
) : AndroidViewModel(application) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val reportIssueRequestModel = MutableLiveData<ReportIssueRequestModel>()

    init {
        errorFieldsLiveData.value = errorFields
        reportIssueRequestModel.value = ReportIssueRequestModel(null, "")
    }

    private var _reportIssueLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var reportIssueLiveData: LiveData<Event<Resource<BaseDataModel>>> = _reportIssueLiveData

    fun reportIssue() = viewModelScope.launch {
        _reportIssueLiveData.postValue(Event(Resource.loading(null)))
        _reportIssueLiveData.postValue(Event(repo.reportIssue(reportIssueRequestModel.value!!)))
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