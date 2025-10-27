package com.example.fairshare.ui.components

import android.util.Log

data class ExpenseData(
    var id: String = "",
    var title: String = "",
    var amount: Double = 0.0,
    var category: String = "",
    var note: String = "",
    var entryType: String = "",
    var dateTime: Long = 0L,
    var userId: String = "",
    var groupId: String? = null,
    var participants: List<String>? = null,
    var paidBy: String? = null
)
