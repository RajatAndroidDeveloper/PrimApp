package com.primapp.chat

import com.primapp.utils.DateTimeUtils
import com.sendbird.android.GroupChannel
import com.sendbird.android.SendBird
import com.sendbird.android.User

object SendBirdHelper {

    fun getGroupChannelTitle(channel: GroupChannel): String? {
        val members = channel.members
        return if (members.size < 2 || SendBird.getCurrentUser() == null) {
            "No Members"
        } else if (members.size == 2) {
            val names = StringBuffer()
            for (member in members) {
                if (member.userId == SendBird.getCurrentUser().userId) {
                    continue
                }
                names.append(", " + member.nickname)
            }
            names.delete(0, 2).toString()
        } else {
            var count = 0
            val names = StringBuffer()
            for (member in members) {
                if (member.userId == SendBird.getCurrentUser().userId) {
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
        return if (members.size < 2 || SendBird.getCurrentUser() == null) {
            ""
        } else if (members.size == 2) {
            members.forEach {
                if (it.userId != SendBird.getCurrentUser().userId && it.connectionStatus == User.ConnectionStatus.ONLINE) {
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
        return if (members.size < 2 || SendBird.getCurrentUser() == null) {
            false
        } else if (members.size == 2) {
            members.forEach {
                if (it.userId != SendBird.getCurrentUser().userId && it.connectionStatus == User.ConnectionStatus.ONLINE) {
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
        return if (members.size < 2 || SendBird.getCurrentUser() == null) {
            null
        } else if (members.size == 2) {
            members.forEach {
                if (it.userId != SendBird.getCurrentUser().userId) {
                    var lastSeenText = "last seen "
                    if(DateTimeUtils.isToday(it.lastSeenAt)){
                        lastSeenText += "today at ${DateTimeUtils.getDateFromMillis(it.lastSeenAt, DateTimeUtils.TIME_FORMAT)}"
                    }else{
                        lastSeenText += DateTimeUtils.getDateFromMillis(it.lastSeenAt, DateTimeUtils.LAST_SEEN_DATE_FORMAT)
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
        return if (members.size < 2 || SendBird.getCurrentUser() == null) {
            -1
        } else if (members.size == 2) {
            members.forEach {
                if (it.userId != SendBird.getCurrentUser().userId) {
                    return it.userId.toInt()
                }
            }
            return -1
        } else {
            -1
        }
    }

}