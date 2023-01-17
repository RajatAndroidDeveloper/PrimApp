package com.primapp.model.portfolio

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel
import java.io.Serializable

data class UserPortfolioResponse(
    @SerializedName("content")
    val content: PortfolioContent
) : BaseDataModel()


data class PortfolioContent(
    @SerializedName("mentoringPortfolio")
    var mentoringPortfolio: ArrayList<MentoringPortfolioData>?,
    @SerializedName("experiences")
    var experiences: ArrayList<ExperienceData>?,
    @SerializedName("skills_certificate")
    var skills_certificate: ArrayList<SkillsCertificateData>?,
    @SerializedName("benefits")
    var benefits: ArrayList<BenefitsData>?,
) : Serializable

data class ExperienceData(
    @SerializedName("cdate")
    val cdate: String?,
    @SerializedName("company_name")
    val companyName: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_current_company")
    val isCurrentCompany: Boolean,
    @SerializedName("job_type")
    val jobType: JobType?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("months")
    val months: Int = 0,
    @SerializedName("title")
    val title: String?,
    @SerializedName("udate")
    val udate: String?,
    @SerializedName("user")
    val user: Int,
    @SerializedName("years")
    val years: Int = 0,
    @SerializedName("start_date")
    val startDate: Long = 0,
    @SerializedName("end_date")
    val endDate: Long = 0,
) : Serializable

data class MentoringPortfolioData(
    @SerializedName("content_file")
    val contentFile: String?,
    @SerializedName("file_type")
    val fileType: String?,
    @SerializedName("get_image_url")
    val imageUrl: String?,
    @SerializedName("get_thumbnail_url")
    val thumbnailUrl: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_active")
    val isActive: Boolean?,
    @SerializedName("thumbnail_file")
    val thumbnailFile: String?,
    @SerializedName("user")
    val user: Int?,
    //Local to display remove button
    var isShowRemove: Boolean = false
) : Serializable

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
) : Serializable

data class BenefitsData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("user")
    val user: Int
) : Serializable

data class JobType(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
) : Serializable