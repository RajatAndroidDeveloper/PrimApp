package com.primapp.constants

import android.content.Context
import com.primapp.R

object ReportReasonTypes {
    const val RUDE_LANGUAGE = 1
    const val SEXUAL_CONTENT = 2
    const val HARASSMENT = 3
    const val THREAT_OR_VIOLENT = 4
    const val OTHERS = 5

    fun getReportReasonMessage(context: Context, reportType:Int): String?{
       return when (reportType) {
            RUDE_LANGUAGE -> {
                context.getString(R.string.report_reason_rude_language)
            }

            SEXUAL_CONTENT -> {
                context.getString(R.string.report_reason_sexual)
            }
            HARASSMENT -> {
                context.getString(R.string.report_reason_harassment)
            }
            THREAT_OR_VIOLENT -> {
                context.getString(R.string.report_reason_threatening)
            }

            else -> {
                null
            }
        }
    }
}