package com.example.fairshare.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.navigation.NavHostController
import com.example.fairshare.core.data.models.FabMenuItem
import com.example.fairshare.navigation.Screen

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FabMenu(
    modifier: Modifier = Modifier,
    items: List<FabMenuItem>,
    listState: LazyListState? = null, // optional
) {
    val fabVisible by remember {
        derivedStateOf {
            listState?.firstVisibleItemIndex == 0 || listState == null
        }
    }

    var expanded by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = expanded) { expanded = false }

    Box(modifier = modifier) {

        FloatingActionButtonMenu(
            modifier = Modifier.align(Alignment.BottomEnd),
            expanded = expanded,
            button = {
                ToggleFloatingActionButton(
                    modifier = Modifier
                        .animateFloatingActionButton(
                            visible = fabVisible || expanded,
                            alignment = Alignment.BottomEnd
                        ),
                    checked = expanded,
                    onCheckedChange = { expanded = !expanded }
                ) {
                    val icon by remember {
                        derivedStateOf {
                            if (checkedProgress > 0.5f) Icons.Default.Close else Icons.Default.Add
                        }
                    }
                    Icon(
                        painter = rememberVectorPainter(icon),
                        contentDescription = null
                    )
                }
            }
        ) {
            items.forEachIndexed { index, menuItem ->

                FloatingActionButtonMenuItem(
                    modifier = Modifier.semantics {
                        isTraversalGroup = true
                        if (index == items.size - 1) {
                            customActions = listOf(
                                CustomAccessibilityAction(
                                    label = "Close menu",
                                    action = {
                                        expanded = false
                                        true
                                    }
                                )
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        menuItem.onClick()
                    },
                    icon = { Icon(menuItem.icon, contentDescription = null) },
                    text = { Text(menuItem.label) }
                )
            }
        }
    }
}
