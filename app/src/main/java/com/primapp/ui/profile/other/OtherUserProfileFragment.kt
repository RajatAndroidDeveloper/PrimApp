package com.primapp.ui.profile.other

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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentOtherUserProfileBinding
import com.primapp.extensions.showError
import com.primapp.model.auth.UserData
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.ViewPagerCommunityAdapter
import com.primapp.ui.profile.UserJoinedCommunitiesFragment
import com.primapp.ui.profile.UserMenteesFragment
import com.primapp.ui.profile.UserMentorsFragment
import com.primapp.ui.profile.UserPostsFragment
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.android.synthetic.main.fragment_other_user_profile.*

class OtherUserProfileFragment : BaseFragment<FragmentOtherUserProfileBinding>() {

    var userId: Int? = null

    lateinit var user: UserData

    lateinit var titles: ArrayList<SpannableStringBuilder>

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_other_user_profile

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        setData()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
        userId = OtherUserProfileFragmentArgs.fromBundle(requireArguments()).userId

        if (isLoaded) {
            return
        }
        viewModel.getUserData(userId!!)
    }

    private fun setObserver() {
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            it.peekContent().let {
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.LOADING -> {
                        showLoading(true)
                        binding.includeProfileCard.clProfileCard.isVisible = false
                    }
                    Status.SUCCESS -> {
                        showLoading(false)
                        it.data?.content?.let {
                            binding.includeProfileCard.clProfileCard.isVisible = true
                            user = it
                            binding.user = user
                            if (UserCache.getUserId(requireContext()) == it.id) {
                                //Update user data if viewing own profile
                                UserCache.saveUser(requireContext(), it)
                            }
                            initViewPager()
                        }
                    }
                }
            }
        })
    }

    fun showImage() {
        val bundle = Bundle()
        bundle.putString("url", user.userImage)
        findNavController().navigate(R.id.imageViewDialog, bundle)
    }

    fun showLoading(visible: Boolean) {
        // if loader is shown then hide other views
        binding.includeProfileCard.clProfileCard.isVisible = !visible
        binding.tabLayout.isVisible = !visible
        binding.viewPager.isVisible = !visible
        binding.progressBar.isVisible = visible
    }

    //----------------------- View Pager and Tabs Data ---------------------------

    private fun initViewPager() {
        titles = initTitle()

        val userPostFragment = UserPostsFragment()
        userPostFragment.userId = userId!!

        val fragmentList = arrayListOf<Fragment>(
            userPostFragment,
            UserJoinedCommunitiesFragment(userId!!),
            UserMentorsFragment(),
            UserMenteesFragment()
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
            getTabHeader(user.joinedCommunityCount, "Communities"),
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