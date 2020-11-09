package com.primapp.ui.communities

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.FragmentAllCommunityBinding
import com.primapp.extensions.setDivider
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunitListAdapter


class AllCommunityFragment : BaseFragment<FragmentAllCommunityBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_all_community

    val adapter by lazy { CommunitListAdapter { item -> onItemClick(item) } }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setAdapter()
    }

    private fun setAdapter() {
        val recyclerView = binding.rvCommunityList
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.setDivider(R.drawable.recyclerview_divider)
        recyclerView.adapter = adapter
    }

    fun onItemClick(any: Any) {

    }
}