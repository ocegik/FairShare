package com.example.fairshare.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExpensePeopleSelector(
    people: List<String>,
    onSelectionChange: (List<String>) -> Unit
) {
    // Track who is selected
    val selectedPeople = remember { mutableStateMapOf<String, Boolean>() }
    people.forEach { if (it !in selectedPeople) selectedPeople[it] = true } // default: all selected

    // Determine parent checkbox state
    val parentState = when {
        selectedPeople.values.all { it } -> ToggleableState.On
        selectedPeople.values.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    val onParentClick = {
        val newValue = parentState != ToggleableState.On
        people.forEach { selectedPeople[it] = newValue }
        onSelectionChange(selectedPeople.filterValues { it }.keys.toList())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Parent row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .triStateToggleable(
                    state = parentState,
                    onClick = onParentClick,
                    role = Role.Checkbox
                )
                .padding(vertical = 4.dp)
        ) {
            TriStateCheckbox(state = parentState, onClick = null)
            Text(
                "Select All Participants",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Individual people
        Column(Modifier.padding(start = 36.dp)) {
            people.forEach { name ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = selectedPeople[name] == true,
                            onValueChange = {
                                selectedPeople[name] = it
                                onSelectionChange(selectedPeople.filterValues { it }.keys.toList())
                            },
                            role = Role.Checkbox
                        )
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = selectedPeople[name] == true,
                        onCheckedChange = null
                    )
                    Text(name, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
