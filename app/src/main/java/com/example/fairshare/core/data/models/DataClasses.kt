package com.example.fairshare.core.data.models

import androidx.compose.ui.graphics.vector.ImageVector

data class User(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
    val username: String,
    val groups: List<String> = emptyList(),
    val bookMarkedGroup: String? = null
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

data class UserStats(
    val receivables: Double = 0.0,
    val debt: Double = 0.0,
    val income: Double = 0.0,
    val expense: Double = 0.0
)

data class Group(
    var groupId: String = "",
    val name: String = "",
    val owner: String = "",
    val password: String = "",
    val members: List<String> = emptyList(),
    var createdAt: Long = System.currentTimeMillis(),
)

data class GroupMember(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
    val isOwner: Boolean
)

data class GroupUiData(
    val group: Group,
    val members: List<GroupMember>
)

data class DebtSummary(
    val fromUserId: String,
    val toUserId: String,
    val totalAmount: Double
)

data class FabMenuItem(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

data class SettingsCategory(
    val title: String,
    val description: String? = null,
    val onClick: () -> Unit
)

