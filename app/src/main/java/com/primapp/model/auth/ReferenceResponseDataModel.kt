package com.primapp.model.auth

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel


data class ReferenceResponseDataModel(
    @SerializedName("content")
    val content: Content?
) : BaseDataModel()

data class Content(
    @SerializedName("entity")
    val entity: String?,
    @SerializedName("items")
    val referenceItemsList: ArrayList<ReferenceItems>?
)

data class ReferenceItems(
    @SerializedName("item_id")
    val itemId: Int?,
    @SerializedName("item_value")
    val itemValue: String,
    @SerializedName("item_text")
    val itemText: String
)