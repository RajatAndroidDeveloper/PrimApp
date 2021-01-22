package com.primapp.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*


object DateTimeUtils {

    const val SEND_DOB_FORMAT = "yyyy/MM/dd"
    const val DOB_FORMAT = "MMM dd, yyyy"

    const val DEFAULT_SERVER_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    const val STRING_DATE_FORMAT = "dd MMM yyyy"

    fun getDateFromPicker(calendar: Calendar, format: String? = SEND_DOB_FORMAT): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(calendar.time)
    }

    fun getDateFromMillis(millis: Long?, format: String? = DOB_FORMAT): String {
        return if (millis == null || millis == 0L) "" else SimpleDateFormat(
            format,
            Locale.getDefault()
        ).format(Date(millis))
    }

    fun convertServerTimeStamp(timestamp: String, format: String? = STRING_DATE_FORMAT): String? {
        val timestampFormat = SimpleDateFormat(DEFAULT_SERVER_TIME_FORMAT, Locale.getDefault())
        val date = timestampFormat.parse(timestamp)
        return if (date == null) null else SimpleDateFormat(format, Locale.getDefault()).format(date)
    }

    fun getTimeAgoFromTimeStamp(timestamp: String): String? {
        val timestampFormat = SimpleDateFormat(DEFAULT_SERVER_TIME_FORMAT, Locale.getDefault())
        timestampFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = timestampFormat.parse(timestamp)
        return if (date == null) null else DateUtils.getRelativeTimeSpanString(
            date.time,
            Calendar.getInstance().timeInMillis,
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }
}