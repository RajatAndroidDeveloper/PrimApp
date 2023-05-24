package com.primapp.model.earning

import com.google.gson.annotations.SerializedName

data class EarningResponseModel(

	@field:SerializedName("content")
	val content: List<ContentItem?>? = null
)

data class ContentItem(

	@field:SerializedName("cdate")
	val cdate: String? = null,

	@field:SerializedName("amount_earned")
	val amountEarned: Any? = null,

	@field:SerializedName("contract")
	val contract: Contract? = null,

	@field:SerializedName("rating")
	val rating: Any? = null,

	@field:SerializedName("rating_reason")
	val ratingReason: Any? = null,

	@field:SerializedName("rated_on")
	val ratedOn: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("udate")
	val udate: String? = null,

	@field:SerializedName("spend_by")
	val spendBy: SpendBy? = null
)

data class CreatedBy(

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("get_image_url")
	val getImageUrl: String? = null
)

data class SpendBy(

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("get_image_url")
	val getImageUrl: String? = null
)

data class Contract(

	@field:SerializedName("end_date")
	val endDate: Long? = null,

	@field:SerializedName("cdate")
	val cdate: String? = null,

	@field:SerializedName("scope_of_work")
	val scopeOfWork: String? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("contract_status")
	val contractStatus: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("udate")
	val udate: String? = null,

	@field:SerializedName("created_by")
	val createdBy: CreatedBy? = null,

	@field:SerializedName("start_date")
	val startDate: Long? = null
)
