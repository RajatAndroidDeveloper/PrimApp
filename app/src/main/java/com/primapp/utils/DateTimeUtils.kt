package com.primapp.utils

import android.text.format.DateUtils
import com.primapp.model.portfolio.ExperienceData
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


object DateTimeUtils {

    const val SEND_DOB_FORMAT = "yyyy/MM/dd"
    const val DOB_FORMAT = "MMM dd, yyyy"
    const val LAST_SEEN_DATE_FORMAT = "dd MMMM yyyy hh:mm a"
    const val TIME_FORMAT = "hh:mm a"
    const val EXPERIENCE_DOB_FORMAT = "MM/dd/yyyy"

    const val DEFAULT_SERVER_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    const val STRING_DATE_FORMAT = "dd MMM yyyy" //02 Jun 2022
    const val STRING_DATE_FORMAT_TIME = "MMM dd, yyyy | hh:mm a" //02 Jun 2022
    const val CREATE_TODO_DATE_FORMAT = "yyyy/MM/dd | hh:mm a"
    const val CREATED_AT_DATE_FORMAT = "MMM dd, yyyy 'at' h:mm a"

    fun getDateFromPicker(calendar: Calendar, format: String? = SEND_DOB_FORMAT): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(calendar.time)
    }

    fun getDateFromMillisValue(millis: Long?, format: String? = SEND_DOB_FORMAT): String {
        return if (millis == null || millis == 0L) "" else SimpleDateFormat(
            format,
            Locale.getDefault()
        ).format(Date(millis))
    }

    fun getDateFromMillis(millis: Long?, format: String? = DOB_FORMAT): String {
        return if (millis == null || millis == 0L) "" else SimpleDateFormat(
            format,
            Locale.getDefault()
        ).format(Date(millis))
    }

    fun getDateAndTimeFromMillis(millis: Long?, format: String? = STRING_DATE_FORMAT_TIME): String {
        return if (millis == null || millis == 0L) "" else SimpleDateFormat(
            format,
            Locale.getDefault()
        ).format(Date(millis))
    }

    fun convertServerTimeStamp(timestamp: String?, format: String? = DOB_FORMAT): String? {
        if (timestamp.isNullOrEmpty()) return null
        val timestampFormat = SimpleDateFormat(DEFAULT_SERVER_TIME_FORMAT, Locale.getDefault())
        timestampFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = timestampFormat.parse(timestamp)
        val timestampFormat1 = SimpleDateFormat(format, Locale.getDefault())
        timestampFormat1.timeZone = TimeZone.getDefault()
        return if (date == null) null else timestampFormat1.format(date)
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

    fun getDateFromTimeStamp(timestamp: String): Date? {
        val timestampFormat = SimpleDateFormat(DEFAULT_SERVER_TIME_FORMAT, Locale.getDefault())
        timestampFormat.timeZone = TimeZone.getTimeZone("UTC")
        return timestampFormat.parse(timestamp)
    }

    fun getDayAgoFromTimeStamp(timestamp: String): String? {
        val timestampFormat = SimpleDateFormat(DEFAULT_SERVER_TIME_FORMAT, Locale.getDefault())
        timestampFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = timestampFormat.parse(timestamp)
        return if (date == null) null else DateUtils.getRelativeTimeSpanString(
            date.time,
            Calendar.getInstance().timeInMillis,
            DateUtils.DAY_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        ).toString()
    }

    /**
     * If the given time is of a different date, display the date.
     * If it is of the same date, display the time.
     * @param timeInMillis  The time to convert, in milliseconds.
     * @return  The time or date.
     */
    fun formatDateTime(timeInMillis: Long): String? {
        return if (isToday(timeInMillis)) {
            formatTime(timeInMillis)
        } else {
            formatDate(timeInMillis, STRING_DATE_FORMAT)
        }
    }

    fun formatTime(timeInMillis: Long): String? {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    fun formatDate(timeInMillis: Long, format: String? = "MMMM dd"): String? {
        val dateFormat =
            SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    fun isToday(timeInMillis: Long): Boolean {
        val dateFormat =
            SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(timeInMillis)
        return date == dateFormat.format(System.currentTimeMillis())
    }

    /**
     * Checks if two dates are of the same day.
     * @param millisFirst   The time in milliseconds of the first date.
     * @param millisSecond  The time in milliseconds of the second date.
     * @return  Whether {@param millisFirst} and {@param millisSecond} are off the same day.
     */
    fun hasSameDate(millisFirst: Long, millisSecond: Long): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        return dateFormat.format(millisFirst) == dateFormat.format(millisSecond)
    }
}