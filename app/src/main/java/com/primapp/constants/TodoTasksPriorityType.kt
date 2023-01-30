package com.primapp.constants

import com.primapp.R
import com.primapp.model.auth.ReferenceItems

object TodoTasksPriorityType {
    const val HIGH = "HIGH"
    const val NORMAL = "NORMAL"
    const val LOW = "LOW"

    fun getPriorityList(): ArrayList<ReferenceItems> {
        val list = arrayListOf<ReferenceItems>()
        list.add(ReferenceItems(1, HIGH, HIGH))
        list.add(ReferenceItems(2, NORMAL, NORMAL))
        list.add(ReferenceItems(3, LOW, LOW))
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