package com.primapp

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.primapp.cache.UserCache
import com.primapp.di.components.DaggerApplicationComponent
import com.primapp.viewmodels.CommunitiesViewModel
import com.sendbird.android.SendBird
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class PrimApp() : DaggerApplication(), LifecycleObserver {
    private val TAG = "PrimApp_Lifecycle"

    override fun onCreate() {
        super.onCreate()
        SendBird.init(BuildConfig.SENDBIRD_APP_ID, applicationContext)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this);
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }

    fun isActivityVisible(): String {
        Log.e(TAG, "************* visible111")
        Log.e(TAG, "************* ${ProcessLifecycleOwner.get().lifecycle.currentState.name}")
        return ProcessLifecycleOwner.get().lifecycle.currentState.name
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        //App in background
        Log.e(TAG, "************* backgrounded11")
        Log.e(TAG, "************* ${isActivityVisible()}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {

        Log.e(TAG, "************* foregrounded11")
        Log.e(TAG, "************* ${isActivityVisible()}")

        // App in foreground
    }
}