package com.example.financeguide.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.financeguide.theme.LocalThemeStrings
import com.example.financeguide.theme.Primary

@Composable
fun BottomNavBar(
    currentDestination: TopLevelDestination,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalThemeStrings.current
    val isSpidey = strings.chatbotName == "Spider-Man"

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        TopLevelDestination.entries.forEach { destination ->
            if (destination == TopLevelDestination.ADD_TRANSACTION) {
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToDestination(destination) },
                    icon = {
                        if (isSpidey) {
                            Box(
                                modifier = Modifier
                                    .offset(y = (-16).dp)
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFC1121F),
                                                Color(0xFF1D4ED8)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title,
                                    tint = Color.White
                                )
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .offset(y = (-16).dp)
                                    .size(56.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                shadowElevation = 4.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(destination.icon, contentDescription = destination.title)
                                }
                            }
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
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
