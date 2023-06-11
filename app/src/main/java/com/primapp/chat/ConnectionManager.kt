package com.primapp.chat

import com.sendbird.android.ConnectionState
import com.sendbird.android.SendbirdChat
import com.sendbird.android.handler.ConnectHandler
import com.sendbird.android.handler.ConnectionHandler
import com.sendbird.android.handler.DisconnectHandler

object ConnectionManager {

    const val TAG = "SendBirdChatLogs"

    fun login(userId: String, handler: ConnectHandler?) {
        SendbirdChat.connect(
            userId
        ) { user, e ->
            handler?.onConnected(user, e)
        }
    }

    fun logout(handler: DisconnectHandler?) {
        SendbirdChat.disconnect { handler?.onDisconnected() }
    }

    fun addConnectionManagementHandler(userId: String?, handlerId: String?, handler: ConnectionManagementHandler?) {
        SendbirdChat.addConnectionHandler(handlerId!!, object : ConnectionHandler {
            override fun onReconnectStarted() {}
            override fun onReconnectSucceeded() {
                handler?.onConnected(true)
            }

            override fun onConnected(userId: String) {
                TODO("Not yet implemented")
            }

            override fun onDisconnected(userId: String) {
                TODO("Not yet implemented")
            }

            override fun onReconnectFailed() {}
        })
        if (SendbirdChat.connectionState == ConnectionState.OPEN) {
            handler?.onConnected(false)
        } else if (SendbirdChat.connectionState == ConnectionState.CLOSED) { // push notification or system kill
            SendbirdChat.connect(userId!!) { user, e ->
                if (e != null) {
                    return@connect
                }
                handler?.onConnected(false)
            }
        }
    }

    fun removeConnectionManagementHandler(handlerId: String?) {
        handlerId?.let { SendbirdChat.removeConnectionHandler(it) }
    }

    interface ConnectionManagementHandler {
        /**
         * A callback for when connected or reconnected to refresh.
         *
         * @param reconnect Set false if connected, true if reconnected.
         */
        fun onConnected(reconnect: Boolean)
    }
}
