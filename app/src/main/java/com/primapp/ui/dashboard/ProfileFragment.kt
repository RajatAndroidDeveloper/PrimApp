package com.primapp.ui.dashboard

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentProfileBinding
import com.primapp.model.auth.UserData
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_profile

    lateinit var user: UserData

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.profile), toolbar)
        setData()
    }

    private fun setData() {
        user = UserCache.getUser(requireContext())!!
        ivEndIcon.setImageResource(R.drawable.setting)
        binding.user = user
        binding.frag = this

        ivEndIcon.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

    }

    fun editProfile() {
        findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
    }
}