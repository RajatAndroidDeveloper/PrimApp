package com.primapp.model.dashboard

import com.google.gson.annotations.SerializedName

data class DashboardDetailsResponseModel(

	@field:SerializedName("content")
	val content: Content? = null
)

data class Content(

	@field:SerializedName("total_spend")
	val totalSpend: Double? = null,

	@field:SerializedName("total_earning")
	val totalEarning: Double? = null,

	@field:SerializedName("rating")
	val rating: Double? = null,

	@field:SerializedName("total_mentee_served")
	val totalMenteeServed: Int? = null
)
