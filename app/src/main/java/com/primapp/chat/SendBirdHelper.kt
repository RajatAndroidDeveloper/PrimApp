package com.primapp.chat

import com.primapp.utils.DateTimeUtils
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.message.FileMessage
import com.sendbird.android.user.User
import java.util.*

object SendBirdHelper {

    fun getGroupChannelTitle(channel: GroupChannel): String? {
        val members = channel.members
        return if (members.size < 2 || SendbirdChat.currentUser == null) {
            "No Members"
        } else if (members.size == 2) {
            val names = StringBuffer()
            for (member in members) {
                if (member.userId == SendbirdChat.currentUser!!.userId) {
                    continue
                }
                names.append(", " + member.nickname)
            }
            names.delete(0, 2).toString()
        } else {
            var count = 0
            val names = StringBuffer()
            for (member in members) {
                if (member.userId == SendbirdChat.currentUser?.userId) {
                    continue
                }
                count++
                names.append(", " + member.nickname)
                if (count >= 10) {
                    break
                }
            }
            names.delete(0, 2).toString()
        }
    }

    fun getGroupChannelProfileImage(channel: GroupChannel): String? {
        val members = channel.members
        return if (members.size < 2 || SendbirdChat.currentUser == null) {
            ""
        } else if (members.size == 2) {
            members.forEach {
                if (it.userId != SendbirdChat.currentUser?.userId) {
                    return it.profileUrl
                }
            }
            return ""
        } else {
            channel.coverUrl
        }
    }

    fun getMembersOnlineStatus(channel: GroupChannel): Boolean {
        val members = channel.members
        return if (members.size < 2 || SendbirdChat.currentUser == null) {
            false
        } else if (members.size == 2) {
            members.forEach {
                if (it.userId != SendbirdChat.currentUser?.userId && it.connectionStatus == User.ConnectionStatus.ONLINE) {
                    return true
                }
            }
            return false
        } else {
            false
        }
    }

    fun getMembersLastSeen(channel: GroupChannel): String? {
        val members = channel.members
        return if (members.size < 2 || SendbirdChat.currentUser == null) {
            null
        } else if (members.size == 2) {
            members.forEach {
                if (it.userId != SendbirdChat.currentUser?.userId) {
                    var lastSeenText = "last seen "
                    if (DateTimeUtils.isToday(it.lastSeenAt)) {
                        lastSeenText += "today at ${DateTimeUtils.getDateFromMillis(
                            it.lastSeenAt,
                            DateTimeUtils.TIME_FORMAT
                        )}"
                    } else {
                        lastSeenText += DateTimeUtils.getDateFromMillis(
                            it.lastSeenAt,
                            DateTimeUtils.LAST_SEEN_DATE_FORMAT
                        )
                    }
                    return lastSeenText
                }
            }
            return null
        } else {
            null
        }
    }

    fun getGroupChannelOtherUserId(channel: GroupChannel): Int {
        val members = channel.members
        return if (members.size < 2 || SendbirdChat.currentUser == null) {
            -1
        } else if (members.size == 2) {
            members.forEach {
                if (it.userId != SendbirdChat.currentUser?.userId) {
                    return it.userId.toInt()
                }
            }
            return -1
        } else {
            -1
        }
    }

    fun getThumbnailUrl(message: FileMessage?): String {
        val thumbnails = message?.thumbnails
        return if (thumbnails?.size?:0 > 0) {
            if (message?.type!!.toLowerCase(Locale.ROOT).contains("gif")) {
                message.url
            } else {
                thumbnails!![0].url
            }
        } else {
            message!!.url
        }
    }
}