package com.example.fairshare.ui.components

data class ExpenseData(
    val id: String = "",
    val title: String,
    val entryType: String = "",
    val userId: String = "",
    val amount: Double,
    val category: String,
    val note: String,
    val dateTime: Long = System.currentTimeMillis(),
    val groupId: String? = null,
    val paidBy: String? = null,
    val participants: List<String>? = null
)
