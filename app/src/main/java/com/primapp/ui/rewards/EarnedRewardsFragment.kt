package com.primapp.ui.rewards

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentEarnedRewardsBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class EarnedRewardsFragment : BaseFragment<FragmentEarnedRewardsBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_earned_rewards

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("Earned Rewards", toolbar = toolbar)
        setData()
    }

    private fun setData() {
        val tokens = EarnedRewardsFragmentArgs.fromBundle(requireArguments()).tokens
        binding.tokens = tokens
    }
}