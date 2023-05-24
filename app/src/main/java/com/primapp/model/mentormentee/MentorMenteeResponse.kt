package com.primapp.model.mentormentee

import com.google.gson.annotations.SerializedName

data class MentorMenteeResponse(

	@field:SerializedName("content")
	val content: Content? = null
)

data class ResultsItem(
	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("get_image_url")
	val getImageUrl: String? = null,

	@field:SerializedName("user_online_status")
	val userOnlineStatus: String? = null
)

data class Content(

	@field:SerializedName("next_page")
	val nextPage: Int? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("prev_page")
	val prevPage: Int? = null,

	@field:SerializedName("results")
	val results: List<ResultsItem>? = null
)
