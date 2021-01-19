package com.primapp.ui.dashboard

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentUpdatesBinding
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunitListAdapter

class UpdatesFragment : BaseFragment<FragmentUpdatesBinding>() {

    val adapter by lazy { CommunitListAdapter { item -> onItemClick(item) } }

    override fun getLayoutRes(): Int = R.layout.fragment_updates

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
        setAdapter()
    }

    private fun setData() {
        binding.frag = this
    }

    private fun setAdapter() {
        binding.rvCommunityPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.rvCommunityPosts.adapter = adapter
    }

    fun createPost() {
        findNavController().navigate(UpdatesFragmentDirections.actionUpdatesFragmentToCreatePostFragment())
    }

    private fun onItemClick(item: Any) {
        TODO("Not yet implemented")
    }
}