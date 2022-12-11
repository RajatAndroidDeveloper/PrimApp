package com.primapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.primapp.PrimApp
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.portfolio.*
import com.primapp.repository.PortfolioRepository
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class PortfolioViewModel @Inject constructor(
    errorFields: ErrorFields,
    application: Application,
    val repo: PortfolioRepository
) : AndroidViewModel(application) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    //TO send files in mentoring portfolio
    val mentoringPortfolioRequestModel = MutableLiveData<MentoringPortfolioRequest>()

    init {
        errorFieldsLiveData.value = errorFields
        mentoringPortfolioRequestModel.value = MentoringPortfolioRequest(null, null, null)
    }

    private var _userPortfolioLiveData = MutableLiveData<Event<Resource<UserPortfolioResponse>>>()
    var userPortfolioLiveData: LiveData<Event<Resource<UserPortfolioResponse>>> = _userPortfolioLiveData

    fun getPortfolioData(userId: Int) = viewModelScope.launch {
        _userPortfolioLiveData.postValue(Event(Resource.loading(null)))
        _userPortfolioLiveData.postValue(Event(repo.getPortfolioData(userId)))
    }

    private var _addBenefitsLiveData = MutableLiveData<Event<Resource<AddBenefitResponse>>>()
    var addBenefitLiveData: LiveData<Event<Resource<AddBenefitResponse>>> = _addBenefitsLiveData

    fun addBenefit(benefitText: String) = viewModelScope.launch {
        _addBenefitsLiveData.postValue(Event(Resource.loading(null)))
        _addBenefitsLiveData.postValue(Event(repo.addBenefits(AddBenefitRequest(benefitText))))
    }

    private var _updateBenefitsLiveData = MutableLiveData<Event<Resource<AddBenefitResponse>>>()
    var updateBenefitsLiveData: LiveData<Event<Resource<AddBenefitResponse>>> = _updateBenefitsLiveData

    fun updateBenefit(benefitId: Int, benefitText: String) = viewModelScope.launch {
        _updateBenefitsLiveData.postValue(Event(Resource.loading(null)))
        _updateBenefitsLiveData.postValue(Event(repo.updateBenefits(benefitId, AddBenefitRequest(benefitText))))
    }

    private var _deleteBenefitsLiveData = MutableLiveData<Event<Resource<DeleteGenericResponse>>>()
    var deleteBenefitsLiveData: LiveData<Event<Resource<DeleteGenericResponse>>> = _deleteBenefitsLiveData

    fun deleteBenefit(benefitId: Int) = viewModelScope.launch {
        _deleteBenefitsLiveData.postValue(Event(Resource.loading(null)))
        _deleteBenefitsLiveData.postValue(Event(repo.deleteBenefits(benefitId)))
    }


    private var _addMentoringPortfolioLiveData = MutableLiveData<Event<Resource<AddMentoringPortfolioResponse>>>()
    var addMentoringPortfolioLiveData: LiveData<Event<Resource<AddMentoringPortfolioResponse>>> =
        _addMentoringPortfolioLiveData

    fun addMentoringPortfolio() = viewModelScope.launch {
        _addMentoringPortfolioLiveData.postValue(Event(Resource.loading(null)))
        _addMentoringPortfolioLiveData.postValue(Event(repo.addMentoringPortfolio(mentoringPortfolioRequestModel.value!!)))
    }

    private var _deleteMentoringPortfolioLiveData = MutableLiveData<Event<Resource<DeleteGenericResponse>>>()
    var deleteMentoringPortfolioLiveData: LiveData<Event<Resource<DeleteGenericResponse>>> = _deleteMentoringPortfolioLiveData

    fun deleteMentoringPortfolio(id: Int) = viewModelScope.launch {
        _deleteMentoringPortfolioLiveData.postValue(Event(Resource.loading(null)))
        _deleteMentoringPortfolioLiveData.postValue(Event(repo.deleteMentoringPortfolio(id)))
    }

    //-----To Upload file/image-----
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

    //-----END To Upload file/image-----
}