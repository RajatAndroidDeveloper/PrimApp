package com.primapp.utils

import android.os.Bundle
import androidx.navigation.fragment.findNavController
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
        PopUpHelpMessageArgs.fromBundle(requireArguments()).let {
            binding.message = it.message
            binding.isHelperDialog = it.isHelperDialog
        }
        isCancelable = false
    }

    fun dismissDialog() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set("key", true)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "sourceId",
            PopUpHelpMessageArgs.fromBundle(requireArguments()).sourceId
        )
        dismiss()
    }
}