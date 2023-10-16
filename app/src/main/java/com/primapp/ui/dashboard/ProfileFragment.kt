package com.primapp.ui.dashboard

import android.app.DownloadManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentProfileBinding
import com.primapp.extensions.blink
import com.primapp.extensions.showError
import com.primapp.extensions.showSuccess
import com.primapp.model.DownloadFile
import com.primapp.model.ShowImage
import com.primapp.model.ShowVideo
import com.primapp.model.auth.UserData
import com.primapp.model.portfolio.PortfolioContent
import com.primapp.model.rewards.RewardsContent
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.ViewPagerCommunityAdapter
import com.primapp.ui.communities.members.CommunityMembersFragment
import com.primapp.ui.portfolio.adapter.MentoringPortfolioAdapter
import com.primapp.ui.portfolio.adapter.PortfolioBenefitsAdapter
import com.primapp.ui.portfolio.adapter.PortfolioExperienceAdapter
import com.primapp.ui.portfolio.adapter.PortfolioSkillsNCertificateAdapter
import com.primapp.ui.profile.UserJoinedCommunitiesFragment
import com.primapp.ui.profile.UserPostsFragment
import com.primapp.ui.splash.SplashViewModel
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.DialogUtils
import com.primapp.utils.DownloadUtils
import com.primapp.utils.visible
import com.primapp.viewmodels.PortfolioViewModel
import kotlinx.android.synthetic.main.layout_profile_top_card.*
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*
import javax.inject.Inject

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    var rewardsData: RewardsContent? = null
    lateinit var portfolioContent: PortfolioContent
    val viewModel by viewModels<SplashViewModel> { viewModelFactory }
    val mViewModel by viewModels<PortfolioViewModel> { viewModelFactory }

    private val adapterPortfolioExperience by lazy { PortfolioExperienceAdapter() }
    private val adapterPortfolioSkillsNCertificate by lazy { PortfolioSkillsNCertificateAdapter() }

    @Inject
    lateinit var downloadManager: DownloadManager

    override fun getLayoutRes(): Int = R.layout.fragment_profile

    lateinit var user: UserData

    lateinit var titles: ArrayList<SpannableStringBuilder>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.profile), toolbar)
        setData()
        setAdapter()
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

        mViewModel.userPortfolioLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }

                    Status.LOADING -> {
                        showLoading()
                    }

                    Status.SUCCESS -> {
                        it.data?.let { response ->
                            portfolioContent = response.content

                            loadDataToAdapters()
                        }
                    }
                }
            }
        })
    }

    private fun loadDataToAdapters() {
        binding.portfolioData = portfolioContent

        portfolioContent.experiences?.let { exp ->
            adapterPortfolioExperience.addData(exp)
        }

        portfolioContent.skills_certificate?.let { skillNCer ->
            adapterPortfolioSkillsNCertificate.addData(skillNCer)
        }
    }

    private fun setData() {
        analyticsManager.trackScreenView(AnalyticsManager.SCREEN_PROFILE)
        user = UserCache.getUser(requireContext())!!
        ivEndIcon.setImageResource(R.drawable.setting)
        ivEndIcon.setPadding(12)
        ivEndIcon.visible(false)

        toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.backgroundColor))
        clToolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.backgroundColor))
        tvTitle.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorAccent))

        ivMenu.setImageResource(R.drawable.back)
        ivMenu.visible(true)
        ivMenu.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.user = user
        binding.frag = this
        initViewPager()
        ivEndIcon.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        var isLoggedInUser = user.id == UserCache.getUserId(requireContext())
        binding.isLoggedInUser = isLoggedInUser

        ivPortfolio.visible(false)
        ivPortfolio.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("userId", user.id)
            bundle.putString("title", "${user.firstName} ${user.lastName}")
            if (!user.isPortfolioAvailable) {
                DialogUtils.showYesNoDialog(requireActivity(), R.string.create_mentoring_porfolio, {
                    findNavController().navigate(R.id.portfolioDashboardFragment, bundle)
                })
            } else {
                findNavController().navigate(R.id.portfolioDashboardFragment, bundle)
            }
        }

        tvReadMore.setOnClickListener {
            DialogUtils.showReadMoreDialog(
                requireActivity(),
                tvAboutDescription.text.toString().trim()
            )
        }
        tvDigitalTokenEarned.blink()
        mViewModel.getPortfolioData(userId = user.id)
    }

    fun editProfile() {
        findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
    }

    fun showImage() {
        val bundle = Bundle()
        bundle.putString("url", user.userImage)
        bundle.putBoolean("isInAppropriate", user.isInappropriate)
        bundle.putString("fullName", user.firstName + " " + user.lastName)
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

    private fun setAdapter() {
        rvExperience.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        rvExperience.adapter = adapterPortfolioExperience

        rvSkillsCertificate.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        rvSkillsCertificate.adapter = adapterPortfolioSkillsNCertificate
    }

    private fun onItemClick(item: Any?) {
        when (item) {
            is ShowImage -> {
                val bundle = Bundle()
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.imageViewDialog, bundle)
            }
            is ShowVideo -> {
                val bundle = Bundle()
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.videoViewDialog, bundle)
            }
            is DownloadFile -> {
                DownloadUtils.download(requireContext(), downloadManager, item.url)
            }
        }
    }

    fun onAddExperiences() {
        if (user.id != UserCache.getUserId(requireContext()) || !this::portfolioContent.isInitialized) {
            return
        }
        val bundle = Bundle()
        bundle.putSerializable("portfolioData", portfolioContent)
        if (portfolioContent.experiences.isNullOrEmpty()) {
            findNavController().navigate(R.id.addExperienceFragment, bundle)
        } else {
            findNavController().navigate(R.id.updateExperienceFragment, bundle)
        }
    }

    fun onAddSkills() {
        if (user.id != UserCache.getUserId(requireContext()) || !this::portfolioContent.isInitialized) {
            return
        }
        val bundle = Bundle()
        bundle.putSerializable("portfolioData", portfolioContent)
        findNavController().navigate(R.id.updateSkillsFragment, bundle)
    }
}