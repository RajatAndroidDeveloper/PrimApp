package com.primapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.RadioGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.primapp.R
import com.primapp.constants.NotificationFilterTypes
import com.primapp.constants.ReportReasonTypes
import kotlinx.android.synthetic.main.layout_dialog_help1.*
import kotlinx.android.synthetic.main.layout_dialog_help1.btnClose
import kotlinx.android.synthetic.main.layout_dialog_help1.tvDialogMessage
import kotlinx.android.synthetic.main.layout_dialog_yes_no.*
import kotlinx.android.synthetic.main.layout_notification_filter.*
import android.content.DialogInterface


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


    fun showNotificationFilter(
        activity: Activity,
        selectedFilterType: String?,
        closeCallback: ((filterType: String?) -> Unit)? = null
    ) {
        val dialog = Dialog(activity, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_notification_filter)

        var selectedNotificationType: String? = null

        when (selectedFilterType) {
            NotificationFilterTypes.MENTORING_RELATIONSHIP -> {
                dialog.rgNotificationFilter.check(R.id.rbMentoringRelation)
                selectedNotificationType = NotificationFilterTypes.MENTORING_RELATIONSHIP
            }
            NotificationFilterTypes.COMMUNITY_RELATED -> {
                dialog.rgNotificationFilter.check(R.id.rbCommunityJoin)
                selectedNotificationType = NotificationFilterTypes.COMMUNITY_RELATED
            }
            NotificationFilterTypes.POST_RELATED -> {
                dialog.rgNotificationFilter.check(R.id.rbPostRelated)
                selectedNotificationType = NotificationFilterTypes.POST_RELATED
            }
            else -> {
                dialog.rgNotificationFilter.check(R.id.rbAll)
                selectedNotificationType = null
            }
        }

        dialog.rgNotificationFilter.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                when (p1) {
                    R.id.rbAll -> {
                        selectedNotificationType = null
                    }
                    R.id.rbMentoringRelation -> {
                        selectedNotificationType = NotificationFilterTypes.MENTORING_RELATIONSHIP
                    }
                    R.id.rbCommunityJoin -> {
                        selectedNotificationType = NotificationFilterTypes.COMMUNITY_RELATED
                    }
                    R.id.rbPostRelated -> {
                        selectedNotificationType = NotificationFilterTypes.POST_RELATED
                    }
                }
            }
        })

        dialog.btnClose.setOnClickListener {
            closeCallback?.invoke(selectedNotificationType)
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showChooserDialog(
        context: Context,
        title: String,
        options: Array<String>,
        closeCallback: ((selectedOption: Int) -> Unit)? = null
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
            // the user clicked on colors[which]
            closeCallback?.invoke(which)
        })
        builder.show()
    }
}
