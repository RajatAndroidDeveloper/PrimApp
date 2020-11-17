package com.primapp.ui.communities

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentCreateCommunityBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class CreateCommunityFragment : BaseFragment<FragmentCreateCommunityBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_create_community

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setToolbar(getString(R.string.create_community), toolbar)
    }

}