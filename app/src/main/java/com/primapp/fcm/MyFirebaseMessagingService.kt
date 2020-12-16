package com.primapp.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.i(TAG, "New Message : ${p0.messageId}")
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.i(TAG, "New FCM Token : ${p0}")
    }

    companion object {
        const val TAG = "fcm_push"
        const val CHANNEL_ID = "fcmATPush"
    }
}