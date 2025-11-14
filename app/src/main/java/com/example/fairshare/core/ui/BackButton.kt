package com.example.fairshare.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
    iconSize: Int = 36
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape)
            .then(Modifier.size((iconSize + 24).dp)) // button size slightly bigger than icon
    ) {
        Surface(
            color = backgroundColor,
            shape = CircleShape,
            tonalElevation = 2.dp
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = tint,
                modifier = Modifier
                    .size(iconSize.dp)
                    .padding(6.dp)
            )
        }
    }
}
