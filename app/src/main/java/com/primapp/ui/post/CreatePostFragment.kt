package com.primapp.ui.post

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentCreatePostBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_create_post

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.create_post), toolbar)

    }
}