package com.primapp.ui.communities
import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentCreatedCommunityBinding
import com.primapp.ui.base.BaseFragment

class CreatedCommunityFragment : BaseFragment<FragmentCreatedCommunityBinding>() {

    override fun getLayoutRes(): Int  = R.layout.fragment_created_community

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
}