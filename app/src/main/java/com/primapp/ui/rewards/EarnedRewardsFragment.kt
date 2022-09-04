package com.primapp.ui.rewards

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.primapp.R
import com.primapp.databinding.FragmentEarnedRewardsBinding
import com.primapp.model.rewards.RewardsContent
import com.primapp.retrofit.ApiConstant
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class EarnedRewardsFragment : BaseFragment<FragmentEarnedRewardsBinding>() {

    private var rewardsData: RewardsContent? = null

    override fun getLayoutRes(): Int = R.layout.fragment_earned_rewards

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.earned_tokens), toolbar = toolbar)
        setData()
        setAnimationListeners()
    }

    private fun setAnimationListeners() {
        binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                binding.animationView.isVisible = false
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }

    private fun setData() {
        rewardsData = EarnedRewardsFragmentArgs.fromBundle(requireArguments()).rewardsData

        binding.frag = this
        binding.rewardsData = rewardsData
    }

    fun redeemTokens() {
        rewardsData?.redeemUrl?.let {
            val bundle = Bundle()
            bundle.putString("title", "Redeem Tokens")
            bundle.putString("url", it)
            findNavController().navigate(R.id.commonWebView, bundle)
        }
    }
}