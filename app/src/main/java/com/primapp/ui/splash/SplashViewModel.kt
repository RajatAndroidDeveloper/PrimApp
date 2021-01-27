package com.primapp.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.repository.SplashRepository
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(val repo: SplashRepository) : ViewModel() {

    //Get user data
    private var _getUserLiveData = MutableLiveData<Event<Resource<VerifyUserResponseModel>>>()
    var userLiveData: LiveData<Event<Resource<VerifyUserResponseModel>>> = _getUserLiveData

    fun getUserData(userId: Int) = viewModelScope.launch {
        _getUserLiveData.postValue(Event(Resource.loading(null)))
        _getUserLiveData.postValue(Event(repo.getUserData(userId)))
    }
}