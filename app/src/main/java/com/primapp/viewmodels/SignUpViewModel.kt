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
import com.primapp.model.auth.SignUpRequestDataModel
import com.primapp.repository.RegistrationRepository
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import kotlinx.coroutines.launch
import javax.inject.Inject


class SignUpViewModel @Inject constructor(
    errorFields: ErrorFields,
    application: Application,
    val repo: RegistrationRepository
) :
    AndroidViewModel(application) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val signUpRequestDataModel = MutableLiveData<SignUpRequestDataModel>()

    init {
        errorFieldsLiveData.value = errorFields
        signUpRequestDataModel.value =
            SignUpRequestDataModel("", "", "", "", null, "", null, "", "", "", "android", null)
    }

    fun validateSignUpData(): Boolean {
        val error = errorFieldsLiveData.value
        error?.errorFirstName = null
        error?.errorLastName = null
        error?.errorUsername = null
        error?.errorEmail = null
        error?.errorGender = null
        error?.errorDOB = null
        error?.errorCountry = null
        error?.errorPassword = null
        error?.errorConfirmPassword = null
        error?.errorPrivacyPolicy = null
        errorFieldsLiveData.value = error!!

        Log.i("anshul", "validating")

        val result = signUpRequestDataModel.value?.isValidFormData()

        Log.i("anshul", "$result")

        when (result) {
            ValidationResults.EMPTY_FIRSTNAME -> {
                errorFieldsLiveData.value?.errorFirstName =
                    context.getString(R.string.valid_empty_firstname)
            }

            ValidationResults.EMPTY_LASTNAME -> {
                errorFieldsLiveData.value?.errorLastName =
                    context.getString(R.string.valid_empty_lastname)
            }

            ValidationResults.EMPTY_USERNAME -> {
                errorFieldsLiveData.value?.errorUsername =
                    context.getString(R.string.valid_empty_username)
            }

            ValidationResults.INVALID_USERNAME -> {
                errorFieldsLiveData.value?.errorUsername =
                    context.getString(R.string.valid_username)
            }

            ValidationResults.EMPTY_EMAIL, ValidationResults.EMAIL_NOT_VALID -> {
                errorFieldsLiveData.value?.errorEmail =
                    context.getString(R.string.valid_email)
            }

            ValidationResults.EMPTY_GENDER -> {
                errorFieldsLiveData.value?.errorGender =
                    context.getString(R.string.valid_gender)
            }

            ValidationResults.EMPTY_DOB -> {
                errorFieldsLiveData.value?.errorDOB =
                    context.getString(R.string.valid_empty_dob)
            }

            ValidationResults.EMPTY_COUNTRY -> {
                errorFieldsLiveData.value?.errorCountry =
                    context.getString(R.string.valid_country)
            }

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
                signUpRequestDataModel.value?.deviceId = repo.token

                Log.i("anshul", "Success Data : ${Gson().toJson(signUpRequestDataModel.value)}")
                return true
            }
        }
        return false
    }


    // get reference data
    private var _referenceLiveData = MutableLiveData<Resource<ReferenceResponseDataModel>>()
    var referenceLiveData: LiveData<Resource<ReferenceResponseDataModel>> = _referenceLiveData

    fun getReferenceData(type: String) = viewModelScope.launch {
        _referenceLiveData.postValue(Resource.loading(null))
        _referenceLiveData.postValue(repo.getReferenceData(type))
    }

    private var _signUpLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var signUpLiveDataLiveData: LiveData<Event<Resource<BaseDataModel>>> = _signUpLiveData

    fun signUpUser() = viewModelScope.launch {
        _signUpLiveData.postValue(Event(Resource.loading(null)))
        _signUpLiveData.postValue(Event(repo.signUpUser(signUpRequestDataModel.value!!)))
    }

}