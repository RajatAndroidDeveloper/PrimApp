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
import com.primapp.model.aws.PresignedURLResponseModel
import com.primapp.model.portfolio.*
import com.primapp.repository.PortfolioRepository
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
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
    val addExperienceRequestModel = MutableLiveData<AddExperienceRequest>()

    init {
        errorFieldsLiveData.value = errorFields
        mentoringPortfolioRequestModel.value = MentoringPortfolioRequest(null, null, null)
        addExperienceRequestModel.value = AddExperienceRequest(null, false, null, null, null, null, null)
    }

    fun validateData(): Boolean {
        val error = errorFieldsLiveData.value
        error?.errorTitle = null
        error?.errorJobType = null
        error?.errorCompnayName = null
        error?.errorLocation = null
        error?.errorYears = null
        error?.errorMonths = null
        errorFieldsLiveData.value = error

        Log.i("anshul", "validating")

        val result = addExperienceRequestModel.value?.isValidFormData()

        Log.i("anshul", "$result")

        when (result) {
            ValidationResults.EMPTY_JOB_TITLE -> {
                errorFieldsLiveData.value?.errorTitle =
                    context.getString(R.string.valid_empty_job_title)
            }

            ValidationResults.EMPTY_JOB_TYPE -> {
                errorFieldsLiveData.value?.errorJobType =
                    context.getString(R.string.valid_empty_job_type)
            }

            ValidationResults.EMPTY_COMPANY_NAME -> {
                errorFieldsLiveData.value?.errorCompnayName =
                    context.getString(R.string.valid_empty_company_name)
            }

            ValidationResults.EMPTY_LOCATION -> {
                errorFieldsLiveData.value?.errorLocation =
                    context.getString(R.string.valid_empty_location)
            }

            ValidationResults.EMPTY_YEARS -> {
                errorFieldsLiveData.value?.errorYears =
                    context.getString(R.string.valid_empty_years)
            }

            ValidationResults.EMPTY_MONTHS -> {
                errorFieldsLiveData.value?.errorMonths =
                    context.getString(R.string.valid_empty_months)
            }

            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success Data : ${Gson().toJson(addExperienceRequestModel.value)}")
                return true
            }
        }
        return false
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
    var deleteMentoringPortfolioLiveData: LiveData<Event<Resource<DeleteGenericResponse>>> =
        _deleteMentoringPortfolioLiveData

    fun deleteMentoringPortfolio(id: Int) = viewModelScope.launch {
        _deleteMentoringPortfolioLiveData.postValue(Event(Resource.loading(null)))
        _deleteMentoringPortfolioLiveData.postValue(Event(repo.deleteMentoringPortfolio(id)))
    }

    private var _addPortfolioExperienceLiveData = MutableLiveData<Event<Resource<AddExperienceResponse>>>()
    var addPortfolioExperienceLiveData: LiveData<Event<Resource<AddExperienceResponse>>> =
        _addPortfolioExperienceLiveData

    fun addPortfolioExperience() = viewModelScope.launch {
        _addPortfolioExperienceLiveData.postValue(Event(Resource.loading(null)))
        _addPortfolioExperienceLiveData.postValue(Event(repo.addPortfolioExperience(addExperienceRequestModel.value!!)))
    }

    private var _deleteExperienceLiveData = MutableLiveData<Event<Resource<DeleteGenericResponse>>>()
    var deleteExperienceLiveData: LiveData<Event<Resource<DeleteGenericResponse>>> = _deleteExperienceLiveData

    fun deleteExperience(id: Int) = viewModelScope.launch {
        _deleteExperienceLiveData.postValue(Event(Resource.loading(null)))
        _deleteExperienceLiveData.postValue(Event(repo.deleteExperience(id)))
    }

    private var _updateExperienceLiveData = MutableLiveData<Event<Resource<AddExperienceResponse>>>()
    var updateExperienceLiveData: LiveData<Event<Resource<AddExperienceResponse>>> = _updateExperienceLiveData

    fun updateExperience(id: Int) = viewModelScope.launch {
        _updateExperienceLiveData.postValue(Event(Resource.loading(null)))
        _updateExperienceLiveData.postValue(Event(repo.updateExperience(id, addExperienceRequestModel.value!!)))
    }

    private var _benefitSuggestionsLiveData = MutableLiveData<Event<Resource<BenefitSuggestionResponse>>>()
    var benefitSuggestionsLiveData: LiveData<Event<Resource<BenefitSuggestionResponse>>> = _benefitSuggestionsLiveData

    fun getBenefitSuggestions() = viewModelScope.launch {
        _benefitSuggestionsLiveData.postValue(Event(Resource.loading(null)))
        _benefitSuggestionsLiveData.postValue(Event(repo.getBenefitSuggestions()))
    }

    private var _skillsListLiveData = MutableLiveData<Event<Resource<SkillsNCertificateResponse>>>()
    var skillsListLiveData: LiveData<Event<Resource<SkillsNCertificateResponse>>> = _skillsListLiveData

    fun getSkillsList() = viewModelScope.launch {
        _skillsListLiveData.postValue(Event(Resource.loading(null)))
        _skillsListLiveData.postValue(Event(repo.getSkillsList()))
    }

    private var _addSkillLiveData = MutableLiveData<Event<Resource<AddSkillsResponse>>>()
    var addSkillLiveData: LiveData<Event<Resource<AddSkillsResponse>>> = _addSkillLiveData

    fun addSkill(id: Int) = viewModelScope.launch {
        _addSkillLiveData.postValue(Event(Resource.loading(null)))
        _addSkillLiveData.postValue(Event(repo.addSkill(AddSkillsRequest(arrayListOf(id)))))
    }

    private var _deleteSkillsLiveData = MutableLiveData<Event<Resource<DeleteGenericResponse>>>()
    var deleteSkillsLiveData: LiveData<Event<Resource<DeleteGenericResponse>>> = _deleteSkillsLiveData

    fun deleteSkillFromPortfolio(id: Int) = viewModelScope.launch {
        _deleteSkillsLiveData.postValue(Event(Resource.loading(null)))
        _deleteSkillsLiveData.postValue(Event(repo.deleteSkillFromPortfolio(id)))
    }

    // get reference data
    private var _referenceLiveData = MutableLiveData<Resource<ReferenceResponseDataModel>>()
    var referenceLiveData: LiveData<Resource<ReferenceResponseDataModel>> = _referenceLiveData

    fun getReferenceData(type: String) = viewModelScope.launch {
        _referenceLiveData.postValue(Resource.loading(null))
        _referenceLiveData.postValue(repo.getReferenceData(type))
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