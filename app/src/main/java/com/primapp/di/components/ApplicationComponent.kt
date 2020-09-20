package com.primapp.di.components

import android.app.Application
import com.primapp.PrimApp
import com.primapp.di.modules.ActivityModules
import com.primapp.di.modules.AppModule
import com.primapp.di.modules.ContextModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        ActivityModules::class
    ]
)
interface ApplicationComponent : AndroidInjector<PrimApp> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Application): ApplicationComponent
    }
}