package com.primapp.ui.portfolio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.primapp.R
import com.primapp.databinding.FragmentAddExperienceBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class AddExperienceFragment : BaseFragment<FragmentAddExperienceBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_add_experience

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.experiences), toolbar)
        setData()
    }

    fun setData() {
        binding.frag = this
    }
}