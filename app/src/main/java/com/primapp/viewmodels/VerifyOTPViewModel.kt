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
import com.primapp.model.auth.SignUpRequestDataModel
import com.primapp.model.auth.VerifyForgotPasswordResponse
import com.primapp.model.auth.VerifyOTPRequestModel
import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.repository.RegistrationRepository
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import kotlinx.coroutines.launch
import javax.inject.Inject

class VerifyOTPViewModel @Inject constructor(
    errorFields: ErrorFields,
    application: Application,
    val repo: RegistrationRepository
) :
    AndroidViewModel(application) {
    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val verifyOTPRequestModel = MutableLiveData<VerifyOTPRequestModel>()

    init {
        errorFieldsLiveData.value = errorFields
        verifyOTPRequestModel.value = VerifyOTPRequestModel("")
    }

    fun validateOTPData(): Boolean {
        val error = errorFieldsLiveData.value
        error?.errorOTP = null
        errorFieldsLiveData.value = error

        Log.i("anshul", "validating")

        val result = verifyOTPRequestModel.value?.isValidFormData()

        Log.i("anshul", "$result")

        when (result) {
            ValidationResults.EMPTY_FIELD -> {
                errorFieldsLiveData.value?.errorOTP = context.getString(R.string.valid_empty_otp)
            }

            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success Data : ${Gson().toJson(verifyOTPRequestModel.value)}")
                return true
            }
        }
        return false
    }

    private var _verifyOTPLiveData = MutableLiveData<Resource<VerifyUserResponseModel>>()
    var verifyOTPLiveData: LiveData<Resource<VerifyUserResponseModel>> = _verifyOTPLiveData

    fun verifyOTPForSignUp(signUpRequestDataModel: SignUpRequestDataModel) = viewModelScope.launch {
        _verifyOTPLiveData.postValue(Resource.loading(null))
        _verifyOTPLiveData.postValue(repo.verifyUser(signUpRequestDataModel))
    }

    private var _forgotUsernameVerifyOTPLiveData = MutableLiveData<Resource<BaseDataModel>>()
    var forgotUsernameVerifyOTP: LiveData<Resource<BaseDataModel>> =
        _forgotUsernameVerifyOTPLiveData

    fun forgotUsernameVerifyOTP(userId: String) =
        viewModelScope.launch {
            _forgotUsernameVerifyOTPLiveData.postValue(Resource.loading(null))
            _forgotUsernameVerifyOTPLiveData.postValue(
                repo.forgotUsernameVerify(userId, verifyOTPRequestModel.value!!)
            )
        }

    private var _forgotPasswordVerifyOTPLiveData = MutableLiveData<Event<Resource<VerifyForgotPasswordResponse>>>()
    var forgotPasswordVerifyOTP: LiveData<Event<Resource<VerifyForgotPasswordResponse>>> =
        _forgotPasswordVerifyOTPLiveData

    fun forgotPasswordVerifyOTP(userId: String) =
        viewModelScope.launch {
            _forgotPasswordVerifyOTPLiveData.postValue(Event(Resource.loading(null)))
            _forgotPasswordVerifyOTPLiveData.postValue(
                Event(repo.forgotPasswordVerify(userId, verifyOTPRequestModel.value!!))
            )
        }


    private var _resendOTPLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var resendOTPLiveData: LiveData<Event<Resource<BaseDataModel>>> =
        _resendOTPLiveData

    fun resendOTP(email: String) =
        viewModelScope.launch {
            _resendOTPLiveData.postValue(Event(Resource.loading(null)))
            _resendOTPLiveData.postValue(
                Event(repo.resendOTP(email))
            )
        }


}