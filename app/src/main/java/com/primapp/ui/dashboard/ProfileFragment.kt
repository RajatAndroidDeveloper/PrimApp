package com.primapp.ui.dashboard

import android.content.Intent
import android.os.Bundle
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentProfileBinding
import com.primapp.ui.MainActivity
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_profile

    val user by lazy { UserCache.getUser(requireContext()) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("Profile", toolbar)
        setData()
    }

    private fun setData() {
        ivEndIcon.setImageResource(R.drawable.setting)
        binding.user = user

        btnLogout.setOnClickListener {
            UserCache.clearAll(requireContext())
            startActivity(Intent(requireContext(), MainActivity::class.java))
            activity?.finish()
        }

    }
}