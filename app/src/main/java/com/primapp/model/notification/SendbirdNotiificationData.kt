package com.primapp.model.notification
import com.google.gson.annotations.SerializedName


data class SendbirdNotiificationData(
    @SerializedName("app_id")
    val appId: String,
    @SerializedName("audience_type")
    val audienceType: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("channel")
    val channel: Channel,
    @SerializedName("channel_type")
    val channelType: String,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("custom_type")
    val customType: String,
    @SerializedName("files")
    val files: List<File>,
    @SerializedName("mentioned_users")
    val mentionedUsers: List<Any>,
    @SerializedName("message")
    val message: String,
    @SerializedName("message_id")
    val messageId: Long,
    @SerializedName("push_alert")
    val pushAlert: String,
    @SerializedName("push_sound")
    val pushSound: String,
    @SerializedName("push_title")
    val pushTitle: Any?,
    @SerializedName("recipient")
    val recipient: Recipient,
    @SerializedName("sender")
    val sender: Sender,
    @SerializedName("translations")
    val translations: Translations,
    @SerializedName("type")
    val type: String,
    @SerializedName("unread_message_count")
    val unreadMessageCount: Int
)

data class Channel(
    @SerializedName("channel_url")
    val channelUrl: String,
    @SerializedName("custom_type")
    val customType: String,
    @SerializedName("name")
    val name: String
)


data class File(
    @SerializedName("channel_id")
    val channelId: Int,
    @SerializedName("channel_url")
    val channelUrl: String,
    @SerializedName("custom")
    val custom: String,
    @SerializedName("edge_ts")
    val edgeTs: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("remote_addr")
    val remoteAddr: String,
    @SerializedName("req_id")
    val reqId: String,
    @SerializedName("require_auth")
    val requireAuth: Boolean,
    @SerializedName("size")
    val size: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    val url: String
)

data class Recipient(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("push_template")
    val pushTemplate: String
)

data class Sender(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_url")
    val profileUrl: String,
    @SerializedName("require_auth_for_profile_image")
    val requireAuthForProfileImage: Boolean
)

class Translations(
)