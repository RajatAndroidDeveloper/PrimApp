package com.primapp.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.primapp.R
import kotlinx.android.synthetic.main.layout_dialog_help1.*
import kotlinx.android.synthetic.main.layout_dialog_yes_no.*


object DialogUtils {

    fun showCloseDialog(
        activity: Activity,
        @StringRes messageId: Int,
        @DrawableRes drawableRes: Int? = null,
        closeCallback: (() -> Unit)? = null
    ) {
        val dialog = Dialog(activity, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_help1)
        drawableRes?.let { dialog.ivDialogImage.setImageResource(it) }
        dialog.tvDialogMessage.text = activity.getString(messageId)

        dialog.btnClose.setOnClickListener {
            closeCallback?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showCloseDialog(
        activity: Activity,
        message: String,
        @DrawableRes drawableRes: Int? = null,
        closeCallback: (() -> Unit)? = null
    ) {
        val dialog = Dialog(activity, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_help1)
        drawableRes?.let { dialog.ivDialogImage.setImageResource(it) }
        dialog.tvDialogMessage.text = message

        dialog.btnClose.setOnClickListener {
            closeCallback?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showYesNoDialog(
        activity: Activity,
        @StringRes messageId: Int,
        yesClickCallback: (() -> Unit)? = null,
        noClickCallback: (() -> Unit)? = null
    ) {
        val dialog = Dialog(activity, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_yes_no)
        dialog.tvDialogYesNoMessage.text = activity.getString(messageId)

        dialog.btnYes.setOnClickListener {
            yesClickCallback?.invoke()
            dialog.dismiss()
        }

        dialog.btnNo.setOnClickListener {
            noClickCallback?.invoke()
            dialog.dismiss()
        }
        dialog.show()
    }
}