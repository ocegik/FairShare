package com.example.fairshare.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fairshare.data.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelector(
    groups: List<Group>,
    onGroupSelected: (String) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    var sheetState = rememberModalBottomSheetState()

    // Current selection persists at this level
    var selectedGroupName by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        OutlinedButton(
            onClick = { showSheet = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedGroupName ?: "Select Group",
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select group"
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showSheet = false }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Choose Group",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(16.dp)
                )

                groups.forEach { group ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedGroupName = group.name
                                onGroupSelected(group.groupId)
                                showSheet = false
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
