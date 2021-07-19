package com.primapp.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.model.post.ReportPostRequestModel
import com.primapp.model.rewards.RewardsResponseModel
import com.primapp.repository.SplashRepository
import com.primapp.retrofit.base.BaseDataModel
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

    private var _reportPostLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var reportPostLiveData: LiveData<Event<Resource<BaseDataModel>>> = _reportPostLiveData

    fun reportPost(
        communityId: Int,
        postId: Int,
        reportType: Int,
        reportText: String?
    ) = viewModelScope.launch {
        _reportPostLiveData.postValue(Event(Resource.loading(null)))
        _reportPostLiveData.postValue(
            Event(
                repo.reportPost(
                    communityId,
                    postId,
                    ReportPostRequestModel(reportText, reportType)
                )
            )
        )
    }

    //Get rewards data
    private var _getRewardsLiveData = MutableLiveData<Event<Resource<RewardsResponseModel>>>()
    var rewardsLiveData: LiveData<Event<Resource<RewardsResponseModel>>> = _getRewardsLiveData

    fun getRewardsData() = viewModelScope.launch {
        _getRewardsLiveData.postValue(Event(Resource.loading(null)))
        _getRewardsLiveData.postValue(Event(repo.getRewardsData()))
    }
}