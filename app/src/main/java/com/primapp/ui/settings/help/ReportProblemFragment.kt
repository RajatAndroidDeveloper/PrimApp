package com.primapp.ui.settings.help

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentReportProblemBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class ReportProblemFragment() : BaseFragment<FragmentReportProblemBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_report_problem

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.report_a_problem), toolbar)

    }
}