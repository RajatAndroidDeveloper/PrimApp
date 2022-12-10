package com.primapp.model.portfolio

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel

data class UserPortfolioResponse(
    @SerializedName("content")
    val content: PortfolioContent
) : BaseDataModel()


data class PortfolioContent(
    @SerializedName("mentoringPortfolio")
    val mentoringPortfolio: ArrayList<MentoringPortfolioData>?,
    @SerializedName("experiences")
    val experiences: ArrayList<ExperienceData>?,
    @SerializedName("skills_certificate")
    val skills_certificate: ArrayList<SkillsCertificateData>?,
    @SerializedName("benefits")
    val benefits: ArrayList<String>?,
)

data class ExperienceData(
    @SerializedName("cdate")
    val cdate: String?,
    @SerializedName("company_name")
    val companyName: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_active")
    val isActive: Boolean?,
    @SerializedName("is_current_company")
    val isCurrentCompany: Boolean?,
    @SerializedName("job_type")
    val jobType: JobType?,
    @SerializedName("jobType")
    val jobTypeId: Int?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("months")
    val months: Any?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("udate")
    val udate: String?,
    @SerializedName("user")
    val user: Int?,
    @SerializedName("years")
    val years: Any?
)

data class MentoringPortfolioData(
    @SerializedName("content_file")
    val contentFile: String?,
    @SerializedName("file_type")
    val fileType: String?,
    @SerializedName("get_image_url")
    val getImageUrl: String?,
    @SerializedName("get_thumbnail_url")
    val getThumbnailUrl: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_active")
    val isActive: Boolean?,
    @SerializedName("thumbnail_file")
    val thumbnailFile: String?,
    @SerializedName("user")
    val user: Int?
)

data class SkillsCertificateData(
    @SerializedName("cdate")
    val cdate: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("is_active")
    val isActive: Boolean?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("udate")
    val udate: String?,
    @SerializedName("user")
    val user: Int?
)

data class JobType(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)