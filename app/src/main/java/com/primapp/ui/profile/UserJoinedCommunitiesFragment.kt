package com.primapp.ui.profile

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentUserJoinedCommunitiesBinding
import com.primapp.ui.base.BaseFragment

class UserJoinedCommunitiesFragment :BaseFragment<FragmentUserJoinedCommunitiesBinding>(){

    override fun getLayoutRes(): Int = R.layout.fragment_user_joined_communities

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}