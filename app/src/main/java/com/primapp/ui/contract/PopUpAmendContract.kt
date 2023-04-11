package com.primapp.ui.contract

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.PopupAmendContractLayoutBinding
import com.primapp.ui.base.BaseDialogFragment

class PopUpAmendContract: BaseDialogFragment<PopupAmendContractLayoutBinding>(){

    override fun getLayoutRes() = R.layout.popup_amend_contract_layout

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.pop = this
        isCancelable = false
    }

    fun closeDialog(){
        dialog?.dismiss()
    }
}