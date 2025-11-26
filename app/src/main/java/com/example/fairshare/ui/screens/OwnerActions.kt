package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fairshare.ui.components.ActionButton
import com.example.fairshare.viewmodel.GroupViewModel

@Composable
fun OwnerActions(
    groupId: String,
    currentUserId: String,
    groupViewModel: GroupViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        ActionButton("Rename Group") { /* TODO */ }

        ActionButton("Change Password") { /* TODO */ }

        ActionButton("Transfer Ownership") { /* TODO */ }

        ActionButton("Remove Members") { /* TODO */ }

        ActionButton(
            text = "Delete Group",
            color = Color.Red
        ) {
            groupViewModel.deleteGroup(groupId)
        }
    }
}

