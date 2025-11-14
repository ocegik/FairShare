package com.example.fairshare.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fairshare.core.data.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelector(
    groups: List<Group>,
    selectedGroupId: String?,
    onGroupSelected: (String) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Current selection persists at this level
    var selectedGroupName by remember {
        mutableStateOf(
            groups.find { it.groupId == selectedGroupId }?.name
        )
    }

    LaunchedEffect(selectedGroupId, groups) {
        selectedGroupName = groups.find { it.groupId == selectedGroupId }?.name
    }

    OutlinedButton(
        onClick = { showSheet = true },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = selectedGroupName ?: "Select Group",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showSheet = false },
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = "Choose Group",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(20.dp)
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
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
