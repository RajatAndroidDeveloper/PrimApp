package com.primapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.primapp.PrimApp
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

}