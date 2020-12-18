package com.primapp.di.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.primapp.ui.SplashViewModel
import com.primapp.viewmodels.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignUpViewModel::class)
    abstract fun bindSignUpViewModel(viewModel: SignUpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VerifyOTPViewModel::class)
    abstract fun bindVerifyOTPViewModel(viewModel: VerifyOTPViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForgotDataViewModel::class)
    abstract fun bindForgotDataViewModel(viewModel: ForgotDataViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PasswordVerificationViewModel::class)
    abstract fun bindPasswordVerificationViewModel(viewModel: PasswordVerificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CommunitiesViewModel::class)
    abstract fun bindCommunityViewModel(viewModel: CommunitiesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditProfileViewModel::class)
    abstract fun bindEditProfileViewModel(viewModel: EditProfileViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
