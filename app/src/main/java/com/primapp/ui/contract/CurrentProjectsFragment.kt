package com.primapp.ui.contract

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentCurrentProjectsBinding
import com.primapp.extensions.setDivider
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedListAdapter
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.dashboard.MentorsMenteesAdaptor
import kotlinx.android.synthetic.main.fragment_current_projects.*
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class CurrentProjectsFragment : BaseFragment<FragmentCurrentProjectsBinding>() {

    override fun getLayoutRes() = R.layout.fragment_current_projects
    lateinit var adapter: CurrentProjectsAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        setToolbar(getString(R.string.current_projects), toolbar)
        setAdapter()
    }

    private fun createProjectList(): ArrayList<ProjectData> {
        var projectsList: ArrayList<ProjectData> = ArrayList<ProjectData>()
        projectsList.add(ProjectData("Apple TV tester - need you to have Apple TV + iPAD + Macbook + iPhone to test Airplay", "we are looking for someone that\n" +
                "1. have Apple TV\n" +
                "2. have ipad\n" +
                "3. have Macbook\n" +
                "4. have iPhone\n" +
                "need you to be test Airplay and few buttons on a..."))
        projectsList.add(ProjectData("Getting app upload","hello we have 4 apps to upload to playstore. We need your console where you can add my apps.my budget is 13\$ for 4 apps publish.\n" +
                "All the apps are ready in zip files and we will provide you all the publishing details."))
        return projectsList
    }

    private fun setAdapter() {
        var projectList = createProjectList()
        binding.rvCurrentProjects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            setDivider(R.drawable.recyclerview_divider)
        }

        binding.rvCompletedProjects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            setDivider(R.drawable.recyclerview_divider)
        }
    }

    fun openAllProjectsFragment(){
        findNavController().navigate(R.id.action_currentProjectsFragment_to_allProjectsFragment)
    }
}