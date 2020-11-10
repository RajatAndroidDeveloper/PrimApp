package com.primapp.di.modules

import com.primapp.ui.communities.*
import com.primapp.ui.dashboard.NotificationsFragment
import com.primapp.ui.dashboard.ProfileFragment
import com.primapp.ui.dashboard.UpdatesFragment
import com.primapp.ui.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DashboardFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeUpdatesFragment(): UpdatesFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationFragment(): NotificationsFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeCommunitiesFragment(): CommunitiesFragment

    @ContributesAndroidInjector
    abstract fun contributeCommunitiesJoinFragment(): CommunityJoinViewFragment

    @ContributesAndroidInjector
    abstract fun contributeAllCommunityFragment(): AllCommunityFragment

    @ContributesAndroidInjector
    abstract fun contributeJoinedCommunityFragment(): JoinedCommunityFragment

    @ContributesAndroidInjector
    abstract fun contributeCreatedCommunityFragment(): CreatedCommunityFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment
}