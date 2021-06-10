package com.primapp

import com.primapp.di.components.DaggerApplicationComponent
import com.sendbird.android.SendBird
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class PrimApp() : DaggerApplication() {


    override fun onCreate() {
        super.onCreate()
        SendBird.init(BuildConfig.SENDBIRD_APP_ID, applicationContext)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }

}