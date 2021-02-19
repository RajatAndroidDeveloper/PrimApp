package com.primapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.primapp.model.notification.NotificationUIModel
import com.primapp.model.notification.dateFromTimeStamp
import com.primapp.repository.NotificationRepository
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.DateTimeUtils
import com.primapp.utils.ErrorFields
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationViewModel @Inject constructor(
    private val repo: NotificationRepository,
    app: Application,
    errorFields: ErrorFields
) : AndroidViewModel(app) {

    private var notificationLiveData: LiveData<PagingData<NotificationUIModel>>? = null

    fun getUserNotification(): LiveData<PagingData<NotificationUIModel>> {
        val lastResult = notificationLiveData
        if (lastResult != null) {
            return lastResult
        }
        val newResultLiveData: LiveData<PagingData<NotificationUIModel>> =
            repo.getUserNotifications().map { pagingData ->
                pagingData.map { NotificationUIModel.NotificationItem(it) }
            }.map {
                it.insertSeparators<NotificationUIModel.NotificationItem, NotificationUIModel> { before, after ->
                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }

                    if (before == null) {
                        // we're at the beginning of the list
                        return@insertSeparators NotificationUIModel.SeparatorItem(
                            DateTimeUtils.getDayAgoFromTimeStamp(after.notification.cdate).toString()
                        )
                    }

                    // check between 2 items
                    if (!before.dateFromTimeStamp!!.equals(after.dateFromTimeStamp)) {
                        NotificationUIModel.SeparatorItem(
                            DateTimeUtils.getDayAgoFromTimeStamp(after.notification.cdate).toString()
                        )
                    } else {
                        null
                    }
                }
            }.cachedIn(viewModelScope)
        notificationLiveData = newResultLiveData
        return newResultLiveData
    }

    private var _acceptRejectMentorshipLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var acceptRejectMentorshipLiveData: LiveData<Event<Resource<BaseDataModel>>> = _acceptRejectMentorshipLiveData

    fun acceptRejectMentorship(requestId: Int, action: String, message: String?) = viewModelScope.launch {
        _acceptRejectMentorshipLiveData.postValue(Event(Resource.loading(null)))
        _acceptRejectMentorshipLiveData.postValue(Event(repo.acceptRejectMentorship(requestId, action, message)))
    }

}