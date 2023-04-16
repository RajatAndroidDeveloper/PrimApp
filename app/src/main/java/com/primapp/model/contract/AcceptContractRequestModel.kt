package com.primapp.model.contract

import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import java.io.Serializable

data class AcceptContractRequestModel(

	@field:SerializedName("contract")
	var contract: Int? = null,

	@field:SerializedName("accepted_price")
	var acceptedPrice: Double? = null,

	@field:SerializedName("status")
	var status: String? = null
) : Serializable {
	fun isValidFormData(): ValidationResults {
		if (status.isNullOrEmpty())
			return ValidationResults.EMPTY_CONTRACT_STATUS

		return ValidationResults.SUCCESS
	}
}