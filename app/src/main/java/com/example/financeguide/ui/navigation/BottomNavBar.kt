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
    val isBasic = strings.chatbotName == "FinBot"

    val fabGradient = when {
        isSpidey -> listOf(Color(0xFFC1121F), Color(0xFF1D4ED8)) // Web Crimson to Spider Blue
        isBasic  -> listOf(Color(0xFF00E599), Color(0xFF00D2FF)) // Mint Emerald to Cyber Cyan
        else     -> listOf(Color(0xFF1E88E5), Color(0xFFFFC72C)) // Ocean Blue to Straw Hat Gold
    }

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
                        Box(
                            modifier = Modifier
                                .offset(y = (-16).dp)
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(colors = fabGradient)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.title,
                                tint = if (isBasic) Color(0xFF080D1A) else Color.White,
                                modifier = Modifier.size(28.dp)
                            )
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
