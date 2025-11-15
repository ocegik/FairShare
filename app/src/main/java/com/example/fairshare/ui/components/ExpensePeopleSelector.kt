package com.example.fairshare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fairshare.R
import com.example.fairshare.core.data.models.GroupMember

@Composable
fun ExpensePeopleSelector(
    people: List<GroupMember>,
    selectedIds: List<String>,
    onSelectionChange: (List<String>) -> Unit
) {
    val selectedMap = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(people) {
        people.forEach { member ->
            if (member.uid !in selectedMap) {
                selectedMap[member.uid] = selectedIds.contains(member.uid)
            }
        }
    }

    val parentState = when {
        selectedMap.values.all { it } -> ToggleableState.On
        selectedMap.values.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    val toggleAll = {
        val newState = parentState != ToggleableState.On
        selectedMap.keys.forEach { selectedMap[it] = newState }
        onSelectionChange(selectedMap.filterValues { it }.keys.toList())
    }

    Column(Modifier.fillMaxWidth().padding(8.dp)) {

        // Parent (Select All)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .triStateToggleable(state = parentState, onClick = toggleAll)
        ) {
            TriStateCheckbox(state = parentState, onClick = null)
            Text(
                text = stringResource(R.string.select_all_participants),
                fontWeight = FontWeight.SemiBold
            )
        }

        // Child rows: show names instead of IDs
        Column(
            Modifier.padding(start = 36.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            people.forEach { member ->
                val checked = selectedMap[member.uid] == true

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = checked,
                            onValueChange = { new ->
                                selectedMap[member.uid] = new
                                onSelectionChange(selectedMap.filterValues { it }.keys.toList())
                            }
                        )
                ) {
                    Checkbox(checked = checked, onCheckedChange = null)
                    Text(member.displayName ?: member.uid)
                }
            }
        }

    }
}
