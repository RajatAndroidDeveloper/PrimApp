package com.primapp.model.contract

import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import java.io.Serializable

data class AmendContractRequestModel(

	@field:SerializedName("amount")
	var amount: Double? = 0.0,

	@field:SerializedName("contract")
	var contract: Int? = null
): Serializable {
	fun isValidFormData(): ValidationResults {
		if (amount == 0.0)
			return ValidationResults.EMPTY_CONTRACT_PRICE

		return ValidationResults.SUCCESS
	}
}

