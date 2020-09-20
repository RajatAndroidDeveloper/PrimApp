package com.primapp.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.primapp.R
import com.primapp.databinding.LayoutDialogHelpBinding
import com.primapp.ui.base.BaseDialogFragment

class PopUpHelpMessage : BaseDialogFragment<LayoutDialogHelpBinding>() {

    override fun getLayoutRes(): Int = R.layout.layout_dialog_help

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
    }

    private fun setData() {
        binding.pop = this
        binding.message = PopUpHelpMessageArgs.fromBundle(requireArguments()).message
    }
}