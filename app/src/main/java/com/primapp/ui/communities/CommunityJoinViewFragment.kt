package com.primapp.ui.communities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.primapp.R
import com.primapp.databinding.FragmentCommunityJoinViewBinding
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.ViewPagerCommunityAdapter
import kotlinx.android.synthetic.main.toolbar_community_back.*


class CommunityJoinViewFragment : BaseFragment<FragmentCommunityJoinViewBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_community_join_view

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
        initViewPager()

        setClicks()
    }

    private fun setClicks() {
        ivTickMark.setOnClickListener {
            findNavController().navigate(R.id.dashboardActivity)
            activity?.finish()
        }

        ivAdd.setOnClickListener {
            findNavController().navigate(R.id.createCommunityFragment)
        }
    }

    private fun setData() {
        val title: String = CommunityJoinViewFragmentArgs.fromBundle(requireArguments()).title
        setToolbar(title, toolbar)

        if (CommunityJoinViewFragmentArgs.fromBundle(requireArguments()).isNewUser) {
            ivTickMark.visibility = View.VISIBLE
        } else {
            ivTickMark.visibility = View.GONE
        }
    }

    private fun initViewPager() {
        val titles = arrayListOf<String>(
            "All",
            "Joined",
            "Created"
        )

        val fragmentList = arrayListOf<Fragment>(
            AllCommunityFragment(),
            JoinedCommunityFragment(),
            CreatedCommunityFragment()
        )

        binding.viewPager.adapter = ViewPagerCommunityAdapter(
            fragmentList,
            childFragmentManager,
            lifecycle
        )

        // attaching tab mediator
        TabLayoutMediator(binding.tabLayout, binding.viewPager,
            TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                tab.text = titles[position]
            }
        ).attach()

    }
}