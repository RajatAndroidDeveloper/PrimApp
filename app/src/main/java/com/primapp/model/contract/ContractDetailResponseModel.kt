package com.primapp.model.contract

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel

data class ContractDetailResponseModel(
    @SerializedName("content")
    val content: ResultsItem
) : BaseDataModel()
