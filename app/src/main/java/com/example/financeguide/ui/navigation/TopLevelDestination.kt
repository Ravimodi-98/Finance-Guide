package com.example.financeguide.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    DASHBOARD("Home", Icons.Rounded.Home, "dashboard"),
    GROUPS("Groups", Icons.Rounded.People, "groups"),
    BUDGETS("Budgets", Icons.Rounded.PieChart, "budgets"),
    ADD_TRANSACTION("Add", Icons.Rounded.Add, "add_transaction"),
    INSIGHTS("Insights", Icons.Rounded.BarChart, "insights"),
    SETTINGS("Settings", Icons.Rounded.Settings, "settings")
}
