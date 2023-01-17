package com.primapp.model.portfolio

import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import java.io.Serializable

data class AddExperienceRequest(
    @SerializedName("company_name")
    var companyName: String?,
    @SerializedName("is_current_company")
    var isCurrentCompany: Boolean = false,
    @SerializedName("jobType")
    var jobType: Int?,
    @SerializedName("location")
    var location: String?,
    @SerializedName("months")
    var months: Int?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("years")
    var years: Int?,
    @SerializedName("start_date")
    var startDate: Long?,
    @SerializedName("end_date")
    var endDate: Long?,
) : Serializable {
    fun isValidFormData(): ValidationResults {
        if (title.isNullOrEmpty())
            return ValidationResults.EMPTY_JOB_TITLE

        if (jobType == null)
            return ValidationResults.EMPTY_JOB_TYPE

        if (companyName.isNullOrEmpty())
            return ValidationResults.EMPTY_COMPANY_NAME

        if (location.isNullOrEmpty())
            return ValidationResults.EMPTY_LOCATION

        if (isCurrentCompany == false) {
//            if (years == null)
//                return ValidationResults.EMPTY_YEARS
//
//            if (months == null)
//                return ValidationResults.EMPTY_MONTHS

            if(startDate == null)
                return ValidationResults.EMPTY_START_DATE

            if(endDate == null)
                return ValidationResults.EMPTY_END_DATE
        }

        return ValidationResults.SUCCESS
    }

}

data class AddExperienceResponse(
    @SerializedName("content")
    val content: ExperienceData
)