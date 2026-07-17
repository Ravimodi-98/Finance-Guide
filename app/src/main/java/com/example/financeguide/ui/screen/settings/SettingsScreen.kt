package com.example.financeguide.ui.screen.settings

import android.widget.Toast

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Surface
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


import androidx.compose.material.icons.rounded.Payments

@Composable
fun SettingsScreen(
    settingsManager: com.example.financeguide.data.local.SettingsManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val themeMode by settingsManager.theme.collectAsState()
    val currencyCode by settingsManager.currency.collectAsState()
    val userName by settingsManager.userName.collectAsState()
    val userEmail by settingsManager.userEmail.collectAsState()
    val userAvatar by settingsManager.userAvatar.collectAsState()

    val currentThemeName = when (themeMode) {
        "dark" -> "Dark Mode (Jolly Roger Style)"
        "light" -> "Light Mode (Marine Base Style)"
        else -> "System Default"
    }

    val currentCurrencyName = when (currencyCode) {
        "BELI" -> "Beli (฿)"
        "INR" -> "Rupees (₹)"
        "USD" -> "Dollars ($)"
        "GBP" -> "Pounds (£)"
        else -> currencyCode
    }

    val avatarEmoji = when (userAvatar) {
        "Luffy" -> "🍖"
        "Zoro" -> "⚔️"
        "Nami" -> "🍊"
        "Sanji" -> "🍳"
        "Chopper" -> "🌸"
        else -> "🍖"
    }

    val avatarColor = when (userAvatar) {
        "Luffy" -> Color(0xFFD00000)
        "Zoro" -> Color(0xFF2A9D8F)
        "Nami" -> Color(0xFFFFB703)
        "Sanji" -> Color(0xFF3B82F6)
        "Chopper" -> Color(0xFFEC4899)
        else -> Color(0xFFFFB703)
    }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showVersionDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp)
            )

            // User Profile Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(avatarColor.copy(alpha = 0.15f))
                            .border(BorderStroke(2.dp, avatarColor), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = avatarEmoji, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { showEditProfileDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Profile",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsSection(title = "Preferences")
            
            SettingsSwitchItem(
                icon = Icons.Rounded.Notifications,
                title = "Budget Alerts",
                subtitle = "Get notified when you exceed 80% of budget",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
            
            SettingsClickableItem(
                icon = Icons.Rounded.ColorLens,
                title = "Theme",
                subtitle = currentThemeName,
                onClick = { showThemeDialog = true }
            )

            SettingsClickableItem(
                icon = Icons.Rounded.Payments,
                title = "Bounty Currency",
                subtitle = currentCurrencyName,
                onClick = { showCurrencyDialog = true }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSection(title = "Data")
            
            SettingsClickableItem(
                icon = Icons.Rounded.Download,
                title = "Export Data",
                subtitle = "Export transactions to CSV",
                onClick = { 
                    Toast.makeText(context, "Export not implemented yet", Toast.LENGTH_SHORT).show()
                    showExportDialog = true
                }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSection(title = "About")
            
            SettingsClickableItem(
                icon = Icons.Rounded.Info,
                title = "App Version",
                subtitle = "1.0.0 (MVP)",
                onClick = { showVersionDialog = true }
            )
            
            SettingsClickableItem(
                icon = Icons.Rounded.Info,
                title = "About",
                subtitle = "Learn more about this app",
                onClick = { showAboutDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSection(title = "Account")

            SettingsClickableItem(
                icon = Icons.Rounded.Logout,
                title = "Log Out of Crew",
                subtitle = "Sign out from this pirate ledger",
                onClick = { settingsManager.logout() }
            )
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme") },
            text = {
                Column {
                    TextButton(onClick = { 
                        settingsManager.setTheme("light")
                        showThemeDialog = false
                    }) { Text("Light Mode") }
                    TextButton(onClick = { 
                        settingsManager.setTheme("dark")
                        showThemeDialog = false
                    }) { Text("Dark Mode") }
                    TextButton(onClick = { 
                        settingsManager.setTheme("system")
                        showThemeDialog = false
                    }) { Text("System Default") }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text("Close") }
            }
        )
    }

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select Currency") },
            text = {
                Column {
                    TextButton(onClick = { 
                        settingsManager.setCurrency("BELI")
                        showCurrencyDialog = false
                    }) { Text("Beli (฿) [One Piece]") }
                    TextButton(onClick = { 
                        settingsManager.setCurrency("INR")
                        showCurrencyDialog = false
                    }) { Text("Rupees (₹) [India]") }
                    TextButton(onClick = { 
                        settingsManager.setCurrency("USD")
                        showCurrencyDialog = false
                    }) { Text("Dollars ($) [US]") }
                    TextButton(onClick = { 
                        settingsManager.setCurrency("GBP")
                        showCurrencyDialog = false
                    }) { Text("Pounds (£) [UK]") }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) { Text("Close") }
            }
        )
    }

    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            title = { Text("App Version") },
            text = { Text("FinanceGuide v1.0.0 (MVP)\nAll rights reserved.") },
            confirmButton = {
                TextButton(onClick = { showVersionDialog = false }) { Text("OK") }
            }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Data") },
            text = { Text("Export functionality will be added in a future release.") },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) { Text("Close") }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About FinanceGuide") },
            text = {
                Column {
                    Text(
                        text = "FinanceGuide v1.0.0",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your personal finance companion that helps you track expenses, manage budgets, and gain smart insights into your spending habits.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Key Features:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Track income & expenses by category", style = MaterialTheme.typography.bodySmall)
                    Text("• Set monthly budgets with progress tracking", style = MaterialTheme.typography.bodySmall)
                    Text("• Visual spending insights with donut charts", style = MaterialTheme.typography.bodySmall)
                    Text("• AI-powered smart suggestions", style = MaterialTheme.typography.bodySmall)
                    Text("• Budget alerts when nearing limits", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Built with Jetpack Compose, Material 3, and Room Database. Designed with a premium dark-mode-first experience.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Developed by Ravi Modi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "© 2026 All rights reserved.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("Close") }
            }
        )
    }

    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(userName) }
        var editEmail by remember { mutableStateOf(userEmail) }
        var editAvatar by remember { mutableStateOf(userAvatar) }

        val avatarsList = listOf(
            Pair("Luffy", "🍖"),
            Pair("Zoro", "⚔️"),
            Pair("Nami", "🍊"),
            Pair("Sanji", "🍳"),
            Pair("Chopper", "🌸")
        )

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Crew Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Pirate Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Crew Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Crew Identity",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        avatarsList.forEach { (avatarName, emoji) ->
                            val isSelected = editAvatar == avatarName
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        border = BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.1f)
                                        ),
                                        shape = CircleShape
                                    )
                                    .clickable { editAvatar = avatarName },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editName.isNotBlank() && editEmail.isNotBlank()) {
                            settingsManager.updateProfile(editName, editEmail, editAvatar)
                            showEditProfileDialog = false
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SettingsSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
