package com.primapp.ui.post.reported_post

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentReportedPostsBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class ReportedPostsFragment : BaseFragment<FragmentReportedPostsBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_reported_posts

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.reported_posts), toolbar)
    }
}