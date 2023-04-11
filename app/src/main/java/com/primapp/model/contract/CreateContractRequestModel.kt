package com.primapp.model.contract

import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import java.io.Serializable

data class CreateContractRequestModel (
    @SerializedName("name")
    var name: String?,
    @SerializedName("price")
    var price: Double = 0.0,
    @SerializedName("start_date")
    var startDate: String?,
    @SerializedName("scope_of_work")
    var scopeOfProject: String?,
    @SerializedName("end_date")
    var endDate: String?
    ): Serializable {
    fun isValidFormData(): ValidationResults {
        if (name.isNullOrEmpty())
            return ValidationResults.EMPTY_CONTRACT_NAME

        if (scopeOfProject.isNullOrEmpty())
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
