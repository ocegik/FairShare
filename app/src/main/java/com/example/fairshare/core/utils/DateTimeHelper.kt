package com.example.fairshare.core.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

fun formatDateTime(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
