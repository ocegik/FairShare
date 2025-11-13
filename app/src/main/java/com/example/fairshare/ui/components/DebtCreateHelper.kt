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
): Boolean {
    Log.d("DebtCreation", "=== Creating debts for expense: $expenseId ===")
    Log.d("DebtCreation", "Paid by: $paidBy")
    Log.d("DebtCreation", "Participants: $participants")
    Log.d("DebtCreation", "Total amount: $amount")

    // Calculate share per person
    val numberOfPeople = participants.size
    val sharePerPerson = amount / numberOfPeople

    Log.d("DebtCreation", "Number of people: $numberOfPeople")
    Log.d("DebtCreation", "Share per person: $sharePerPerson")

    // Track success of all debt creations
    var allSuccessful = true

    // Create debt for each participant (except the person who paid)
    val debtsToCreate = participants.filter { it != paidBy }

    Log.d("DebtCreation", "Creating ${debtsToCreate.size} debts")

    // Use a CompletableDeferred to wait for all debts to be created
    val results = debtsToCreate.map { participantId ->
        CompletableDeferred<Boolean>().apply {
            val debt = DebtData(
                id = "", // Repository will generate ID
                expenseId = expenseId,
                fromUserId = participantId, // Person who owes
                toUserId = paidBy, // Person who paid
                amount = sharePerPerson,
                status = "pending",
                groupId = groupId,
                createdAt = System.currentTimeMillis(),
                settledAt = null
            )

            Log.d("DebtCreation", "Creating debt: $participantId owes $paidBy $$sharePerPerson")

            // Add debt through ViewModel
            debtViewModel.addDebt(debt) { success ->
                if (success) {
                    Log.d("DebtCreation", "✓ Debt created successfully")
                } else {
                    Log.e("DebtCreation", "✗ Failed to create debt")
                    allSuccessful = false
                }
                complete(success)
            }
        }
    }

    // Wait for all debts to be created
    results.forEach { it.await() }

    Log.d("DebtCreation", "=== Debt creation process completed (success: $allSuccessful) ===")
    return allSuccessful
}