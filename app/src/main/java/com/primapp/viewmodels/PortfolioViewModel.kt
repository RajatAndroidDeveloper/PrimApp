package com.primapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.primapp.PrimApp
import com.primapp.model.portfolio.AddBenefitRequest
import com.primapp.model.portfolio.AddBenefitResponse
import com.primapp.model.portfolio.DeleteGenericResponse
import com.primapp.model.portfolio.UserPortfolioResponse
import com.primapp.repository.PortfolioRepository
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import kotlinx.coroutines.launch
import javax.inject.Inject

class PortfolioViewModel @Inject constructor(
    errorFields: ErrorFields,
    application: Application,
    val repo: PortfolioRepository
) : AndroidViewModel(application) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    init {
        errorFieldsLiveData.value = errorFields
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

}