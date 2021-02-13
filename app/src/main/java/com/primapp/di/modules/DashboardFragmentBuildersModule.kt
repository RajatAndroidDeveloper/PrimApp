package com.primapp.di.modules

import com.primapp.ui.communities.*
import com.primapp.ui.communities.create.CreateCommunityFragment
import com.primapp.ui.communities.details.CommunityDetailsFragment
import com.primapp.ui.communities.edit.EditCommunityFragment
import com.primapp.ui.communities.members.CommunityMembersFragment
import com.primapp.ui.dashboard.NotificationsFragment
import com.primapp.ui.dashboard.ProfileFragment
import com.primapp.ui.post.UpdatesFragment
import com.primapp.ui.post.comment.PostCommentFragment
import com.primapp.ui.post.create.CreatePostFragment
import com.primapp.ui.post.reply.PostCommentReplyFragment
import com.primapp.ui.profile.*
import com.primapp.ui.profile.other.OtherUserProfileFragment
import com.primapp.ui.settings.AboutUsFragment
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
    abstract fun contributeUserMentorsFragment(): UserMentorsFragment

    @ContributesAndroidInjector
    abstract fun contributeUserMenteesFragment(): UserMenteesFragment

    @ContributesAndroidInjector
    abstract fun contributePostCommentFragment(): PostCommentFragment

    @ContributesAndroidInjector
    abstract fun contributeCommunityMembersFragment(): CommunityMembersFragment

    @ContributesAndroidInjector
    abstract fun contributePostCommentReplyFragment(): PostCommentReplyFragment

    @ContributesAndroidInjector
    abstract fun contributeOtherUserProfileFragment(): OtherUserProfileFragment

}