package com.primapp.ui.post

import android.os.Bundle
import android.widget.RadioGroup
import androidx.core.view.isVisible
import com.primapp.R
import com.primapp.databinding.FragmentCreatePostBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*


class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_create_post

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.create_post), toolbar)

        binding.rgFileType.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                when (p1) {
                    R.id.rbImage -> {
                        binding.groupSelectFile.isVisible = true
                    }
                    R.id.rbVideo -> {
                        binding.groupSelectFile.isVisible = true
                    }
                    R.id.rbGif -> {
                        binding.groupSelectFile.isVisible = true
                    }
                    R.id.rbNone -> {
                        binding.groupSelectFile.isVisible = false
                    }
                }
            }
        })
    }
}