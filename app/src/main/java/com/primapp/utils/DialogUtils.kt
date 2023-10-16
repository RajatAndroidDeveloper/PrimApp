package com.primapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.RadioGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.google.android.flexbox.FlexboxLayoutManager
import com.primapp.R
import com.primapp.constants.NotificationFilterTypes
import com.primapp.model.auth.ReferenceItems
import com.primapp.model.portfolio.BenefitsData
import com.primapp.ui.initial.AutocompleteListArrayAdapter
import com.primapp.ui.portfolio.adapter.PortfolioAddRemoveItemAdapter
import com.primapp.ui.portfolio.adapter.PortfolioBenefitsAdapter
import kotlinx.android.synthetic.main.layout_dialog_choose_media_option.ivClose
import kotlinx.android.synthetic.main.layout_dialog_choose_media_option.llImage
import kotlinx.android.synthetic.main.layout_dialog_choose_media_option.llVideo
import kotlinx.android.synthetic.main.layout_dialog_choose_media_option.tvClose
import kotlinx.android.synthetic.main.layout_dialog_edittext.*
import kotlinx.android.synthetic.main.layout_dialog_edittext.btnSave
import kotlinx.android.synthetic.main.layout_dialog_edittext.ivDialogCloseIcon
import kotlinx.android.synthetic.main.layout_dialog_edittext.tvDialogTitle
import kotlinx.android.synthetic.main.layout_dialog_help1.*
import kotlinx.android.synthetic.main.layout_dialog_help1.btnClose
import kotlinx.android.synthetic.main.layout_dialog_help1.tvDialogMessage
import kotlinx.android.synthetic.main.layout_dialog_read_more.ivCloseDialog
import kotlinx.android.synthetic.main.layout_dialog_search_text.*
import kotlinx.android.synthetic.main.layout_dialog_yes_no.*
import kotlinx.android.synthetic.main.layout_dialog_yes_no.btnNo
import kotlinx.android.synthetic.main.layout_dialog_yes_no.btnYes
import kotlinx.android.synthetic.main.layout_dialog_yes_no.tvDialogYesNoMessage
import kotlinx.android.synthetic.main.layout_dialog_yes_no_desc.*
import kotlinx.android.synthetic.main.layout_notification_filter.*


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

    fun showYesNoDialog(
        activity: Activity,
        @StringRes messageId: Int,
        description: String,
        yesClickCallback: (() -> Unit)? = null,
        noClickCallback: (() -> Unit)? = null
    ) {
        val dialog = Dialog(activity, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_yes_no_desc)
        dialog.tvDialogYesNoMessage.text = activity.getString(messageId)
        dialog.tvDialogYesNoDescription.text = description

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


    fun showEditTextDialog(
        activity: Activity,
        title: String?,
        description: String?,
        value: String?,
        hintText: String?,
        suggestionList: ArrayList<BenefitsData>?,
        saveCallback: ((filterType: String?) -> Unit)? = null,
        closeCallback: (() -> Unit)? = null
    ) {
        val dialog = Dialog(activity, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_edittext)

        dialog.tvDialogTitle.text = title
        dialog.tvDialogTitle.isVisible = !title.isNullOrEmpty()

        dialog.tvDialogMessage.text = description
        dialog.tvDialogMessage.isVisible = !description.isNullOrEmpty()
        val editText = dialog.etDialogText
        editText.setText(value)
        editText.hint = hintText
        //List Related changes
        dialog.tvSuggestions.isVisible = !suggestionList.isNullOrEmpty()
        dialog.rvSuggestions.isVisible = !suggestionList.isNullOrEmpty()
        //val list: List<BenefitsData>? = suggestionList?.filterIsInstance<BenefitsData>()
        if (!suggestionList.isNullOrEmpty()) {
            val adapter = PortfolioBenefitsAdapter { item ->
                if (item is String) {
                    saveCallback?.invoke(item)
                    dialog.dismiss()
                }
            }
            dialog.rvSuggestions.apply {
                this.layoutManager = FlexboxLayoutManager(activity)
            }
            dialog.rvSuggestions.adapter = adapter
            adapter.addData(suggestionList)
        }

        dialog.btnSave.setOnClickListener {
            saveCallback?.invoke(editText.text.toString())
            dialog.dismiss()
        }

        dialog.ivDialogCloseIcon.setOnClickListener {
            closeCallback?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showCameraChooserDialog(
        activity: Context,
        saveCallback: ((filterType: String?) -> Unit)? = null
    ) {
        val dialog = Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.layout_dialog_choose_media_option)

        dialog.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.llImage.setOnClickListener {
            saveCallback?.invoke("Image")
            dialog.dismiss()
        }

        dialog.llVideo.setOnClickListener {
            saveCallback?.invoke("Video")
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showSearchTextDialog(
        activity: Activity,
        title: String?,
        skillsList: ArrayList<ReferenceItems>,
        saveCallback: ((selectedId: List<Int>) -> Unit)? = null,
        closeCallback: (() -> Unit)? = null
    ) {
        val dialog = Dialog(activity, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_search_text)

        dialog.tvDialogTitle.text = title
        dialog.tvDialogTitle.isVisible = !title.isNullOrEmpty()

        //For selected item Array logic
        val adapterSelectedItems = PortfolioAddRemoveItemAdapter()
        dialog.rvItems.apply {
            this.layoutManager = FlexboxLayoutManager(activity)
        }
        dialog.rvItems.adapter = adapterSelectedItems
        //End selected item Array Logic

        //var selectedIdToSend: Int? = null

        val adapterSkillsList = AutocompleteListArrayAdapter(activity, R.layout.item_simple_text)
        adapterSkillsList.addAll(skillsList)

        dialog.mAutoCompleteSkills.setAdapter(adapterSkillsList)
//        dialog.mAutoCompleteSkills.validator = object : AutoCompleteTextView.Validator {
//            override fun fixText(p0: CharSequence?): CharSequence {
//                return ""
//            }
//
//            override fun isValid(p0: CharSequence?): Boolean {
//                val isDataValid = adapterSkillsList.contains(p0.toString())
//                if (isDataValid) {
//                    adapterSelectedItems.addItem(adapterSkillsList.getItem(p0.toString()))
//                    dialog.tlSearchBox.error = null
//                } else {
//                    dialog.tlSearchBox.error = activity.resources.getString(R.string.valid_empty_skill_textbox)
//                }
//
//                return isDataValid
//            }
//        }

        dialog.mAutoCompleteSkills.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            dialog.tlSearchBox.error = null
            adapterSelectedItems.addItem(adapterSkillsList.getItem(position))
            dialog.mAutoCompleteSkills.setText("")
        }

        dialog.btnSave.setOnClickListener {
            dialog.mAutoCompleteSkills.clearFocus()
            if (dialog.mAutoCompleteSkills.error == null && adapterSelectedItems.list.isNotEmpty()) {
                saveCallback?.invoke(adapterSelectedItems.list.map { it.itemId!! })
                dialog.dismiss()
            } else {
                dialog.tlSearchBox.error = activity.resources.getString(R.string.valid_empty_skill)
            }
        }

        dialog.ivDialogCloseIcon.setOnClickListener {
            closeCallback?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }



    fun showReadMoreDialog(
        activity: Activity,
        messageDescription: String,
        noClickCallback: (() -> Unit)? = null
    ) {
        val dialog = Dialog(activity,android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_dialog_read_more)
        dialog.setCanceledOnTouchOutside(true)
        dialog.tvDialogMessage.text = messageDescription

        dialog.ivCloseDialog.setOnClickListener {
            noClickCallback?.invoke()
            dialog.dismiss()
        }
        dialog.show()
    }

}
