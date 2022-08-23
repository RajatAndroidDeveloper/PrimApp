package com.primapp.ui.rewards

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.primapp.R
import com.primapp.databinding.FragmentEarnedRewardsBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class EarnedRewardsFragment : BaseFragment<FragmentEarnedRewardsBinding>() {

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
        val rewardsData = EarnedRewardsFragmentArgs.fromBundle(requireArguments()).rewardsData
        Log.d("anshul_rewards->", Gson().toJson(rewardsData))
        binding.rewardsData = rewardsData
    }
}