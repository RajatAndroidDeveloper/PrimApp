package com.primapp.ui.communities

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentCommunitiesBinding
import com.primapp.ui.base.BaseFragment

class CommunitiesFragment : BaseFragment<FragmentCommunitiesBinding>() {

    var isNewUser: Boolean = false

    override fun getLayoutRes(): Int = R.layout.fragment_communities

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()

    }

    private fun setData() {
        binding.frag = this
        isNewUser = CommunitiesFragmentArgs.fromBundle(requireArguments()).isNewUser
    }

    fun openCommunities(name: String) {
        val bundle = Bundle()
        bundle.putString("title", name)
        bundle.putBoolean("isNewUser", isNewUser)
        findNavController().navigate(R.id.communityJoinViewFragment, bundle)
    }

}