package com.primapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.primapp.PrimApp
import com.primapp.R
import com.primapp.model.auth.ForgotDataRequestModel
import com.primapp.model.auth.ForgotDataResponseModel
import com.primapp.model.auth.SignUpRequestDataModel
import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.repository.RegistrationRepository
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import kotlinx.coroutines.launch
import javax.inject.Inject

class ForgotDataViewModel @Inject constructor(
    errorFields: ErrorFields,
    application: Application,
    val repo: RegistrationRepository
) :
    AndroidViewModel(application) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val forgotDataModel = MutableLiveData<ForgotDataRequestModel>()

    init {
        errorFieldsLiveData.value = errorFields
        forgotDataModel.value = ForgotDataRequestModel("")
    }

    fun validate(): Boolean {
        val error = errorFieldsLiveData.value
        error?.errorEmail = null
        errorFieldsLiveData.value = error

        Log.i("anshul", "validating")

        val result = forgotDataModel.value?.isValidFormData()

        Log.i("anshul", "$result")

        when (result) {
            ValidationResults.EMPTY_EMAIL, ValidationResults.EMAIL_NOT_VALID -> {
                errorFieldsLiveData.value?.errorEmail =
                    context.getString(R.string.valid_email)
            }

            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success")
                return true
            }
        }
        return false
    }

    private var _forgotUsernameLiveData = MutableLiveData<Event<Resource<ForgotDataResponseModel>>>()
    var forgotUsernameLiveData: LiveData<Event<Resource<ForgotDataResponseModel>>> =
        _forgotUsernameLiveData

    fun forgotUsername(forgotDataRequestModel: ForgotDataRequestModel) = viewModelScope.launch {
        _forgotUsernameLiveData.postValue(Event((Resource.loading(null))))
        _forgotUsernameLiveData.postValue(Event(repo.forgotUsername(forgotDataRequestModel)))
    }

    private var _forgotPasswordLiveData = MutableLiveData<Event<Resource<ForgotDataResponseModel>>>()
    var forgotPasswordLiveData: LiveData<Event<Resource<ForgotDataResponseModel>>> =
        _forgotPasswordLiveData

    fun forgotPassword(forgotDataRequestModel: ForgotDataRequestModel) = viewModelScope.launch {
        _forgotPasswordLiveData.postValue(Event((Resource.loading(null))))
        _forgotPasswordLiveData.postValue(Event(repo.forgotPassword(forgotDataRequestModel)))
    }


}