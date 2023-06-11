package com.primapp

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.primapp.BuildConfig.SENDBIRD_APP_ID
import com.primapp.di.components.DaggerApplicationComponent
import com.sendbird.android.LogLevel
import com.sendbird.android.SendbirdChat
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.params.InitParams
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class PrimApp() : DaggerApplication() {
    private val TAG = "PrimApp_Lifecycle"

    override fun onCreate() {
        super.onCreate()
        sendbirdChatInit()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }
    private fun sendbirdChatInit(){
        val initParams = InitParams(SENDBIRD_APP_ID, applicationContext, true)
        initParams.logLevel = LogLevel.ERROR
        SendbirdChat.init(
            initParams,
            object : InitResultHandler {
                override fun onInitFailed(e: SendbirdException) {
                    Log.e("sendbirdChatStatus","Failed")
                    // If useLocalCaching is true and init fails, the SDK will turn off the useLocalCaching flag so that you can still proceed with your app
                }

                override fun onMigrationStarted() {
                    // This won't be called if useLocalCaching is set to false.
                }

                override fun onInitSucceed() {
                    Log.e("sendbirdChatStatus","Connected")
                }
            })
    }

}