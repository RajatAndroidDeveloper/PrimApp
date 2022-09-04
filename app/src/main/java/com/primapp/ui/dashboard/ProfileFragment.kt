package com.primapp.ui.dashboard

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentProfileBinding
import com.primapp.extensions.blink
import com.primapp.extensions.showError
import com.primapp.extensions.showSuccess
import com.primapp.model.auth.UserData
import com.primapp.model.rewards.RewardsContent
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.ViewPagerCommunityAdapter
import com.primapp.ui.communities.members.CommunityMembersFragment
import com.primapp.ui.profile.UserJoinedCommunitiesFragment
import com.primapp.ui.profile.UserPostsFragment
import com.primapp.ui.splash.SplashViewModel
import com.primapp.utils.AnalyticsManager
import kotlinx.android.synthetic.main.layout_profile_top_card.*
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    var rewardsData: RewardsContent? = null

    val viewModel by viewModels<SplashViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_profile

    lateinit var user: UserData

    lateinit var titles: ArrayList<SpannableStringBuilder>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.profile), toolbar)
        setData()
        setObserver()
    }

    private fun setObserver() {
        viewModel.rewardsLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                pbDigitalToken.isVisible = false
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.LOADING -> {
                        pbDigitalToken.isVisible = true
                    }
                    Status.SUCCESS -> {
                        it.data?.let {
                            rewardsData = it.content
                            binding.rewardsData = it.content
                        }
                    }
                }
            }
        })

        viewModel.getRewardsData()
    }

    private fun setData() {
        analyticsManager.trackScreenView(AnalyticsManager.SCREEN_PROFILE)
        user = UserCache.getUser(requireContext())!!
        ivEndIcon.setImageResource(R.drawable.setting)
        binding.user = user
        binding.frag = this
        initViewPager()
        ivEndIcon.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        tvDigitalTokenEarned.blink()
    }

    fun editProfile() {
        findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
    }

    fun showImage() {
        val bundle = Bundle()
        bundle.putString("url", user.userImage)
        findNavController().navigate(R.id.imageViewDialog, bundle)
    }

    fun gotoEarnedRewards() {
        if (pbDigitalToken.isVisible) {
            return
        }

        val bundle = Bundle()
        bundle.putSerializable("rewardsData", rewardsData)
        findNavController().navigate(R.id.earnedRewardsFragment, bundle)
    }

    private fun initViewPager() {
        titles = initTitle()

        val userPostFragment = UserPostsFragment()
        userPostFragment.arguments = Bundle().apply {
            putInt("userId", user.id)
        }

        val userJoinedCommunity = UserJoinedCommunitiesFragment()
        userJoinedCommunity.arguments = Bundle().apply {
            putInt("userId", user.id)
            putString("type", UserJoinedCommunitiesFragment.CREATED)
        }

        val mentorMembersFragment = CommunityMembersFragment()
        mentorMembersFragment.arguments = Bundle().apply {
            putInt("userId", user.id)
            putString("type", CommunityMembersFragment.MENTOR_MEMBERS_LIST)
        }

        val menteeMemberFragment = CommunityMembersFragment()
        menteeMemberFragment.arguments = Bundle().apply {
            putInt("userId", user.id)
            putString("type", CommunityMembersFragment.MENTEE_MEMBERS_LIST)
        }

        val fragmentList = arrayListOf<Fragment>(
            userPostFragment,
            userJoinedCommunity,
            mentorMembersFragment,
            menteeMemberFragment
        )

        binding.viewPager.adapter = ViewPagerCommunityAdapter(
            fragmentList,
            childFragmentManager,
            lifecycle
        )


        val linearLayout: LinearLayout = binding.tabLayout.getChildAt(0) as LinearLayout
        linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        val drawable = GradientDrawable()
        drawable.setColor(Color.LTGRAY)
        drawable.setSize(1, 1)
        linearLayout.dividerPadding = 16
        linearLayout.dividerDrawable = drawable


        // attaching tab mediator
        TabLayoutMediator(binding.tabLayout, binding.viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                tab.text = titles[position]
            }
        ).attach()

    }

    fun refreshTabs() {
        user = UserCache.getUser(requireContext())!!
        titles = initTitle()
        binding.viewPager.adapter?.notifyDataSetChanged()
    }

    private fun initTitle(): ArrayList<SpannableStringBuilder> {
        return arrayListOf<SpannableStringBuilder>(
            getTabHeader(user.postsCount, "Posts"),
            getTabHeader(user.createdCommunityCount, "Communities"),
            getTabHeader(user.mentorCount, "Mentors"),
            getTabHeader(user.menteeCount, "Mentees")
        )
    }

    private fun getTabHeader(number: Int? = 0, text: String): SpannableStringBuilder {
        val num = number.toString()
        val sb = SpannableStringBuilder("$number")
        val boldStyleSpan: StyleSpan = StyleSpan(Typeface.BOLD)
        val normalStyleSpan: StyleSpan = StyleSpan(Typeface.NORMAL)
        sb.append("\n")
        sb.append(text)
        sb.setSpan(boldStyleSpan, 0, num.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        sb.setSpan(RelativeSizeSpan(1.3f), 0, num.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        sb.setSpan(normalStyleSpan, num.length + 1, sb.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return sb
    }
}