package com.primapp.ui.portfolio

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentAddBenefitsBinding
import com.primapp.extensions.showInfo
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_community_back.*

class AddBenefitsFragment : BaseFragment<FragmentAddBenefitsBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_add_benefits

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.benefits), toolbar)
        setData()
    }

    fun setData() {
        binding.frag = this

        ivAdd.setOnClickListener {
            showInfo(requireContext(), getString(R.string.not_yet_implemented))
        }
    }
}