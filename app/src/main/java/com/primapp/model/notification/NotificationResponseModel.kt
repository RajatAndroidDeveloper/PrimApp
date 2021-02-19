package com.primapp.model.notification

import com.google.gson.annotations.SerializedName
import com.primapp.model.auth.UserData
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
    val message: String,
    @SerializedName("notification_type")
    var notificationType: String,
    @SerializedName("receiver")
    val receiver: UserData?,
    @SerializedName("sender")
    val sender: UserData?,
    @SerializedName("title")
    var title: String,
    @SerializedName("cdate")
    val cdate: String,
    @SerializedName("udate")
    val udate: String
)

sealed class NotificationUIModel {
    data class NotificationItem(val notification: NotificationResult) : NotificationUIModel()
    data class SeparatorItem(val description: String) : NotificationUIModel()
}

val NotificationUIModel.NotificationItem.dateFromTimeStamp: String?
    get() = DateTimeUtils.convertServerTimeStamp(this.notification.cdate)