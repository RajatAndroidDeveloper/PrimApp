package com.primapp.di.modules

import com.primapp.ui.SplashFragment
import com.primapp.ui.communities.*
import com.primapp.ui.initial.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeSignUpFragment(): SignUpFragment

    @ContributesAndroidInjector
    abstract fun contributeForgotPassFragment(): ForgotPasswordFragment

    @ContributesAndroidInjector
    abstract fun contributeForgotUsernameFragment(): ForgotUsernameFragment

    @ContributesAndroidInjector
    abstract fun contributeVerifyOTPFragment(): VerifyOTPFragment

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
    abstract fun contributePasswordVerificationFragment(): PasswordVerificationFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateCommunityFragment(): CreateCommunityFragment

    @ContributesAndroidInjector
    abstract fun contributeCommunityDetailsFragment(): CommunityDetailsFragment
}