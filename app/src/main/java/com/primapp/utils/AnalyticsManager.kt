package com.primapp.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.primapp.model.auth.UserData

class AnalyticsManager(private val firebaseAnalytics: FirebaseAnalytics) {

    fun trackScreenView(screenName: String) {
        val screenNameToSend = "screen_$screenName"
        firebaseAnalytics.logEvent(screenNameToSend, null)
    }

    fun logEvent(eventName: String, eventValue: Bundle? = null) {
        val eventNameToSend = "event_$eventName"
        firebaseAnalytics.logEvent(eventNameToSend, eventValue)
    }

    fun logUserProperties(data: UserData?) {
        data?.let {
            FirebaseCrashlytics.getInstance().setUserId(it.id.toString())
            FirebaseCrashlytics.getInstance().setCustomKey("name", "${it.firstName} ${it.lastName}")
            firebaseAnalytics.setUserId(it.id.toString())
            firebaseAnalytics.setUserProperty("name", "${it.firstName} ${it.lastName}")
            firebaseAnalytics.setUserProperty("email", it.email)
            firebaseAnalytics.setUserProperty("joinedCommunityCount", it.joinedCommunityCount.toString())
        }
    }

    companion object {
        const val SCREEN_SPLASH_VIEW = "Splash"
        const val SCREEN_UPDATES = "Updates"
        const val SCREEN_NOTIFICATION = "Notifications"
        const val SCREEN_COMMUNITIES = "Communities"
        const val SCREEN_PROFILE = "Profile"
        const val SCREEN_NETWORK_ERROR = "NetworkError"
        const val SCREEN_SETTINS = "Settings"

        const val EVENT_LOGOUT = "Logout"
        const val EVENT_LOGIN = "Login"
    }
}