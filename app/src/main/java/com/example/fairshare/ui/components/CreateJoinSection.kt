package com.example.fairshare.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fairshare.navigation.Screen

@Composable
fun CreateJoinSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Need a new group?", style = MaterialTheme.typography.bodyMedium)
        TextButton(
            onClick = { navController.navigate(Screen.CreateGroup.route) }
        ) {
            Text("Create")
        }

        Spacer(Modifier.height(12.dp))

        Text("Want to join a group?", style = MaterialTheme.typography.bodyMedium)
        TextButton(
            onClick = { navController.navigate(Screen.JoinGroup.route) }
        ) {
            Text("Join")
        }
    }
}
