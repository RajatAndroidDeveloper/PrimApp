package com.primapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.primapp.PrimApp
import com.primapp.R
import com.primapp.model.auth.LoginRequestDataModel
import com.primapp.model.auth.SignUpRequestDataModel
import com.primapp.model.auth.VerifyUserResponseModel
import com.primapp.repository.RegistrationRepository
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    errorFields: ErrorFields,
    application: Application,
    val repo: RegistrationRepository
) : AndroidViewModel(application) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val loginRequestDataModel = MutableLiveData<LoginRequestDataModel>()

    init {
        errorFieldsLiveData.value = errorFields
        loginRequestDataModel.value = LoginRequestDataModel("", "","android","")
    }

    fun validateLoginUser() {
        val error = errorFieldsLiveData.value
        error?.errorUsername = null
        error?.errorPassword = null
        errorFieldsLiveData.value = error!!

        Log.i("anshul", "validating")

        val result = loginRequestDataModel.value?.isValidFormData()

        Log.i("anshul", "$result")

        when (result) {
            ValidationResults.EMPTY_USERNAME -> {
                errorFieldsLiveData.value?.errorUsername =
                    context.getString(R.string.valid_empty_username)
            }

            ValidationResults.EMPTY_PASSWORD -> {
                errorFieldsLiveData.value?.errorPassword =
                    context.getString(R.string.valid_password)
            }
            ValidationResults.INVALID_PASSWORD -> {
                errorFieldsLiveData.value?.errorPassword =
                    context.getString(R.string.invalid_password)
            }
            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success")
                loginRequestDataModel.value?.deviceId = repo.token
                loginUser()
            }
            else -> {

            }
        }

    }

    private var _loginUserLiveData = MutableLiveData<Event<Resource<VerifyUserResponseModel>>>()
    var loginUserLiveData: LiveData<Event<Resource<VerifyUserResponseModel>>> = _loginUserLiveData

    fun loginUser() = viewModelScope.launch {
        _loginUserLiveData.postValue(Event(Resource.loading(null)))
        _loginUserLiveData.postValue(Event(repo.loginUser(loginRequestDataModel.value!!)))
    }

}