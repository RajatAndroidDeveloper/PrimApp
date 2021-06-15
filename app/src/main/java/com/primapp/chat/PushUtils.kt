package com.primapp.chat

import com.sendbird.android.SendBird
import com.sendbird.android.SendBird.SetPushTriggerOptionHandler
import com.sendbird.android.SendBirdPushHandler
import com.sendbird.android.SendBirdPushHelper
import com.sendbird.android.SendBirdPushHelper.OnPushRequestCompleteListener

object PushUtils {
    fun registerPushHandler(handler: SendBirdPushHandler) {
        SendBirdPushHelper.registerPushHandler(handler)
    }

    fun unregisterPushHandler(listener: OnPushRequestCompleteListener?) {
        SendBirdPushHelper.unregisterPushHandler(listener)
    }

    fun setPushNotification(enable: Boolean, handler: SetPushTriggerOptionHandler?) {
        val option =
            if (enable) SendBird.PushTriggerOption.ALL else SendBird.PushTriggerOption.OFF
        SendBird.setPushTriggerOption(option, handler)
    }
}