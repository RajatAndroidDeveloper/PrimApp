package com.primapp.model.contract

import com.google.gson.annotations.SerializedName

data class AcceptAmendRequestModel(

	@field:SerializedName("status")
    var status: String? = null,

	@field:SerializedName("action_reason")
    var actionReason: String? = null
)
