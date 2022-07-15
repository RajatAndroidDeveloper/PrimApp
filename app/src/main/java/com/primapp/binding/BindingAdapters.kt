package com.primapp.binding

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.bold
import androidx.databinding.BindingAdapter
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout
import com.primapp.R
import com.primapp.constants.*
import com.primapp.extensions.*
import com.primapp.model.auth.UserData
import com.primapp.model.community.CommunityData
import com.primapp.model.notification.NotificationResult
import com.primapp.model.post.ReportedMembers
import com.primapp.utils.DateTimeUtils
import com.primapp.utils.getPrettyNumber
import com.sendbird.android.*
import kotlin.math.min


@BindingAdapter("isRequired")
fun markRequiredInRed(textInput: TextInputLayout, isRequired: Boolean? = false) {
    if (isRequired == true) {
        textInput.hint = "${textInput.hint} *"
    }
}

@BindingAdapter("errorText")
fun textInputErrorFieldBinding(textInput: TextInputLayout, errorMessage: String?) {
    textInput.error = errorMessage
    errorMessage?.let {
        textInput.requestFocus()
    }
}

@BindingAdapter("errorText")
fun checkBoxErrorFieldBinding(checkBox: MaterialCheckBox, errorMessage: String?) {
    checkBox.error = errorMessage
    errorMessage?.let {
        checkBox.requestFocus()
    }
}

@BindingAdapter("spannableText")
fun makeSpannableText(textView: TextView, text: String?) {

    text?.apply {
        val span1 = SpannableString(this)

        span1.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(textView.context, R.color.colorAccent)),
            0,
            this.length,
            SPAN_INCLUSIVE_INCLUSIVE
        )

        span1.setSpan(StyleSpan(Typeface.BOLD), 0, this.length, SPAN_INCLUSIVE_INCLUSIVE)

        textView.text = TextUtils.concat(textView.text, " ", span1)
    }

}

@BindingAdapter("htmlText")
fun makeTermsPolicyText(textView: MaterialCheckBox, text: String?) {
    val htmlText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            text,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    } else {
        Html.fromHtml(text)
    }
    textView.text = htmlText
    textView.removeLinksUnderline()
    textView.movementMethod = LinkMovementMethod.getInstance()
}

@BindingAdapter("htmlText")
fun makeTermsPolicyText(textView: TextView, text: String?) {
    val htmlText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            text,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    } else {
        Html.fromHtml(text)
    }
    textView.text = htmlText
    textView.removeLinksUnderline()
    textView.movementMethod = LinkMovementMethod.getInstance()
}


@BindingAdapter("loadCircularImage")
fun loadCircularImageFromUrl(imgView: ImageView, url: String?) {
    imgView.loadCircularImage(imgView.context, url)
}

@BindingAdapter("loadCircularImage", "name")
fun loadCircularImageFromUrlWithName(imgView: ImageView, url: String?, name: String?) {
    imgView.loadCircularImageWithName(name, url)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("genderAndDobFormat")
fun genderAndDobFormatText(textView: TextView, user: UserData) {
    val dob = DateTimeUtils.getDateFromMillis(user.dateOfBirth)
    var text = ""

    if (user.genderValue.isNullOrEmpty() && dob.isEmpty()) {
        textView.visibility = View.GONE
    } else {
        textView.visibility = View.VISIBLE
    }

    if (!user.genderValue.isNullOrEmpty()) {
        text = user.genderValue
    }

    if (text.isNotEmpty())
        text = "$text | "

    if (dob.isNotEmpty()) {
        text = "$text$dob"
    }

    textView.text = text
}

@SuppressLint("SetTextI18n")
@BindingAdapter("genderDobCountry")
fun genderDobCountryTextView(textView: TextView, user: UserData?) {
    val dob = DateTimeUtils.getDateFromMillis(user?.dateOfBirth)
    var text = ""

    if (user?.genderValue.isNullOrEmpty() && dob.isEmpty() && user?.country.isNullOrEmpty()) {
        textView.visibility = View.GONE
    } else {
        textView.visibility = View.VISIBLE
    }

    if (!user?.genderValue.isNullOrEmpty()) {
        text = user?.genderValue.toString()
    }

    if (text.isNotEmpty())
        text = "$text | "

    if (dob.isNotEmpty()) {
        text = "$text$dob"
    }

    if (text.isNotEmpty())
        text = "$text | "

    if (!user?.country.isNullOrEmpty()) {
        text = "$text${user?.country}"
    }

    textView.text = text
}

@SuppressLint("SetTextI18n")
@BindingAdapter("membersAndCreatedDate", "type")
fun membersAndCreatedDate(textView: TextView, data: CommunityData?, type: String?) {
    data?.let {
        if (type == CommunityFilterTypes.CREATED_COMMUNITY) {
            if (data.adminStatus.equals("Approved", true)) {
                textView.text =
                    "${data.status} | ${DateTimeUtils.convertServerTimeStamp(data.cdate)}"
            } else {
                textView.text =
                    "${data.adminStatus} | ${DateTimeUtils.convertServerTimeStamp(data.cdate)}"
            }
        } else {
            textView.text =
                "${
                    textView.resources.getQuantityString(
                        R.plurals.member_count,
                        it.totalActiveMember.toInt(),
                        getPrettyNumber(it.totalActiveMember)
                    )
                } | ${DateTimeUtils.convertServerTimeStamp(data.cdate)}"
        }
    }
}

@BindingAdapter("isJoined", "isCreatedByMe", "type", "adminStatus")
fun joinButtonStyle(button: Button, isJoined: Boolean, isCreatedByMe: Boolean, type: String?, adminStatus: String?) {
    if (type == CommunityFilterTypes.CREATED_COMMUNITY) {
        if (adminStatus.equals("Pending", true)) {
            button.background = ContextCompat.getDrawable(button.context, R.drawable.button_primary_grey_filled)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.black))
            button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
            button.text = button.context.getString(R.string.edit)
            button.isEnabled = false
        } else {
            //Make the button as Edit
            button.background = ContextCompat.getDrawable(button.context, R.drawable.button_primary_blue_filled)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
            button.text = button.context.getString(R.string.edit)
            button.isEnabled = true
        }
    } else if (type == CommunityFilterTypes.COMMUNITY_DETAILS) {
        if (isJoined) {
            if (isCreatedByMe) {
                button.text = button.context.getString(R.string.edit)
                if (adminStatus.equals("Pending", true)) {
                    button.background = ContextCompat.getDrawable(button.context, R.drawable.button_primary_grey_filled)
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.black))
                    button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
                    button.isEnabled = false
                    return
                }
            } else {
                button.text = button.context.getString(R.string.leave)
            }
            button.background = ContextCompat.getDrawable(button.context, R.drawable.button_primary_blue_filled)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
            button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
            button.isEnabled = true
        } else {
            button.background = ContextCompat.getDrawable(button.context, R.drawable.button_light_accent_blue_outlined)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.colorAccent))
            button.isEnabled = true
        }
    } else {
        if (isJoined) {
            button.background = ContextCompat.getDrawable(button.context, R.drawable.button_primary_grey_filled)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.black))
            button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
            button.isEnabled = false
        } else {
            button.background = ContextCompat.getDrawable(button.context, R.drawable.button_light_accent_blue_outlined)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.colorAccent))
            button.isEnabled = true
        }
    }
}

@BindingAdapter("loadImageFromUrl")
fun loadImageFromUrl(imgView: ImageView, url: String?) {
    imgView.loadImageWithProgress(imgView.context, url)
}

@BindingAdapter("loadPostImageFromUrl")
fun loadPostImageFromUrl(imgView: ImageView, url: String?) {
    imgView.loadImageWithFitCenter(imgView.context, url)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("timeAgoFromTimeStamp")
fun timeAgoFromTimeStamp(textView: TextView, timeStamp: String?) {
    timeStamp?.let { textView.text = DateTimeUtils.getTimeAgoFromTimeStamp(it) }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("prettyNumber")
fun prettyNumber(textView: TextView, number: Int?) {
    number?.let { textView.text = getPrettyNumber(it.toLong()) }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("prettyNumber")
fun prettyNumber(textView: TextView, number: Long?) {
    number?.let { textView.text = getPrettyNumber(it) }
}


@SuppressLint("SetTextI18n")
@BindingAdapter("memberSince")
fun memberSince(textView: TextView, date: String?) {
    date?.let {
        textView.text = textView.resources.getString(R.string.member_since, DateTimeUtils.convertServerTimeStamp(it))
    }
}

@BindingAdapter("replyCount")
fun replyCount(textView: TextView, count: Long?) {
    count?.let {
        textView.text = textView.resources.getQuantityString(R.plurals.reply_count, it.toInt(), getPrettyNumber(it))
    }
}

@BindingAdapter("likeCount")
fun likeCount(textView: TextView, count: Long?) {
    count?.let {
        textView.text = textView.resources.getQuantityString(R.plurals.like_count, it.toInt(), getPrettyNumber(it))
    }
}

@BindingAdapter("inviteMentorButtonStyle")
fun inviteMentorButtonStyle(button: Button, status: Int) {
    if (status == MentorshipStatusTypes.ACCEPTED) {
        button.background = ContextCompat.getDrawable(button.context, R.drawable.button_primary_grey_filled)
        button.setTextColor(ContextCompat.getColor(button.context, R.color.black))
        button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
        button.isEnabled = true
        button.text = button.context.getString(R.string.end_mentorship)
    } else if (status == MentorshipStatusTypes.PENDING) {
        button.background = ContextCompat.getDrawable(button.context, R.drawable.button_primary_grey_filled)
        button.setTextColor(ContextCompat.getColor(button.context, R.color.black))
        button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
        button.isEnabled = false
        button.text = button.context.getString(R.string.requested)
    } else {
        button.background = ContextCompat.getDrawable(button.context, R.drawable.button_primary_blue_filled)
        button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
        button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
        button.isEnabled = true
        button.text = button.context.getString(R.string.invite_mentor)
    }
}

/*

@BindingAdapter("notificationTitle")
fun makeNotificationMentorRequest(textView: TextView, notificationData: NotificationResult?) {
    if (notificationData == null) return
    val mentorName = "${notificationData.sender?.firstName} ${notificationData.sender?.lastName}"
    var msg = ""
    if (notificationData.title.equals("sent", true)) {
        msg = "sent you a mentor request."
    } else if (notificationData.title.equals("rejected", true)
        && notificationData.notificationType.equals(NotificationTypes.MENTORSHIP_REQUEST_ACTION,true)) {
        msg = "You rejected"
    } else if (notificationData.title.equals("accepted", true)
        && notificationData.notificationType.equals(NotificationTypes.MENTORSHIP_REQUEST_ACTION,true)
    ) {
        msg = "You accepted"
    }else if (notificationData.title.equals("rejected", true)
        && notificationData.notificationType.equals(NotificationTypes.MENTORSHIP_UPDATE,true)) {
        msg = "Your request for mentorship is rejected by"
    } else if (notificationData.title.equals("accepted", true)
        && notificationData.notificationType.equals(NotificationTypes.MENTORSHIP_UPDATE,true)
    ) {
        msg = "Your request for mentorship is accepted by"
    }

    val text = when (notificationData.notificationType) {
        NotificationTypes.MENTORSHIP_REQUEST ->
            textView.context.getString(R.string.mentorship_request_msg, mentorName, msg)
        NotificationTypes.MENTORSHIP_REQUEST_ACTION ->
            textView.context.getString(R.string.mentorship_update_msg, msg, mentorName, "request for mentorship.")
        NotificationTypes.MENTORSHIP_UPDATE ->
            textView.context.getString(R.string.mentorship_update_msg, msg, mentorName, ".")
        else -> textView.context.getString(R.string.mentorship_request_msg, mentorName, msg)
    }

    val htmlText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            text,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    } else {
        Html.fromHtml(text)
    }
    textView.text = htmlText
    textView.removeLinksUnderline()
}
*/


@BindingAdapter("notificationTitle")
fun makeNotificationMentorRequest(textView: TextView, notificationData: NotificationResult?) {
    notificationData?.let {
        val colorToHighlight = ContextCompat.getColor(textView.context, R.color.colorAccent)
        val senderFullName = getHighlightedText(
            colorToHighlight,
            "${notificationData.sender?.firstName} ${notificationData.sender?.lastName}"
        )
        val communityName = getHighlightedText(colorToHighlight, it.community.communityName)
        val textToSend = SpannableStringBuilder("")
        when (it.notificationType) {
            NotificationTypes.MENTORSHIP_REQUEST -> {
                //Mentor side notification
                textToSend.append(senderFullName)
                    .append(" sent you a mentorship request in ")
                    .append(communityName)
            }
            NotificationTypes.MENTORSHIP_REQUEST_ACTION -> {
                //Mentor side notification
                if (it.title.equals(NotificationSubTypes.REQUEST_ACCEPTED, true)) {
                    textToSend.append("You accepted ")
                        .append(senderFullName)
                        .append("'s request for mentorship in ")
                        .append(communityName)
                } else if (it.title.equals(NotificationSubTypes.REQUEST_REJECTED, true)) {
                    textToSend.append("You rejected ")
                        .append(senderFullName)
                        .append("'s request for mentorship in ")
                        .append(communityName)
                } else if (it.title.equals(NotificationSubTypes.REQUEST_END, true)) {
                    textToSend.append("You ended the mentorship relation with ")
                        .append(senderFullName)
                        .append(" in ")
                        .append(communityName)
                }
            }
            NotificationTypes.MENTORSHIP_UPDATE -> {
                //Mentee side notification
                if (it.title.equals(NotificationSubTypes.REQUEST_ACCEPTED, true)) {
                    textToSend.append("Your request for mentorship in ")
                        .append(communityName)
                        .append(" is accepted by ")
                        .append(senderFullName)
                } else if (it.title.equals(NotificationSubTypes.REQUEST_REJECTED, true)) {
                    textToSend.append("Your request for mentorship in ")
                        .append(communityName)
                        .append(" is rejected by ")
                        .append(senderFullName)
                        .append(".\n").bold { }
                        .append("Reason : ")
                        .normal(it.message ?: "No reason specified")
                } else if (it.title.equals(NotificationSubTypes.REQUEST_END, true)) {
                    textToSend.append("Your mentorship relation was ended by ")
                        .append(senderFullName)
                        .append(" in ")
                        .append(communityName)
                        .append(".\n").bold { }
                        .append("Reason : ")
                        .normal(it.message ?: "No reason specified")
                }
            }
            NotificationTypes.COMMUNITY_NOTIFICATION -> {
                if (it.title.equals(NotificationSubTypes.COMMUNITY_JOIN_REQUEST, true)) {
                    textToSend.append(senderFullName)
                        .append(" joined your community ")
                        .append(communityName)
                } else if (it.title.equals(NotificationSubTypes.COMMUNITY_LEAVE_REQUEST, true)) {
                    textToSend.append(senderFullName)
                        .append(" left your community ")
                        .append(communityName)
                        .append(".\n")
                        .append("Reason : ")
                        .normal(it.message ?: "No reason specified")
                } else if (it.title.equals(NotificationSubTypes.COMMUNITY_REMOVE_REQUEST, true)) {
                    textToSend.append(senderFullName)
                        .append(" removed you from community ")
                        .append(communityName)
                }
            }
            NotificationTypes.POST_RELATED_NOTIFICATION -> {
                val trimmedPostText = it.postData?.postText?.substring(0, min(it.postData.postText.length, 20));

                if (it.title.equals(NotificationSubTypes.POST_LIKE, true)) {
                    textToSend.append(senderFullName)
                        .append(" liked your post ")

                    if (it.postData?.fileType == null) {
                        textToSend.append("\"$trimmedPostText...\" ")
                    }

                    textToSend.append("in ")
                        .append(communityName)
                } else if (it.title.equals(NotificationSubTypes.POST_COMMENT, true)) {
                    textToSend.append(senderFullName)
                        .append(" commented on your post ")

                    if (it.postData?.fileType == null) {
                        textToSend.append("\"$trimmedPostText...\" ")
                    }

                    textToSend.append("in ")
                        .append(communityName)
                } else if (it.title.equals(NotificationSubTypes.POST_REPLY, true)) {
                    textToSend.append(senderFullName)
                        .append(" replied to your comment ")

                    if (it.postData?.fileType == null && it.postData?.postText != null) {
                        textToSend.append("on ").append("\"$trimmedPostText...\" ")
                    }

                    textToSend.append("in ")
                        .append(communityName)
                } else if (it.title.equals(NotificationSubTypes.POST_COMMENT_LIKE, true)) {
                    textToSend.append(senderFullName)
                        .append(" liked your comment ")

                    if (it.postData?.fileType == null && it.postData?.postText != null) {
                        textToSend.append("on ").append("\"$trimmedPostText...\" ")
                    }

                    textToSend.append("in ")
                        .append(communityName)
                } else if (it.title.equals(NotificationSubTypes.POST_REPLY_LIKE, true)) {
                    textToSend.append(senderFullName)
                        .append(" liked your reply ")

                    if (it.postData?.fileType == null && it.postData?.postText != null) {
                        textToSend.append("on ").append("\"$trimmedPostText...\" ")
                    }

                    textToSend.append("in ")
                        .append(communityName)
                }
            }
            NotificationTypes.ADMIN_ACTION_NOTIFICATION -> {
                if (it.title.equals(NotificationSubTypes.COMMUNITY_APPROVED, true)) {
                    textToSend.append("Your community ")
                        .append(communityName)
                        .append(" has been approved")
                } else if (it.title.equals(NotificationSubTypes.COMMUNITY_REJECTED, true)) {
                    textToSend.append("Your community ")
                        .append(communityName)
                        .append(" has been rejected")
                        .append(".\n").bold { }
                        .append("Reason : ")
                        .normal(it.message ?: "No reason specified")
                }
            }
        }
        textToSend.append(".")
        textView.text = textToSend
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("lastMessage")
fun sendBirdMessage(textView: TextView, lastMessage: BaseMessage?) {
    if (lastMessage != null) {
        when (lastMessage) {
            is UserMessage -> textView.setText(lastMessage.message)
            is AdminMessage -> textView.setText(lastMessage.message)
            else -> {
                val fileMessage = lastMessage as FileMessage
                val fileType = fileMessage.type.split('/')
                if (fileType.size > 1) {
                    textView.text = "${fileType[0].capitalize()} file"
                } else {
                    textView.text = "${fileMessage.sender.nickname} has sent a file"
                }
            }
        }
    }

    if (textView.text.isNullOrEmpty()) {
        textView.text = "Tap to start the chat."
    }
}

@BindingAdapter("messageDateTime")
fun sendBirdMessageDateTime(textView: TextView, lastMessage: BaseMessage?) {
    lastMessage?.let {
        textView.text = DateTimeUtils.formatDateTime(it.getCreatedAt())
    }
}

@BindingAdapter("messageTime")
fun sendBirdMessageTime(textView: TextView, time: Long?) {
    time?.let {
        textView.text = DateTimeUtils.formatTime(time)
    }
}

@BindingAdapter("messageDate")
fun sendBirdMessageDate(textView: TextView, time: Long?) {
    time?.let {
        textView.text = DateTimeUtils.formatDate(time)
    }
}

@BindingAdapter("tokensEarned")
fun tokensEarned(textView: TextView, tokens: Int?) {
    textView.text = textView.resources.getString(R.string.digital_tokens_earned, (tokens ?: 0).toString())
    textView.isSelected = true
}

@BindingAdapter("rewardsCategory")
fun rewardsStageCategory(textView: TextView, accountStage: String?) {
    val drawable = when (accountStage) {
        RewardsCategory.SILVER -> {
            ContextCompat.getDrawable(textView.context, R.drawable.silver_bar)
        }
        RewardsCategory.GOLD -> {
            ContextCompat.getDrawable(textView.context, R.drawable.gold_bar)
        }
        RewardsCategory.PLATINUM -> {
            ContextCompat.getDrawable(textView.context, R.drawable.platinum_bar)
        }
        else -> {
            null
        }
    }

    textView.setCompoundDrawablesWithIntrinsicBounds(
        ContextCompat.getDrawable(textView.context, R.drawable.prim_coin),
        null,
        drawable,
        null
    )
}

@BindingAdapter("removedMemberReason")
fun removedMemberReasonText(textView: TextView, data: ReportedMembers?) {
    data?.let {
        textView.text = textView.resources.getString(
            R.string.reason_summary,
            ReportReasonTypes.getReportReasonMessage(textView.context, data.reportType) ?: data.message
        )
    }
}

@BindingAdapter("android:src")
fun setImageViewResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}