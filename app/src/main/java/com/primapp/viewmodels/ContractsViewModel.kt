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
import com.primapp.model.contract.CreateContractRequestModel
import com.primapp.repository.ContractRepository
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContractsViewModel  @Inject constructor(
    private val repo: ContractRepository,
    app: Application,
    errorFields: ErrorFields) : AndroidViewModel(app) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val createContractRequestModel = MutableLiveData<CreateContractRequestModel>()

    init {
        errorFieldsLiveData.value = errorFields
        createContractRequestModel.value =
            CreateContractRequestModel("", 0.0, "", "", "")
    }

    fun validateData(): Boolean {
        val error = errorFieldsLiveData.value
        error?.errorContractName = null
        error?.errorScopeOfProject = null
        error?.errorStartDate = null
        error?.errorEndDate = null
        error?.errorContractPrice = null
        errorFieldsLiveData.value = error

        Log.i("anshul", "validating")

        val result = createContractRequestModel.value?.isValidFormData()

        Log.i("anshul", "$result")

        when (result) {
            ValidationResults.EMPTY_CONTRACT_NAME -> {
                errorFieldsLiveData.value?.errorContractName =
                    context.getString(R.string.valid_empty_contract_name)
            }

            ValidationResults.EMPTY_SCOPE_PROJECT -> {
                errorFieldsLiveData.value?.errorScopeOfProject =
                    context.getString(R.string.valid_empty_scope_of_project)
            }

            ValidationResults.EMPTY_START_DATE -> {
                errorFieldsLiveData.value?.errorStartDate =
                    context.getString(R.string.valid_empty_start_date_project)
            }

            ValidationResults.EMPTY_END_DATE -> {
                errorFieldsLiveData.value?.errorEndDate =
                    context.getString(R.string.valid_empty_end_date_project)
            }

            ValidationResults.EMPTY_CONTRACT_PRICE -> {
                errorFieldsLiveData.value?.errorContractPrice =
                    context.getString(R.string.valid_empty_price)
            }


            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success Data : ${Gson().toJson(createContractRequestModel.value)}")
                createContract()
                return true
            }
        }
        return false
    }

    private var _createContractLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var createContractLiveData: LiveData<Event<Resource<BaseDataModel>>> = _createContractLiveData

    fun createContract() = viewModelScope.launch {
        _createContractLiveData.postValue(Event(Resource.loading(null)))
        _createContractLiveData.postValue(Event(repo.createContract(createContractRequestModel.value!!)))
    }

}