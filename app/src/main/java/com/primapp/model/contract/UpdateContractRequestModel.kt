package com.primapp.model.contract

import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import java.io.Serializable

data class UpdateContractRequestModel(

    @field:SerializedName("end_date")
    var endDate: String? = null,

    @field:SerializedName("scope_of_work")
    var scopeOfWork: String? = null,

    @field:SerializedName("price")
    var price: Double? = 0.0,

    @field:SerializedName("contract_status")
    var contractStatus: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("start_date")
    var startDate: String? = null
): Serializable {
    fun isValidFormData(): ValidationResults {
        if (name.isNullOrEmpty())
            return ValidationResults.EMPTY_CONTRACT_NAME

        if (scopeOfWork.isNullOrEmpty())
            return ValidationResults.EMPTY_SCOPE_PROJECT

        if (startDate.isNullOrEmpty())
            return ValidationResults.EMPTY_START_DATE

        if (endDate.isNullOrEmpty())
            return ValidationResults.EMPTY_END_DATE

        if (price == 0.0)
            return ValidationResults.EMPTY_CONTRACT_PRICE

        return ValidationResults.SUCCESS
    }
}

