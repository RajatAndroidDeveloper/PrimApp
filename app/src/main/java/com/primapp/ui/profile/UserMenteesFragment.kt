package com.primapp.ui.profile

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentUserMenteesBinding
import com.primapp.ui.base.BaseFragment

class UserMenteesFragment :BaseFragment<FragmentUserMenteesBinding>(){

    override fun getLayoutRes(): Int = R.layout.fragment_user_mentees

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}