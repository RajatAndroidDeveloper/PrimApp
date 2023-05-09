package com.primapp.model.rating

import com.google.gson.annotations.SerializedName

data class AllRatingsResponse(

	@field:SerializedName("content")
	val content: List<ContentItem?>? = null
)

data class RatedBy(

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_online_status")
	val userOnlineStatus: String? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("get_image_url")
	val getImageUrl: String? = null
)

data class CreatedBy(

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_online_status")
	val userOnlineStatus: String? = null,

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

data class ContentItem(

	@field:SerializedName("rated_by")
	val ratedBy: RatedBy? = null,

	@field:SerializedName("contract")
	val contract: Contract? = null,

	@field:SerializedName("rating")
	val rating: Double? = null,

	@field:SerializedName("rating_reason")
	val ratingReason: String? = null
)
