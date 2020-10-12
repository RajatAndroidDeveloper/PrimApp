package com.primapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.primapp.PrimApp
import com.primapp.R
import com.primapp.model.auth.LoginRequestDataModel
import com.primapp.model.auth.SignUpRequestDataModel
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import javax.inject.Inject


class SignUpViewModel @Inject constructor(errorFields: ErrorFields, application: Application) :
    AndroidViewModel(application) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val signUpRequestDataModel = MutableLiveData<SignUpRequestDataModel>()

    init {
        errorFieldsLiveData.value = errorFields
        signUpRequestDataModel.value =
            SignUpRequestDataModel("", "", "", "", "", "", "", "", "", "", "android")
    }

    fun signUpUser(): Boolean {
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
        errorFieldsLiveData.value = error

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
                //loginRequestDataModel.value?.fcmToken = repo.token
                //loginUserSendRequest()

                Log.i("anshul", "Success Data : ${Gson().toJson(signUpRequestDataModel.value)}")
                return true
            }
        }
        return false
    }

}