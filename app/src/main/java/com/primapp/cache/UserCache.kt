package com.primapp.cache

import android.content.Context
import com.primapp.model.auth.UserData
import javax.inject.Inject
import javax.inject.Singleton


object UserCache {

    fun getAccessToken(context: Context): String {
        return Prefs.with(context)?.getString(PrefNames.ACCESS_TOKEN, "") ?: ""
    }

    fun saveAccessToken(context: Context, token: String) {
        Prefs.with(context)?.save(PrefNames.ACCESS_TOKEN, token)
    }

    fun getUserId(context: Context): Int {
        val user = getUser(context)
        return user!!.id
    }

    fun getFCMToken(context: Context): String {
        return Prefs.with(context)?.getString(PrefNames.PUSH_TOKEN, "") ?: ""
    }

    fun saveFCMToken(context: Context, token: String?) {
        Prefs.with(context)?.save(PrefNames.PUSH_TOKEN, token)
    }

    fun getUser(context: Context): UserData? {
        return Prefs.with(context)?.getObject(PrefNames.USER, UserData::class.java)
    }

    fun saveUser(context: Context, user: UserData) {
        Prefs.with(context)?.save(PrefNames.USER, user)
    }

    fun clearAll(context: Context) {
        Prefs.with(context)?.removeAll()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getAccessToken(context).isNotEmpty() && getUser(context) != null
    }

    fun incrementJoinedCommunityCount(context: Context) {
        val user = getUser(context)
        user?.let {
            it.joinedCommunityCount++
            saveUser(context, it)
        }
    }

    fun decrementJoinedCommunityCount(context: Context) {
        val user = getUser(context)
        user?.let {
            it.joinedCommunityCount--
            saveUser(context, it)
        }
    }

    fun incrementPostCount(context: Context) {
        val user = getUser(context)
        user?.let {
            it.postsCount++
            saveUser(context, it)
        }
    }

    fun decrementPostCount(context: Context) {
        val user = getUser(context)
        user?.let {
            it.postsCount--
            saveUser(context, it)
        }
    }

    fun incrementMenteeCount(context: Context) {
        val user = getUser(context)
        user?.let {
            it.menteeCount++
            saveUser(context, it)
        }
    }

    fun decrementMenteeCount(context: Context) {
        val user = getUser(context)
        user?.let {
            it.menteeCount--
            saveUser(context, it)
        }
    }

    fun decrementMentorCount(context: Context) {
        val user = getUser(context)
        user?.let {
            it.mentorCount--
            saveUser(context, it)
        }
    }

    fun resetNotification(context: Context){
        val user = getUser(context)
        user?.let {
            it.notificationsCount = 0
            saveUser(context, it)
        }
    }

    //SendBird Prefs
    fun getSendBirdIsConnected(context: Context): Boolean {
        return Prefs.with(context)?.getBoolean(PrefNames.SENDBIRD_CONNECTED, false) ?: false
    }

    fun saveSendBirdIsConnected(context: Context, connected: Boolean) {
        Prefs.with(context)?.save(PrefNames.SENDBIRD_CONNECTED, connected)
    }
}