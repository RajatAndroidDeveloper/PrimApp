package com.primapp.model.mycontracts

import com.google.gson.annotations.SerializedName

data class MyContractsReponseModel(

	@field:SerializedName("content")
	val content: Content? = null
)

data class User(

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

data class OngoingContractsItem(

	@field:SerializedName("end_date")
	val endDate: Long? = null,

	@field:SerializedName("cdate")
	val cdate: String? = null,

	@field:SerializedName("scope_of_work")
	val scopeOfWork: String? = null,

	@field:SerializedName("amend_request")
	val amendRequest: List<Any?>? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("contract_status")
	val contractStatus: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("accepted_by")
	val acceptedBy: List<Any?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("udate")
	val udate: String? = null,

	@field:SerializedName("created_by")
	val createdBy: CreatedBy? = null,

	@field:SerializedName("start_date")
	val startDate: Long? = null
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

data class AcceptedByItem(

	@field:SerializedName("payement_status")
	val payementStatus: String? = null,

	@field:SerializedName("accepted_price")
	val acceptedPrice: String? = null,

	@field:SerializedName("created_by_id")
	val createdById: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class RequestBy(

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

data class Results(

	@field:SerializedName("ongoing_contracts")
	val ongoingContracts: List<OngoingContractsItem>? = null,

	@field:SerializedName("completed_contracts")
	val completedContracts: List<CompletedContractsItem>? = null
)

data class AmendRequestItem(

	@field:SerializedName("reason")
	val reason: Any? = null,

	@field:SerializedName("request_by")
	val requestBy: RequestBy? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("cdate")
	val cdate: String? = null,

	@field:SerializedName("payement_status")
	val payementStatus: String? = null,

	@field:SerializedName("contract")
	val contract: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("created_by_id")
	val createdById: Int? = null,

	@field:SerializedName("udate")
	val udate: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Content(

	@field:SerializedName("next_page")
	val nextPage: Any? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("prev_page")
	val prevPage: Any? = null,

	@field:SerializedName("results")
	val results: Results? = null
)

data class CompletedContractsItem(

	@field:SerializedName("end_date")
	val endDate: Long? = null,

	@field:SerializedName("cdate")
	val cdate: String? = null,

	@field:SerializedName("scope_of_work")
	val scopeOfWork: String? = null,

	@field:SerializedName("amend_request")
	val amendRequest: List<AmendRequestItem?>? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("contract_status")
	val contractStatus: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("accepted_by")
	val acceptedBy: List<AcceptedByItem?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("udate")
	val udate: String? = null,

	@field:SerializedName("created_by")
	val createdBy: CreatedBy? = null,

	@field:SerializedName("start_date")
	val startDate: Long? = null
)
