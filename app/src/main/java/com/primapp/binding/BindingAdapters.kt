package com.primapp.binding

import android.R.style
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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.bold
import androidx.core.text.toSpannable
import androidx.databinding.BindingAdapter
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.*
import com.primapp.extensions.*
import com.primapp.model.auth.UserData
import com.primapp.model.community.CommunityData
import com.primapp.model.contract.AcceptedByItem
import com.primapp.model.contract.AmendRequestItem
import com.primapp.model.contract.ResultsItem
import com.primapp.model.notification.NotificationResult
import com.primapp.model.portfolio.ExperienceData
import com.primapp.model.post.ReportedMembers
import com.primapp.model.todo.TodoTaskItem
import com.primapp.utils.DateTimeUtils
import com.primapp.utils.getPrettyNumber
import com.sendbird.android.*
import com.sendbird.android.message.AdminMessage
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.message.FileMessage
import com.sendbird.android.message.UserMessage
import java.text.DecimalFormat
import java.util.*
import kotlin.math.min


@BindingAdapter("isRequired")
fun markRequiredInRed(textInput: TextInputLayout, isRequired: Boolean? = false) {
    if (isRequired == true) {
        textInput.hint = "${textInput.hint} *"
    }
}

@BindingAdapter("isRequired", "hint")
fun markRequiredWithHint(textInput: TextInputLayout, isRequired: Boolean? = false, hint: String?) {
    if (isRequired == true) {
        textInput.hint = "$hint *"
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

@BindingAdapter("loadImageFromUrlWithName", "name")
fun loadImageFromUrlWithName(imgView: ImageView, url: String?, name: String?) {
    imgView.loadImageWithRoundedRectangularName(name, url)
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


    if (user?.id == UserCache.getUserId(textView.context)) {
        if (dob.isNotEmpty()) {
            text = "$text$dob"
        }
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
fun joinButtonStyle(
    button: Button,
    isJoined: Boolean,
    isCreatedByMe: Boolean,
    type: String?,
    adminStatus: String?
) {
    if (type == CommunityFilterTypes.CREATED_COMMUNITY) {
        if (adminStatus.equals("Pending", true)) {
            button.background =
                ContextCompat.getDrawable(button.context, R.drawable.button_primary_grey_filled)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.textColor))
            button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
            button.text = button.context.getString(R.string.edit)
            button.isEnabled = false
        } else {
            //Make the button as Edit
            button.background =
                ContextCompat.getDrawable(button.context, R.drawable.button_primary_blue_filled)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
            button.text = button.context.getString(R.string.edit)
            button.isEnabled = true
        }
    } else if (type == CommunityFilterTypes.COMMUNITY_DETAILS) {
        if (isJoined) {
            if (isCreatedByMe) {
                button.text = button.context.getString(R.string.edit)
                if (adminStatus.equals("Pending", true)) {
                    button.background = ContextCompat.getDrawable(
                        button.context,
                        R.drawable.button_primary_grey_filled
                    )
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.textColor))
                    button.typeface =
                        ResourcesCompat.getFont(button.context, R.font.poppins_regular)
                    button.isEnabled = false
                    return
                }
            } else {
                button.text = button.context.getString(R.string.leave)
            }
            button.background =
                ContextCompat.getDrawable(button.context, R.drawable.button_primary_blue_filled)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
            button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
            button.isEnabled = true
        } else {
            button.background = ContextCompat.getDrawable(
                button.context,
                R.drawable.button_light_accent_blue_outlined
            )
            button.setTextColor(ContextCompat.getColor(button.context, R.color.colorAccent))
            button.isEnabled = true
        }
    } else if (type == CommunityFilterTypes.COMMUNITY_PROFILE_BUTTON) {
        if (isJoined) {
            if (isCreatedByMe) {
                button.text = button.context.getString(R.string.edit)
                if (!adminStatus.equals("Pending", true)) {
                    button.background = ContextCompat.getDrawable(
                        button.context,
                        R.drawable.button_primary_blue_filled
                    )
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
                    button.typeface =
                        ResourcesCompat.getFont(button.context, R.font.poppins_regular)
                    button.isEnabled = true
                    return
                }
            }
            button.background =
                ContextCompat.getDrawable(button.context, R.drawable.button_primary_grey_filled)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.textColor))
            button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
            button.isEnabled = false
        } else {
            button.background = ContextCompat.getDrawable(
                button.context,
                R.drawable.button_light_accent_blue_outlined
            )
            button.setTextColor(ContextCompat.getColor(button.context, R.color.colorAccent))
            button.isEnabled = true
        }
    } else {
        if (isJoined) {
            button.background =
                ContextCompat.getDrawable(button.context, R.drawable.button_primary_grey_filled)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.textColor))
            button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
            button.isEnabled = false
        } else {
            button.background = ContextCompat.getDrawable(
                button.context,
                R.drawable.button_light_accent_blue_outlined
            )
            button.setTextColor(ContextCompat.getColor(button.context, R.color.colorAccent))
            button.isEnabled = true
        }
    }
}

@BindingAdapter("loadImageFromUrl")
fun loadImageFromUrl(imgView: ImageView, url: String?) {
    imgView.loadImageWithProgress(imgView.context, url)
}

@BindingAdapter("loadImageWithRoundedCorners")
fun loadImageWithRoundedCorners(imgView: ImageView, url: String?) {
    imgView.loadImageWithRoundedCorners(imgView.context, url)
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
@BindingAdapter("commentNumber")
fun commentNumber(textView: TextView, number: Int?) {
    number?.let { textView.text = getPrettyNumber(it.toLong()) + " Comments" }
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
        textView.text = textView.resources.getString(
            R.string.member_since,
            DateTimeUtils.convertServerTimeStamp(it)
        )
    }
}

@BindingAdapter("replyCount")
fun replyCount(textView: TextView, count: Long?) {
    count?.let {
        textView.text = textView.resources.getQuantityString(
            R.plurals.reply_count,
            it.toInt(),
            getPrettyNumber(it)
        )
    }
}

@BindingAdapter("likeCount")
fun likeCount(textView: TextView, count: Long?) {
    count?.let {
        textView.text = textView.resources.getQuantityString(
            R.plurals.like_count,
            it.toInt(),
            getPrettyNumber(it)
        )
    }
}

@BindingAdapter("inviteMentorButtonStyle")
fun inviteMentorButtonStyle(button: Button, status: Int) {
    if (status == MentorshipStatusTypes.ACCEPTED) {
        button.background =
            ContextCompat.getDrawable(button.context, R.drawable.button_primary_grey_filled)
        button.setTextColor(ContextCompat.getColor(button.context, R.color.textColor))
        button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
        button.isEnabled = true
        button.text = button.context.getString(R.string.end_mentorship)
    } else if (status == MentorshipStatusTypes.PENDING) {
        button.background =
            ContextCompat.getDrawable(button.context, R.drawable.button_primary_grey_filled)
        button.setTextColor(ContextCompat.getColor(button.context, R.color.textColor))
        button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
        button.isEnabled = false
        button.text = button.context.getString(R.string.requested)
    } else {
        button.background =
            ContextCompat.getDrawable(button.context, R.drawable.button_primary_blue_filled)
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
        val colorToHighlight = ContextCompat.getColor(textView.context, R.color.textColor)
        val senderFullName = getHighlightedText(
            colorToHighlight,
            "${notificationData.sender?.firstName} ${notificationData.sender?.lastName}"
        )
        val communityName =
            if (it.community != null) getHighlightedText(
                colorToHighlight,
                it.community.communityName
            ) else ""
        val textToSend = SpannableStringBuilder("")
        when (it.notificationType) {
            NotificationTypes.MENTORSHIP_REQUEST -> {
                //Mentor side notification
                textToSend.append(senderFullName)
                    .append(" sent you a mentorship request in ")
                    .append(communityName)
            }

            NotificationTypes.POST_VIRUS_DETECTED_NOTIFICATION -> {
                textToSend.append("A virus or unwanted malware was found in ")
                    .append(communityName).bold { }
                    .append(" community post. Please upload a clean file.")
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
                } else if (it.title.equals(NotificationSubTypes.NEW_COMMUNITY_CREATED, true)) {
                    textToSend.append(senderFullName)
                        .append(" created new community ")
                        .append(communityName)
                }
            }

            NotificationTypes.POST_RELATED_NOTIFICATION -> {
                val trimmedPostText =
                    it.postData?.postText?.substring(0, min(it.postData.postText.length, 20));

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

            NotificationTypes.CONTRACT_NOTIFICATION -> {
                textToSend.append(senderFullName).append(" has ")
                    .append(it.message?.toLowerCase())
            }
        }
        if (notificationData.notificationType != NotificationTypes.POST_VIRUS_DETECTED_NOTIFICATION) textToSend.append(
            "."
        )
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
                    textView.text = "${fileMessage.sender?.nickname} has sent a file"
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
        textView.text = DateTimeUtils.formatDateTime(it.createdAt)
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
        textView.text = DateTimeUtils.formatDate(time, DateTimeUtils.STRING_DATE_FORMAT)
    }
}

@BindingAdapter("tokensEarned")
fun tokensEarned(textView: TextView, tokens: Int?) {
    textView.text =
        textView.resources.getString(R.string.digital_tokens_earned, (tokens ?: 0).toString())
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
            ReportReasonTypes.getReportReasonMessage(textView.context, data.reportType)
                ?: data.message
        )
    }
}

@BindingAdapter("android:src")
fun setImageViewResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("pointsEarned")
fun removedMemberReasonText(textView: TextView, points: Int?) {
    points?.let {
        textView.text = textView.resources.getString(
            R.string.point_earned,
            it
        )
    }
}

@BindingAdapter("experienceText")
fun portfolioExperienceText(textView: TextView, data: ExperienceData?) {
    data?.let {
        var textToDisplay = ""
        if (it.isCurrentCompany) {
            textToDisplay = "Currently Working | ${it.country ?: ""}"
        } else {
            if (it.years != 0) {
                textToDisplay = "${
                    textView.resources.getQuantityString(
                        R.plurals.count_years,
                        it.years,
                        it.years
                    )
                } "
            }
            if (it.months != 0) {
                textToDisplay += "${
                    textView.resources.getQuantityString(
                        R.plurals.count_months,
                        it.months,
                        it.months
                    )
                } "
            }
            textToDisplay = "${textToDisplay}| ${it.country ?: ""}"
        }
        textView.text = textToDisplay
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("dueOn")
fun dueOn(textView: TextView, date: Long?) {
    date?.let {
        textView.text = textView.resources.getString(
            R.string.due_on,
            DateTimeUtils.getDateFromMillis(it, DateTimeUtils.CREATED_AT_DATE_FORMAT)
        )
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("createdOn")
fun createdOn(textView: TextView, date: String?) {
    date?.let {
        textView.text = textView.resources.getString(
            R.string.created_on,
            DateTimeUtils.convertServerTimeStamp(it, DateTimeUtils.CREATED_AT_DATE_FORMAT)
        )
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("todoDueDate")
fun todoDueDate(textView: TextView, taskItem: TodoTaskItem?) {
    taskItem?.let {
        if (taskItem.dueDate == null || taskItem.dueDate == 0L) {
            textView.text = textView.resources.getString(
                R.string.created_on,
                DateTimeUtils.convertServerTimeStamp(it.cdate, DateTimeUtils.CREATED_AT_DATE_FORMAT)
            )
        } else {
            textView.text = textView.resources.getString(
                R.string.due_on,
                DateTimeUtils.getDateFromMillis(it.dueDate, DateTimeUtils.CREATED_AT_DATE_FORMAT)
            )
        }

    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("contractCreatedStartEndDate")
fun contractCreatedStartEndDate(textView: TextView, data: ResultsItem?) {
    data?.let {
        textView.text =
            "Created Date - ${DateTimeUtils.convertServerTimeStamp(data.cdate)}\nStart Date - ${
                DateTimeUtils.getDateFromMillis(data.startDate)
            }\nEnd Date - ${DateTimeUtils.getDateFromMillis(data.endDate)}"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("contractPriceValue")
fun contractPriceValue(textView: TextView, price: String?) {
    price?.let {
        val amount: String = DecimalFormat.getCurrencyInstance(Locale.US).format(price.toDouble())
        textView.text = "$amount"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("contractEarningAmount")
fun contractEarningAmount(textView: TextView, price: String?) {
    price?.let {
        val amount: String = DecimalFormat.getCurrencyInstance(Locale.US).format(price.toDouble())
        textView.text = "+$amount"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("ratingValueTitle")
fun ratingValueTitle(textView: TextView, result: ResultsItem) {
    result.let {

    }
}


@SuppressLint("SetTextI18n")
@BindingAdapter("amendRequestTitle", "userData")
fun amendRequestTitle(textView: TextView, data: AmendRequestItem?, userData: UserData) {
    val textToSend = SpannableStringBuilder("")
    data?.let {
        val colorToHighlight = ContextCompat.getColor(textView.context, R.color.textColor)
        val fullName =
            getHighlightedText(
                colorToHighlight,
                "${data.requestBy?.firstName + " " + data.requestBy?.lastName}"
            )
        val amount = getHighlightedText(
            colorToHighlight,
            "${
                DecimalFormat.getCurrencyInstance(Locale.US)
                    .format((data.amount ?: "0.0").toDouble())
            }"
        )
        if (data.createdById!! == userData.id) {
            when (data.status) {
                "DECLINED" -> textToSend.append("You have declined the request from ")
                    .append(fullName)
                    .append(" to amend price to ").append(amount)

                "ACCEPTED" -> textToSend.append("You have accepted the request from ")
                    .append(fullName)
                    .append(" to amend price to ").append(amount)

                else -> textToSend.append(fullName).append(" has requested to amend price to ")
                    .append(amount)
            }
        } else {
            when (data.status) {
                "DECLINED" -> textToSend.append("Your request to amend price to ").append(amount)
                    .append(" has declined.")

                "ACCEPTED" -> textToSend.append("Your request to amend price to ").append(amount)
                    .append(" has approved.")

                else -> textToSend.append("You have requested to amend price to ").append(amount)
            }
        }
        textView.text = textToSend
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("amendingReasonTitle", "userData")
fun amendingReasonTitle(textView: TextView, data: AmendRequestItem?, userData: UserData) {
    val textToSend = SpannableStringBuilder("Reason: ")
    data?.let {
        val colorToHighlight = ContextCompat.getColor(textView.context, R.color.textColor)
        val reason = getHighlightedText(colorToHighlight, "${data.reason ?: ""}")
        textView.text = textToSend.append(reason)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("acceptingReasonTitle")
fun acceptingReasonTitle(textView: TextView, data: AcceptedByItem?) {
    val textToSend = SpannableStringBuilder("Reason: ")
    data?.let {
        val colorToHighlight = ContextCompat.getColor(textView.context, R.color.textColor)
        val reason = getHighlightedText(colorToHighlight, "${it.reason ?: ""}")
        textView.text = textToSend.append(reason)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("amendingReplyTitle", "userData")
fun amendingReplyTitle(textView: TextView, data: AmendRequestItem?, userData: UserData) {
    val textToSend = SpannableStringBuilder("Reply: ")
    data?.let {
        val colorToHighlight = ContextCompat.getColor(textView.context, R.color.textColor)
        val reason = getHighlightedText(colorToHighlight, "${data.actionReason ?: ""}")
        textView.text = textToSend.append(reason)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("contractAcceptedTitle")
fun contractAcceptedTitle(textView: TextView, data: AcceptedByItem?) {
    val textToSend = SpannableStringBuilder("")
    data?.let {
        val colorToHighlight = ContextCompat.getColor(textView.context, R.color.textColor)
        val fullName = getHighlightedText(
            colorToHighlight,
            "${data.user?.firstName + " " + data.user?.lastName}"
        )
        textView.text = textToSend.append(fullName).append(" has accepted the contract.")
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("contractStatusTitle")
fun contractStatusTitle(textView: TextView, data: String?) {
    data.let {
        when (it) {
            "IN_PROGRESS" -> textView.text = "In Progress"
            "COMPLETED" -> textView.text = "Completed"
            else -> textView.text = "Not Started"
        }
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@SuppressLint("SetTextI18n")
@BindingAdapter("backgroundTint")
fun backgroundTint(textView: TextView, status: String?) {
    status?.let {
        when (it) {
            "IN_PROGRESS" -> textView.backgroundTintList =
                ContextCompat.getColorStateList(textView.context, R.color.green)

            "COMPLETED" -> textView.backgroundTintList =
                ContextCompat.getColorStateList(textView.context, R.color.colorAccent)

            else -> textView.backgroundTintList =
                ContextCompat.getColorStateList(textView.context, R.color.lightestGreyHint)
        }
    }
}