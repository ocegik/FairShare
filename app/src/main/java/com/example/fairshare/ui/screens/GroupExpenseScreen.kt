package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fairshare.ui.components.AmountField
import com.example.fairshare.ui.components.CategoryDropdown
import com.example.fairshare.ui.components.EntryTypeSelectorRadio
import com.example.fairshare.ui.components.ExpenseData
import com.example.fairshare.ui.components.ExpensePeopleSelector
import com.example.fairshare.ui.components.NoteField
import com.example.fairshare.ui.components.TitleField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupExpenseScreen(
    navController: NavHostController
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val allPeople = listOf("Tarun", "Mohit", "Pramod", "Pandu", "Ankit")
    var selectedPeople by remember { mutableStateOf(allPeople) }
    var entryType by remember { mutableStateOf("Expense")}

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add Expense",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        TitleField(title) { title = it }
        AmountField(amount) { amount = it }
        CategoryDropdown(selectedCategory = category, onCategorySelected = { category = it })
        EntryTypeSelectorRadio{selected ->
            entryType = selected}
        ExpensePeopleSelector(
            people = allPeople,
            onSelectionChange = { selectedPeople = it }
        )
        NoteField(note) { note = it }

        // Submit Button
        Button(
            onClick = {
                if (title.isNotBlank() && amount.isNotBlank() && category.isNotBlank() && selectedPeople.isNotEmpty()) {
                    val expense = ExpenseData(title = title,
                        amount = amount.toDouble(),
                        category = category,
                        note = note,
                        entryType = entryType)
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Expense", fontSize = 16.sp)
        }
    }
}