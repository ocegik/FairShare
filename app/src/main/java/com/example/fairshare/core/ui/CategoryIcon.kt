package com.example.fairshare.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        // Income categories
        "Salary" -> Icons.Default.AccountBalance
        "Business" -> Icons.Default.Business
        "Investment" -> Icons.Default.ArrowUpward
        "Freelance" -> Icons.Default.Work
        "Gift" -> Icons.Default.CardGiftcard
        "Rental Income" -> Icons.Default.Home
        "Bonus" -> Icons.Default.Star

        // Expense categories
        "Food & Dining" -> Icons.Default.Restaurant
        "Transport" -> Icons.Default.DirectionsCar
        "Shopping" -> Icons.Default.ShoppingCart
        "Entertainment" -> Icons.Default.Movie
        "Bills & Utilities" -> Icons.Default.Receipt
        "Health & Fitness" -> Icons.Default.FavoriteBorder
        "Education" -> Icons.Default.School
        "Travel" -> Icons.Default.Flight
        "Groceries" -> Icons.Default.ShoppingBasket

        else -> Icons.Default.Category
    }
}