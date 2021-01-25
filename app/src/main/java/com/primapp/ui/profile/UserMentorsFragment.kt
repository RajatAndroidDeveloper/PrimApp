package com.primapp.ui.profile

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentUserMentorsBinding
import com.primapp.ui.base.BaseFragment

class UserMentorsFragment :BaseFragment<FragmentUserMentorsBinding>(){

    override fun getLayoutRes(): Int = R.layout.fragment_user_mentors

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}