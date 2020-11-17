package com.primapp.ui.settings

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentAboutUsBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class AboutUsFragment : BaseFragment<FragmentAboutUsBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_about_us

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.about_us), toolbar)
        setData()
    }

    private fun setData() {

    }
}