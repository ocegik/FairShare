package com.example.fairshare.ui.components

import android.util.Log
import com.example.fairshare.data.models.DebtData
import com.example.fairshare.data.models.ExpenseData
import com.example.fairshare.viewmodel.DebtViewModel
import kotlinx.coroutines.CompletableDeferred
import java.util.UUID

// Fixed version with proper async handling
suspend fun createDebtsForGroupExpense(
    expenseId: String,
    amount: Double,
    paidBy: String,
    participants: List<String>,
    groupId: String,
    debtViewModel: DebtViewModel
): List<DebtData> {

    val numberOfPeople = participants.size
    val sharePerPerson = amount / numberOfPeople

    val debtsToCreate = participants.filter { it != paidBy }
    val createdDebts = mutableListOf<DebtData>()

    val results = debtsToCreate.map { participantId ->
        CompletableDeferred<Boolean>().apply {

            val debt = DebtData(
                id = "",
                expenseId = expenseId,
                fromUserId = participantId,
                toUserId = paidBy,
                amount = sharePerPerson,
                status = "pending",
                groupId = groupId,
                createdAt = System.currentTimeMillis(),
                settledAt = null
            )

            debtViewModel.addDebt(debt) { success ->
                if (success) {
                    createdDebts.add(debt)
                }
                complete(success)
            }
        }
    }

    results.forEach { it.await() }

    return createdDebts
}
