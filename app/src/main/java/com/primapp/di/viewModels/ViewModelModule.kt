package com.primapp.di.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.primapp.ui.communities.edit.EditCommunityViewModel
import com.primapp.ui.post.create.CreatePostViewModel
import com.primapp.ui.splash.SplashViewModel
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
    @IntoMap
    @ViewModelKey(EditCommunityViewModel::class)
    abstract fun bindEditCommunityViewModel(viewModel: EditCommunityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PostsViewModel::class)
    abstract fun bindPostsViewModel(viewModel: PostsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreatePostViewModel::class)
    abstract fun bindCreatePostViewModel(viewModel: CreatePostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel::class)
    abstract fun bindNotificationViewModel(viewModel: NotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReportIssueViewModel::class)
    abstract fun bindReportIssueViewModel(viewModel: ReportIssueViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PortfolioViewModel::class)
    abstract fun bindPortfolioViewModel(viewModel: PortfolioViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
