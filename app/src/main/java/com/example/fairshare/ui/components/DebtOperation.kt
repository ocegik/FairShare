package com.example.fairshare.ui.components

enum class DebtOperation {
    DEBT_ADDED,      // When a new debt is created
    DEBT_SETTLED,    // When a debt is marked as settled
    DEBT_CANCELLED   // When a debt is cancelled/deleted
}