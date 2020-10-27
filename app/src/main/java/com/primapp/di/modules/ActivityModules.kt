package com.primapp.di.modules

import com.primapp.ui.dashboard.DashboardActivity
import com.primapp.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModules {

    @ContributesAndroidInjector(modules = [AuthFragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
    @ContributesAndroidInjector(modules = [DashboardFragmentBuildersModule::class])
    abstract fun contributeDashboardActivity(): DashboardActivity
}