package com.example.financeguide.ui.navigation

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financeguide.theme.Primary

@Composable
fun BottomNavBar(
    currentDestination: TopLevelDestination,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        TopLevelDestination.entries.forEach { destination ->
            if (destination == TopLevelDestination.ADD_TRANSACTION) {
                // We show this as a FAB overlapping the navigation bar in the scaffold
                // but we add an empty item here to keep spacing correct
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToDestination(destination) },
                    icon = {
                        FloatingActionButton(
                            onClick = { onNavigateToDestination(destination) },
                            shape = CircleShape,
                            containerColor = Primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 4.dp
                            ),
                            modifier = Modifier.offset(y = (-16).dp)
                        ) {
                            Icon(destination.icon, contentDescription = destination.title)
                        }
                    },
                    label = { Text("") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.surface
                    )
                )
            } else {
                NavigationBarItem(
                    selected = currentDestination == destination,
                    onClick = { onNavigateToDestination(destination) },
                    icon = { Icon(destination.icon, contentDescription = destination.title) },
                    label = { Text(destination.title) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Primary,
                        selectedTextColor = Primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
