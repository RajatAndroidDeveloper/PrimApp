package com.primapp.constants

import com.primapp.R
import com.primapp.model.auth.ReferenceItems
import com.primapp.utils.toSentenceCase

object TodoTasksPriorityType {
    const val HIGH = "HIGH"
    const val NORMAL = "NORMAL"
    const val LOW = "LOW"

    fun getPriorityList(): ArrayList<ReferenceItems> {
        val list = arrayListOf<ReferenceItems>()
        list.add(ReferenceItems(1, HIGH, HIGH.toSentenceCase()))
        list.add(ReferenceItems(2, NORMAL, NORMAL.toSentenceCase()))
        list.add(ReferenceItems(3, LOW, LOW.toSentenceCase()))
        return list
    }

    fun getPriorityColor(priorityText: String?): Int {
        return when (priorityText) {
            HIGH -> {
                R.color.priority_high
            }
            NORMAL -> {
                R.color.priority_normal
            }
            LOW -> {
                R.color.priority_low
            }
            else -> {
                R.color.grey
            }
        }
    }
}