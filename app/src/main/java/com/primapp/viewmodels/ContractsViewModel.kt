package com.primapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.primapp.PrimApp
import com.primapp.R
import com.primapp.model.contract.*
import com.primapp.repository.ContractRepository
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContractsViewModel @Inject constructor(
    private val repo: ContractRepository,
    app: Application,
    errorFields: ErrorFields
) : AndroidViewModel(app) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val createContractRequestModel = MutableLiveData<CreateContractRequestModel>()

    val amendContractRequestModel = MutableLiveData<AmendContractRequestModel>()

    val acceptContractRequestModel = MutableLiveData<AcceptContractRequestModel>()

    val acceptAmendRequestModel = MutableLiveData<AcceptAmendRequestModel>()

    init {
        errorFieldsLiveData.value = errorFields
        createContractRequestModel.value = CreateContractRequestModel("", 0.0, "", "", "")
        amendContractRequestModel.value = AmendContractRequestModel(0.0, 0)
        acceptContractRequestModel.value = AcceptContractRequestModel(0, 0.0, "")
        acceptAmendRequestModel.value = AcceptAmendRequestModel("")
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

    fun validateContractAmendData(): Boolean {
        val error = errorFieldsLiveData.value
        error?.errorContractPrice = null

        errorFieldsLiveData.value = error
        val result = amendContractRequestModel.value?.isValidFormData()
        when (result) {
            ValidationResults.EMPTY_CONTRACT_PRICE -> {
                errorFieldsLiveData.value?.errorContractPrice =
                    context.getString(R.string.valid_empty_price)
            }
            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success Data : ${Gson().toJson(acceptContractRequestModel.value)}")
                amendContract(amendContractRequestModel.value!!)
                return true
            }
        }
        return false
    }

    fun validContractAcceptData(): Boolean {
        val result = acceptContractRequestModel.value?.isValidFormData()
        when (result) {
            ValidationResults.EMPTY_CONTRACT_STATUS -> {

            }
            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success Data : ${Gson().toJson(acceptContractRequestModel.value)}")
                acceptContract(acceptContractRequestModel.value!!)
                return true
            }
        }
        return false
    }

    fun acceptDeclineAmendRequest(contractId: Int) {
        acceptAmendRequest(contractId, acceptAmendRequestModel.value!!)
    }

    private var _createContractLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var createContractLiveData: LiveData<Event<Resource<BaseDataModel>>> = _createContractLiveData

    fun createContract() = viewModelScope.launch {
        _createContractLiveData.postValue(Event(Resource.loading(null)))
        _createContractLiveData.postValue(Event(repo.createContract(createContractRequestModel.value!!)))
    }

    // get Post List
    private var allContractLiveData: LiveData<PagingData<ResultsItem>>? = null
    fun getAllContracts(contractType: String): LiveData<PagingData<ResultsItem>> {
        val newResultLiveData: LiveData<PagingData<ResultsItem>> =
            repo.getAllContracts(contractType).cachedIn(viewModelScope)
        allContractLiveData = newResultLiveData
        return newResultLiveData
    }

    private var _contractDetailLiveData = MutableLiveData<Event<Resource<ContractDetailResponseModel>>>()
    var contractDetailLiveData: LiveData<Event<Resource<ContractDetailResponseModel>>> = _contractDetailLiveData

    fun getContractDetails(contractId: Int) = viewModelScope.launch {
        _contractDetailLiveData.postValue(Event(Resource.loading(null)))
        _contractDetailLiveData.postValue(Event(repo.getContractDetails(contractId)))
    }

    private var _amendContractLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var amendContractLiveData: LiveData<Event<Resource<BaseDataModel>>> = _amendContractLiveData

    fun amendContract(amendContractRequestModel: AmendContractRequestModel) = viewModelScope.launch {
        _amendContractLiveData.postValue(Event(Resource.loading(null)))
        _amendContractLiveData.postValue(Event(repo.amendContract(amendContractRequestModel)))
    }

    private var _acceptContractLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var acceptContractLiveData: LiveData<Event<Resource<BaseDataModel>>> = _acceptContractLiveData

    fun acceptContract(acceptContractRequestModel: AcceptContractRequestModel) = viewModelScope.launch {
        _acceptContractLiveData.postValue(Event(Resource.loading(null)))
        _acceptContractLiveData.postValue(Event(repo.acceptContract(acceptContractRequestModel)))
    }

    private var _acceptAmendContractLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var acceptAmendContractLiveData: LiveData<Event<Resource<BaseDataModel>>> = _acceptAmendContractLiveData

    fun acceptAmendRequest(contractId: Int, acceptAmendRequestModel: AcceptAmendRequestModel) = viewModelScope.launch {
        _acceptAmendContractLiveData.postValue(Event(Resource.loading(null)))
        _acceptAmendContractLiveData.postValue(Event(repo.acceptAmendRequest(contractId, acceptAmendRequestModel)))
    }

}