package com.primapp.ui.contract

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentAllProjectsBinding
import com.primapp.extensions.setDivider
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class AllProjectsFragment : BaseFragment<FragmentAllProjectsBinding>(), AdapterView.OnItemSelectedListener,
    OnItemClickEvent {

    lateinit var adapter: CurrentProjectsAdapter
    override fun getLayoutRes() = R.layout.fragment_all_projects
    private var selectedStatus: String = ""

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        setToolbar(getString(R.string.all_projects), toolbar)
        setUpStatusSpinner()
        setAdapter()
    }

    private lateinit var statusArray: Array<String>
    private fun setUpStatusSpinner() {
        statusArray= requireActivity().resources.getStringArray(R.array.status)
        val adapter = ArrayAdapter(requireActivity(), R.layout.custom_spinner_item_layout, statusArray)
        binding.statusSpinner.adapter = adapter
        binding.statusSpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        selectedStatus = statusArray[p2]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun createProjectList(): ArrayList<ProjectData> {
        var projectsList: ArrayList<ProjectData> = ArrayList<ProjectData>()
        projectsList.add(ProjectData("Apple TV tester - need you to have Apple TV + iPAD + Macbook + iPhone to test Airplay", "we are looking for someone that\n" +
                    "1. have Apple TV\n" +
                    "2. have ipad\n" +
                    "3. have Macbook\n" +
                    "4. have iPhone\n" +
                    "need you to be test Airplay and few buttons on a..."))
        projectsList.add(ProjectData("Display the attached large PDF in iPhone (ios) and other OS in HTML5 Application","The attached file opens on Web and iOS but not on iPhone (Safari browser). We need some one who can fix this issue"))
        projectsList.add(ProjectData("Getting app upload","hello we have 4 apps to upload to playstore. We need your console where you can add my apps.my budget is 13\$ for 4 apps publish.\n" +
                "All the apps are ready in zip files and we will provide you all the publishing details."))
        return projectsList
    }

    private fun setAdapter() {
        var projectsList: ArrayList<ProjectData> = createProjectList()
        binding.rvAllProjects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            setDivider(R.drawable.recyclerview_divider)
        }

        adapter = CurrentProjectsAdapter("Ongoing", projectsList, this)
        binding.rvAllProjects.adapter = adapter
    }

    override fun onItemClick() {
        findNavController()?.navigate(R.id.action_allProjectsFragment_to_projectDetailsFragment)
    }
}