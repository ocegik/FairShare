package com.example.fairshare.ui.components

data class ExpenseData(
    val title: String,
    val amount: Double,
    val category: String,
    val note: String,
    val sharedWith: List<String>
)
