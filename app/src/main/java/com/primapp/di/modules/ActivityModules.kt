package com.primapp.di.modules

import com.primapp.ui.DashboardActivity
import com.primapp.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModules {

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeDashboardActivity(): DashboardActivity
}