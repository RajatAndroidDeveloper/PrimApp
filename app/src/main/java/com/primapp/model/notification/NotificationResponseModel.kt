package com.primapp.model.notification

import com.google.gson.annotations.SerializedName
import com.primapp.model.auth.UserData
import com.primapp.model.community.CommunityData
import com.primapp.utils.DateTimeUtils
import java.util.*


data class NotificationResponseModel(
    @SerializedName("content")
    val content: Content
)

data class Content(
    @SerializedName("count")
    val count: Int,
    @SerializedName("next_page")
    val nextPage: Any?,
    @SerializedName("prev_page")
    val prevPage: Any?,
    @SerializedName("results")
    val results: List<NotificationResult>
)

data class NotificationResult(
    @SerializedName("data_id")
    val dataId: Int,
    @SerializedName("is_read")
    val isRead: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("notification_type")
    var notificationType: String,
    @SerializedName("receiver")
    val receiver: UserData?,
    @SerializedName("sender")
    val sender: UserData?,
    @SerializedName("contract_details")
    val contractDetails: ContractDetails? = null,
    @SerializedName("title")
    var title: String,
    @SerializedName("cdate")
    val cdate: String,
    @SerializedName("udate")
    val udate: String,
    @SerializedName("community")
    val community: CommunityData,
    @SerializedName("post_data")
    val postData: PostData?
)

data class PostData(
    @SerializedName("file_type")
    val fileType: String?,
    @SerializedName("get_image_url")
    val getImageUrl: String,
    @SerializedName("get_thumbnail_url")
    val getThumbnailUrl: Any?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("post_text")
    val postText: String?,
    @SerializedName("reply_text")
    val replyText: String?
)


data class CreatedBy(
    @SerializedName("last_name")
    val lastName: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("first_name")
    val firstName: String? = null,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("get_image_url")
    val getImageUrl: String? = null
)

data class ContractDetails(

    @SerializedName("end_date")
    val endDate: Long? = null,

    @SerializedName("cdate")
    val cdate: String? = null,

    @SerializedName("scope_of_work")
    val scopeOfWork: String? = null,

    @SerializedName("price")
    val price: String? = null,

    @SerializedName("contract_status")
    val contractStatus: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("udate")
    val udate: String? = null,

    @SerializedName("created_by")
    val createdBy: CreatedBy? = null,

    @SerializedName("start_date")
    val startDate: Long? = null
)

sealed class NotificationUIModel {
    data class NotificationItem(val notification: NotificationResult) : NotificationUIModel()
    data class SeparatorItem(val description: String) : NotificationUIModel()
}

val NotificationUIModel.NotificationItem.dateFromTimeStamp: String?
    get() = DateTimeUtils.convertServerTimeStamp(this.notification.cdate)