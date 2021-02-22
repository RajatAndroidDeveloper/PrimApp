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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentProfileBinding
import com.primapp.model.auth.UserData
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.ViewPagerCommunityAdapter
import com.primapp.ui.profile.UserJoinedCommunitiesFragment
import com.primapp.ui.profile.UserMenteesFragment
import com.primapp.ui.profile.UserMentorsFragment
import com.primapp.ui.profile.UserPostsFragment
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*


class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_profile

    lateinit var user: UserData

    lateinit var titles: ArrayList<SpannableStringBuilder>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.profile), toolbar)
        setData()
    }

    private fun setData() {
        user = UserCache.getUser(requireContext())!!
        ivEndIcon.setImageResource(R.drawable.setting)
        binding.user = user
        binding.frag = this
        initViewPager()
        ivEndIcon.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
    }

    fun editProfile() {
        findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
    }

    fun showImage() {
        val bundle = Bundle()
        bundle.putString("url", user.userImage)
        findNavController().navigate(R.id.imageViewDialog, bundle)
    }

    private fun initViewPager() {
        titles = initTitle()

        val userPostFragment = UserPostsFragment()
        userPostFragment.userId = user.id

        val fragmentList = arrayListOf<Fragment>(
            userPostFragment,
            UserJoinedCommunitiesFragment(user.id),
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