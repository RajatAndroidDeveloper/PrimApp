package com.primapp.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.primapp.R
import com.primapp.ui.base.BaseDialogFragment

object ProgressDialog {
    var dialog: Dialog? = null

    fun showProgressBar(activity: Context) {

        if (dialog != null && dialog!!.isShowing) {
            return
        }

        try {
            dialog = Dialog(activity)

            dialog?.apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                val layoutParams = window?.attributes
                layoutParams?.dimAmount = .5f
                window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setCancelable(false)
                setCanceledOnTouchOutside(false)
                setContentView(R.layout.progress_dialog)
                show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun dismissProgressDialog() {
        try {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}