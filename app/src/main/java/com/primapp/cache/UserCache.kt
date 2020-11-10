package com.primapp.cache

import android.content.Context
import com.primapp.model.auth.UserData


object UserCache {

    fun getAccessToken(context: Context): String {
        return Prefs.with(context)?.getString(PrefNames.ACCESS_TOKEN, "") ?: ""
    }

    fun saveAccessToken(context: Context, token: String) {
        Prefs.with(context)?.save(PrefNames.ACCESS_TOKEN, token)
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

}