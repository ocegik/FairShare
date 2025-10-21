package com.example.fairshare.ui.components

import java.util.Calendar

fun mergeDateAndTime(dateMillis: Long?, hour: Int?, minute: Int?): Long {
    return if (dateMillis != null && hour != null && minute != null) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.timeInMillis
    } else if (dateMillis != null) {
        // If only date is selected, use current time
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.timeInMillis
    } else {
        // Fallback to current date and time
        System.currentTimeMillis()
    }
}