package com.primapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.primapp.PrimApp
import com.primapp.R
import com.primapp.model.auth.LoginRequestDataModel
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import javax.inject.Inject

class LoginViewModel @Inject constructor(errorFields: ErrorFields, application: Application) :
    AndroidViewModel(application) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val loginRequestDataModel = MutableLiveData<LoginRequestDataModel>()

    init {
        errorFieldsLiveData.value = errorFields
        loginRequestDataModel.value = LoginRequestDataModel("","")
    }

    fun loginUser() {
        val error = errorFieldsLiveData.value
        error?.errorUsername = null
        error?.errorPassword = null
        errorFieldsLiveData.value = error

        Log.i("anshul", "validating")

        val result = loginRequestDataModel.value?.isValidFormData()

        Log.i("anshul", "$result")

        when (result) {
            ValidationResults.EMPTY_USERNAME -> {
                errorFieldsLiveData.value?.errorUsername = context.getString(R.string.valid_empty_username)
            }

            ValidationResults.EMPTY_PASSWORD -> {
                errorFieldsLiveData.value?.errorPassword = context.getString(R.string.valid_password)
            }
            ValidationResults.INVALID_PASSWORD -> {
                errorFieldsLiveData.value?.errorPassword = context.getString(R.string.invalid_password)
            }
            ValidationResults.SUCCESS -> {
                //loginRequestDataModel.value?.fcmToken = repo.token
                //loginUserSendRequest()

                Log.i("anshul", "Success")
            }
            else -> {

            }
        }

    }

}