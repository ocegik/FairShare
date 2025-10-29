package com.example.fairshare.data.models

data class User(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)

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

data class DebtData(
    var id: String = "",
    var expenseId: String = "", // Link back to the group expense
    var fromUserId: String = "", // Person who owes
    var toUserId: String = "", // Person who gets paid
    var amount: Double = 0.0,
    var status: String = "pending", // "pending", "settled", "cancelled"
    var groupId: String? = null,
    var createdAt: Long = System.currentTimeMillis(),
    var settledAt: Long? = null
)