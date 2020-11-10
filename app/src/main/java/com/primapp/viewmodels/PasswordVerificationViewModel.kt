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
import com.primapp.model.auth.PasswordVerificationRequestModel
import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.repository.RegistrationRepository
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import kotlinx.coroutines.launch
import javax.inject.Inject


class PasswordVerificationViewModel @Inject constructor(
    errorFields: ErrorFields,
    application: Application,
    val repo: RegistrationRepository
) :
    AndroidViewModel(application) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val passwordVerificationRequestModel = MutableLiveData<PasswordVerificationRequestModel>()

    init {
        errorFieldsLiveData.value = errorFields
        passwordVerificationRequestModel.value = PasswordVerificationRequestModel("", "")
    }

    fun validatePasswords(): Boolean {
        val error = errorFieldsLiveData.value
        error?.errorPassword = null
        error?.errorConfirmPassword = null
        errorFieldsLiveData.value = error

        Log.i("anshul", "validating")

        val result = passwordVerificationRequestModel.value?.isValidFormData()

        Log.i("anshul", "$result")

        when (result) {
            ValidationResults.EMPTY_PASSWORD -> {
                errorFieldsLiveData.value?.errorPassword =
                    context.getString(R.string.valid_password)
            }
            ValidationResults.INVALID_PASSWORD -> {
                errorFieldsLiveData.value?.errorPassword =
                    context.getString(R.string.invalid_password)
            }
            ValidationResults.EMPTY_CONFIRM_PASSWORD -> {
                errorFieldsLiveData.value?.errorConfirmPassword =
                    context.getString(R.string.valid_password)
            }
            ValidationResults.INVALID_CONFIRM_PASSWORD -> {
                errorFieldsLiveData.value?.errorConfirmPassword =
                    context.getString(R.string.invalid_password)
            }

            ValidationResults.PASS_NOT_MATCH -> {
                errorFieldsLiveData.value?.errorConfirmPassword =
                    context.getString(R.string.valid_pass_not_match)
            }
            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success Data : ${Gson().toJson(passwordVerificationRequestModel.value)}")
                return true
            }
        }
        return false
    }

    private var _changePasswordLiveData = MutableLiveData<Event<Resource<VerifyUserResponseModel>>>()
    var changePasswordLiveData: LiveData<Event<Resource<VerifyUserResponseModel>>> =
        _changePasswordLiveData

    fun changePassword(userId: String) =
        viewModelScope.launch {
            _changePasswordLiveData.postValue(Event(Resource.loading(null)))
            _changePasswordLiveData.postValue(
                Event(repo.changePassword(userId, passwordVerificationRequestModel.value!!))
            )
        }
}