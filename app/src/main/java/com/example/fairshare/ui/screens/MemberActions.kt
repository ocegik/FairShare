package com.example.fairshare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fairshare.ui.components.ActionButton
import com.example.fairshare.viewmodel.GroupViewModel

@Composable
fun MemberActions(
    groupId: String,
    currentUserId: String,
    groupViewModel: GroupViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        ActionButton("Leave Group", color = Color.Red) {
            groupViewModel.leaveGroup(groupId, currentUserId)
        }
    }
}

