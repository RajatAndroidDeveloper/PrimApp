package com.primapp.model.contract

import com.google.gson.annotations.SerializedName

data class UpdateContractStatusRequestModel(
    @field:SerializedName("contract_status")
    var contractStatus: String? = null,
)