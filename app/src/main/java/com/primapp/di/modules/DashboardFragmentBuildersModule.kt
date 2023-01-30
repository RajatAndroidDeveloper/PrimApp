package com.primapp.di.modules

import com.primapp.ui.base.CommonWebView
import com.primapp.ui.base.ImageViewDialog
import com.primapp.ui.base.PopUpReportPost
import com.primapp.ui.base.VideoViewDialog
import com.primapp.ui.chat.ChannelListFragment
import com.primapp.ui.chat.ChatFragment
import com.primapp.ui.chat.CreateChannelFragment
import com.primapp.ui.communities.*
import com.primapp.ui.communities.create.CreateCommunityFragment
import com.primapp.ui.communities.details.CommunityDetailsFragment
import com.primapp.ui.communities.edit.EditCommunityFragment
import com.primapp.ui.communities.members.CommunityMembersFragment
import com.primapp.ui.notification.NotificationsFragment
import com.primapp.ui.dashboard.ProfileFragment
import com.primapp.ui.initial.PasswordVerificationFragment
import com.primapp.ui.notification.MentorRequestRejectionFragment
import com.primapp.ui.portfolio.*
import com.primapp.ui.post.UpdatesFragment
import com.primapp.ui.post.comment.PostCommentFragment
import com.primapp.ui.post.create.CreatePostFragment
import com.primapp.ui.post.details.PostDetailsFragment
import com.primapp.ui.post.reply.PostCommentReplyFragment
import com.primapp.ui.post.reported_post.ReportByMembersFragment
import com.primapp.ui.post.reported_post.ReportedPostsFragment
import com.primapp.ui.profile.*
import com.primapp.ui.profile.other.OtherUserProfileFragment
import com.primapp.ui.rewards.EarnedRewardsFragment
import com.primapp.ui.settings.AboutUsFragment
import com.primapp.ui.settings.SettingsFragment
import com.primapp.ui.settings.help.HelpAndSupportFragment
import com.primapp.ui.settings.help.ReportProblemFragment
import com.primapp.ui.settings.security.SecurityFragment
import com.primapp.ui.todo.AddTodoTaskFragment
import com.primapp.ui.todo.TodoListFragment
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

    @ContributesAndroidInjector
    abstract fun contributeAboutUsFragment(): AboutUsFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateCommunityFragment(): CreateCommunityFragment

    @ContributesAndroidInjector
    abstract fun contributeCommunityDetailsFragment(): CommunityDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeEditProfileFragment(): EditProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeEditCommunityFragment(): EditCommunityFragment

    @ContributesAndroidInjector
    abstract fun contributeCreatePostFragment(): CreatePostFragment

    @ContributesAndroidInjector
    abstract fun contributeUserPostsFragment(): UserPostsFragment

    @ContributesAndroidInjector
    abstract fun contributeUserJoinedCommunitiesFragment(): UserJoinedCommunitiesFragment

    @ContributesAndroidInjector
    abstract fun contributePostCommentFragment(): PostCommentFragment

    @ContributesAndroidInjector
    abstract fun contributeCommunityMembersFragment(): CommunityMembersFragment

    @ContributesAndroidInjector
    abstract fun contributePostCommentReplyFragment(): PostCommentReplyFragment

    @ContributesAndroidInjector
    abstract fun contributeOtherUserProfileFragment(): OtherUserProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeMentorRequestRejectionFragment(): MentorRequestRejectionFragment

    @ContributesAndroidInjector
    abstract fun contributePopUpRequestDialogFragment(): PopUpReportPost

    @ContributesAndroidInjector
    abstract fun contributeImageViewDialogFragment(): ImageViewDialog

    @ContributesAndroidInjector
    abstract fun contributeVideoViewDialogFragment(): VideoViewDialog

    @ContributesAndroidInjector
    abstract fun contributeHelpAndSupportFragment(): HelpAndSupportFragment

    @ContributesAndroidInjector
    abstract fun contributeSecurityFragment(): SecurityFragment

    @ContributesAndroidInjector
    abstract fun contributeReportProblemFragment(): ReportProblemFragment

    @ContributesAndroidInjector
    abstract fun contributePasswordVerificationFragment(): PasswordVerificationFragment

    @ContributesAndroidInjector
    abstract fun contributePostDetailsFragment(): PostDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeChannelListFragment(): ChannelListFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateChannelFragment(): CreateChannelFragment

    @ContributesAndroidInjector
    abstract fun contributeChatFragment(): ChatFragment

    @ContributesAndroidInjector
    abstract fun contributeCommonWebViewFragment(): CommonWebView

    @ContributesAndroidInjector
    abstract fun contributeEarnedRewardsFragment(): EarnedRewardsFragment

    @ContributesAndroidInjector
    abstract fun contributeReportedPostsFragment(): ReportedPostsFragment

    @ContributesAndroidInjector
    abstract fun contributeReportByMembersFragment(): ReportByMembersFragment

    @ContributesAndroidInjector
    abstract fun contributePortfolioDashboardFragment(): PortfolioDashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeAddExperienceFragment(): AddExperienceFragment

    @ContributesAndroidInjector
    abstract fun contributeAddBenefitsFragment(): AddBenefitsFragment

    @ContributesAndroidInjector
    abstract fun contributeAddMentoringPortfolioFragment(): AddMentoringPortfolioFragment

    @ContributesAndroidInjector
    abstract fun contributeUpdateExperienceFragment(): UpdateExperienceFragment

    @ContributesAndroidInjector
    abstract fun contributeAddSkillsFragment(): UpdateSkillsFragment

    @ContributesAndroidInjector
    abstract fun contributeUsersCommunityFragment(): UsersCommunityListFragment

    @ContributesAndroidInjector
    abstract fun contributeTodoListFragment(): TodoListFragment

    @ContributesAndroidInjector
    abstract fun contributeAddTodoTaskFragment(): AddTodoTaskFragment
}