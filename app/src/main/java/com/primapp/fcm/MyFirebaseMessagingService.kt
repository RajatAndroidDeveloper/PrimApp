package com.primapp.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.model.notification.SendbirdNotiificationData
import com.primapp.ui.dashboard.DashboardActivity
import com.sendbird.android.SendBird
import com.sendbird.android.SendBird.PushTokenRegistrationStatus
import org.json.JSONException
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.i(TAG, "New Message : ${Gson().toJson(remoteMessage)}")
        var channelUrl: String? = null
        try {
            if (remoteMessage.getData().containsKey("sendbird")) {
                val sendbird = JSONObject(remoteMessage.getData().get("sendbird"))

                val sendBirdNotificationData: SendbirdNotiificationData =
                    Gson().fromJson(sendbird.toString(), SendbirdNotiificationData::class.java)

                val channel: JSONObject = sendbird.get("channel") as JSONObject
                channelUrl = channel.get("channel_url").toString()
                val messageSender = sendbird.get("sender") as JSONObject
                val messageTitle = messageSender.get("name") as String
                val messageBody = sendbird.get("message") as String

                SendBird.markAsDelivered(remoteMessage.data)

                //Save to User cache
                UserCache.saveNotificationData(this, sendBirdNotificationData)

                // If you want to customize a notification with the received FCM message,
                // write your method like the sendNotification() below.
                showInboxStyleNotification(this, messageTitle, getNotificationDisplayMessage(sendBirdNotificationData), channelUrl, sendBirdNotificationData)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "New FCM Token : ${token}")

        // Register a registration token to Sendbird server.
        SendBird.registerPushTokenForCurrentUser(token) { ptrs, e ->
            if (e != null) {
                Log.e(TAG, "Failed to register to SendBird")
            }
            if (ptrs == PushTokenRegistrationStatus.PENDING) {
                Log.e(TAG, "Registration Pending. Failed to register to SendBird")
                // A token registration is pending.
                // Retry the registration after a connection has been successfully established.
            }
        }
    }

    fun sendNotification(
        context: Context,
        messageTitle: String?,
        messageBody: String?,
        channelUrl: String?,
        notificationData: SendbirdNotiificationData?
    ) {
        //Notification Manager
        val notificationManager = getNotificationManager(context)
        //Pending Intent
        val pendingIntent = getPendingIntent(context, channelUrl)
        //Sound on notification
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setContentTitle(context.resources.getString(R.string.app_name))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setGroup(GROUP_KEY)
//        if (PreferenceUtils.getNotificationsShowPreviews()) {
//            notificationBuilder.setContentText(messageBody)
//        } else {
//            notificationBuilder.setContentText("Somebody sent you a message.")
//        }
        val notificationId = notificationData?.sender?.id?.toInt() ?: 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun showInboxStyleNotification(
        context: Context,
        senderName: String?,
        messageBody: String?,
        channelUrl: String?,
        notificationData: SendbirdNotiificationData?
    ) {
        val listNotification = UserCache.getNotificationData(context)
        Log.d("fcm_Data", Gson().toJson(listNotification))
        val currentNotifications = listNotification?.filter { it.channel.channelUrl.equals(channelUrl) }

        if (currentNotifications == null || currentNotifications.size <= 1) {
            sendNotification(context, senderName, messageBody, channelUrl, notificationData)
        } else {
            //Notification Manager
            val notificationManager = getNotificationManager(context)
            //Pending Intent
            val pendingIntent = getPendingIntent(context, channelUrl)
            //Sound on notification
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            //Show Inbox style notification
            val inboxStyle = NotificationCompat.InboxStyle()
                .setSummaryText("${currentNotifications.size} messages from ${senderName}")
                .setBigContentTitle(senderName)
            currentNotifications.forEach {
                inboxStyle.addLine(getNotificationDisplayMessage(it))
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(senderName)
                .setContentText(messageBody)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setGroup(GROUP_KEY)
                .build()

            val notificationId = notificationData?.sender?.id?.toInt() ?: 0
            notificationManager.notify(notificationId, notification)
        }

    }

    private fun getNotificationDisplayMessage(it: SendbirdNotiificationData): String {
        return if (it.type.equals("FILE", true) && it.files.isNotEmpty()) {
            val fileType = it.files[0].type.split('/')
            if (fileType.size > 1) {
                "${fileType[0].capitalize()} file received"
            } else {
                "${it.sender.name} has sent a file"
            }
        } else {
            it.message
        }
    }

    fun getNotificationManager(context: Context): NotificationManager {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {  // Build.VERSION_CODES.O
            val mChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                    setShowBadge(true)
                }
            notificationManager.createNotificationChannel(mChannel)
        }
        return notificationManager
    }

    fun getPendingIntent(context: Context, channelUrl: String?): PendingIntent {
        val intent = Intent(context, DashboardActivity::class.java)
        intent.putExtra("channelUrl", channelUrl)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
//        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        const val TAG = "fcm_push"
        const val CHANNEL_ID = "prim_notify_channel_01"
        const val CHANNEL_NAME = "PrimNotificationChannel"
        const val GROUP_KEY = "PrimNotificationGroup"
    }
}