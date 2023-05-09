package com.primapp.model.rating

import com.google.gson.annotations.SerializedName

data class SubmitRatingRequestModel(

	@field:SerializedName("rating")
    var rating: Double? = null,

	@field:SerializedName("rating_reason")
	var ratingReason: String? = null
)
