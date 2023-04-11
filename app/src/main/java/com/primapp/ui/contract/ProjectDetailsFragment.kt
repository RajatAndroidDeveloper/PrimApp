package com.primapp.ui.contract

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentProjectDetailsBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_menu_more.*

class ProjectDetailsFragment : BaseFragment<FragmentProjectDetailsBinding>() {

    override fun getLayoutRes() = R.layout.fragment_project_details

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        binding.frag = this
        binding.tvAboutDescription.text = getString(R.string.project_scope_dummy_text)
        binding.tvAboutDescription.setTrimLength(60)
    }

    fun navigateToAmendDialog(){
        findNavController().navigate(R.id.action_projectDetailsFragment_to_popUpAmendContract)
    }
}