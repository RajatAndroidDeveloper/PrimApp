package com.primapp.di.modules

import com.primapp.ui.SplashFragment
import com.primapp.ui.initial.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

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
}