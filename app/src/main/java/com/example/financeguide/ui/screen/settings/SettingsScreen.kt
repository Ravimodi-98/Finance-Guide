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
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Close
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Explore
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
import com.example.financeguide.theme.LocalThemeStrings

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
    val geminiApiKey by settingsManager.geminiApiKey.collectAsState()
    val geminiModel by settingsManager.geminiModel.collectAsState()
    val strings = LocalThemeStrings.current
    val clipboardManager = LocalClipboardManager.current

    val currentThemeName = when (themeMode) {
        "onepiece" -> "One Piece Theme"
        "spiderman" -> "Spider-Man Theme"
        "basic" -> "Finance Pro Theme"
        else -> "One Piece Theme"
    }

    val currentCurrencyName = when (currencyCode) {
        "INR"  -> "Rupees (₹)"
        "USD"  -> "US Dollars ($)"
        "EUR"  -> "Euros (€)"
        "GBP"  -> "Pounds (£)"
        "JPY"  -> "Yen (¥)"
        "BELI" -> "Beli (฿)"
        else   -> currencyCode
    }

    val isSpideyTheme = strings.chatbotName == "Spider-Man"
    val isBasicTheme = strings.chatbotName == "FinBot"

    val displayUserName = when {
        userName == "Guest Pirate" && isSpideyTheme -> "Web Slinger Agent"
        userName == "Guest Pirate" && isBasicTheme  -> "Finance Investor"
        userName == "Monkey D. Luffy" && isBasicTheme -> "Finance Pro User"
        else -> userName
    }

    val displayUserEmail = when {
        userEmail == "guest@strawhats.org" && isSpideyTheme -> "agent@shield.org"
        userEmail == "guest@strawhats.org" && isBasicTheme  -> "pro.user@financeguide.ai"
        userEmail == "luffy@strawhats.org" && isBasicTheme  -> "pro.user@financeguide.ai"
        else -> userEmail
    }

    val avatarEmoji = when (userAvatar) {
        "Spidey", "Luffy", "Investor" -> when {
            isSpideyTheme -> "🕷️"
            isBasicTheme  -> "💼"
            else          -> "🍖"
        }
        "Miles", "Zoro", "Trader" -> when {
            isSpideyTheme -> "⚡"
            isBasicTheme  -> "📈"
            else          -> "⚔️"
        }
        "Gwen", "Nami", "Analyst" -> when {
            isSpideyTheme -> "🕸️"
            isBasicTheme  -> "📊"
            else          -> "🍊"
        }
        "Peter", "Sanji", "Planner" -> when {
            isSpideyTheme -> "📷"
            isBasicTheme  -> "🏛️"
            else          -> "🍳"
        }
        "MJ", "Chopper", "Executive" -> when {
            isSpideyTheme -> "💖"
            isBasicTheme  -> "💎"
            else          -> "🌸"
        }
        else -> when {
            isSpideyTheme -> "🕷️"
            isBasicTheme  -> "💼"
            else          -> "🍖"
        }
    }

    val avatarColor = when (userAvatar) {
        "Spidey", "Luffy", "Investor" -> when {
            isSpideyTheme -> Color(0xFFE50914)
            isBasicTheme  -> Color(0xFF00E599)
            else          -> Color(0xFFD00000)
        }
        "Miles", "Zoro", "Trader" -> when {
            isSpideyTheme -> Color(0xFFFF1744)
            isBasicTheme  -> Color(0xFF00D2FF)
            else          -> Color(0xFF2A9D8F)
        }
        "Gwen", "Nami", "Analyst" -> when {
            isSpideyTheme -> Color(0xFFF48FB1)
            isBasicTheme  -> Color(0xFF8B5CF6)
            else          -> Color(0xFFFFB703)
        }
        "Peter", "Sanji", "Planner" -> when {
            isSpideyTheme -> Color(0xFF0056B3)
            isBasicTheme  -> Color(0xFFFFC72C)
            else          -> Color(0xFF3B82F6)
        }
        "MJ", "Chopper", "Executive" -> when {
            isSpideyTheme -> Color(0xFFE91E63)
            isBasicTheme  -> Color(0xFF38BDF8)
            else          -> Color(0xFFEC4899)
        }
        else -> when {
            isSpideyTheme -> Color(0xFFE50914)
            isBasicTheme  -> Color(0xFF00E599)
            else          -> Color(0xFFFFB703)
        }
    }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showVersionDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }

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
                            text = displayUserName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = displayUserEmail,
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
                title = strings.currencyLabel,
                subtitle = currentCurrencyName,
                onClick = { showCurrencyDialog = true }
            )

            SettingsClickableItem(
                icon = Icons.Rounded.Explore,
                title = "Replay App Tour",
                subtitle = "Watch the first-time feature tour again",
                onClick = {
                    settingsManager.setOnboardingCompleted(false)
                    Toast.makeText(context, "Starting App Tour...", Toast.LENGTH_SHORT).show()
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSection(title = "AI Configuration")

            SettingsClickableItem(
                icon = Icons.Rounded.VpnKey,
                title = "Gemini API Key",
                subtitle = if (geminiApiKey.isBlank()) "No key set (Tap to paste)" else "${geminiApiKey.take(8)}...${geminiApiKey.takeLast(6)}",
                onClick = { showApiKeyDialog = true }
            )

            SettingsClickableItem(
                icon = Icons.Rounded.Psychology,
                title = "AI Model",
                subtitle = geminiModel,
                onClick = { showApiKeyDialog = true }
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
                title = if (isSpideyTheme) "Log Out of Account" else "Log Out of Crew",
                subtitle = if (isSpideyTheme) "Sign out from this web ledger" else "Sign out from this pirate ledger",
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
                        settingsManager.setTheme("onepiece")
                        showThemeDialog = false
                    }) { Text("One Piece 🏴‍☠️") }
                    TextButton(onClick = {
                        settingsManager.setTheme("spiderman")
                        showThemeDialog = false
                    }) { Text("Spider-Man 🕷️") }
                    TextButton(onClick = {
                        settingsManager.setTheme("basic")
                        showThemeDialog = false
                    }) { Text("Finance Pro 📊") }
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
                        settingsManager.setCurrency("INR")
                        showCurrencyDialog = false
                    }) { Text("Rupees (₹) - India") }
                    TextButton(onClick = { 
                        settingsManager.setCurrency("USD")
                        showCurrencyDialog = false
                    }) { Text("US Dollars ($) - Global") }
                    TextButton(onClick = { 
                        settingsManager.setCurrency("EUR")
                        showCurrencyDialog = false
                    }) { Text("Euros (€) - Europe") }
                    TextButton(onClick = { 
                        settingsManager.setCurrency("GBP")
                        showCurrencyDialog = false
                    }) { Text("Pounds (£) - UK") }
                    TextButton(onClick = { 
                        settingsManager.setCurrency("JPY")
                        showCurrencyDialog = false
                    }) { Text("Yen (¥) - Japan") }
                    TextButton(onClick = { 
                        settingsManager.setCurrency("BELI")
                        showCurrencyDialog = false
                    }) { Text("Beli (฿) - Anime") }
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
        var editName by remember { mutableStateOf(displayUserName) }
        var editEmail by remember { mutableStateOf(displayUserEmail) }
        var editAvatar by remember { mutableStateOf(userAvatar) }

        val avatarsList = when {
            isSpideyTheme -> listOf(
                Pair("Spidey", "🕷️"),
                Pair("Miles", "⚡"),
                Pair("Gwen", "🕸️"),
                Pair("Peter", "📷"),
                Pair("MJ", "💖")
            )
            isBasicTheme -> listOf(
                Pair("Investor", "💼"),
                Pair("Trader", "📈"),
                Pair("Analyst", "📊"),
                Pair("Planner", "🏛️"),
                Pair("Executive", "💎")
            )
            else -> listOf(
                Pair("Luffy", "🍖"),
                Pair("Zoro", "⚔️"),
                Pair("Nami", "🍊"),
                Pair("Sanji", "🍳"),
                Pair("Chopper", "🌸")
            )
        }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text(when { isSpideyTheme -> "Edit Hero Profile"; isBasicTheme -> "Edit Profile"; else -> "Edit Crew Profile" }) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(when { isSpideyTheme -> "Hero Name"; isBasicTheme -> "Full Name"; else -> "Pirate Name" }) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text(when { isSpideyTheme -> "Hero Email"; isBasicTheme -> "Email Address"; else -> "Crew Email" }) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = when { isSpideyTheme -> "Hero Identity"; isBasicTheme -> "Profile Avatar"; else -> "Crew Identity" },
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

    if (showApiKeyDialog) {
        var tempKey by remember { mutableStateOf(geminiApiKey) }
        var tempModel by remember { mutableStateOf(geminiModel) }

        val commonModels = listOf(
            "minimaxai/minimax-m3.1-flash",
            "gemini-3.8-flash",
            "gemini-3.5-flash",
            "deepseek-ai/deepseek-v4.1-flash",
            "meta/llama-3.1-8b-instruct"
        )

        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.VpnKey,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "AI Model & API Key",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Paste your Gemini API key below. The Finance AI only operates when an API key is provided.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = tempKey,
                        onValueChange = { tempKey = it },
                        label = { Text("Gemini API Key") },
                        placeholder = { Text("Paste key here...") },
                        singleLine = false,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (tempKey.isNotEmpty()) {
                                IconButton(onClick = { tempKey = "" }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Clear")
                                }
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                val clip = clipboardManager.getText()?.text
                                if (!clip.isNullOrBlank()) {
                                    tempKey = clip.trim()
                                    Toast.makeText(context, "Pasted from clipboard", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(Icons.Rounded.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Paste from Clipboard")
                        }
                    }

                    Text(
                        text = "Model Preset",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(commonModels) { modelName ->
                            val isSelected = tempModel == modelName
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier.clickable {
                                    tempModel = modelName
                                }
                            ) {
                                Text(
                                    text = modelName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = tempModel,
                        onValueChange = { tempModel = it },
                        label = { Text("Selected Model") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Tip: You can create a free key at aistudio.google.com",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        settingsManager.setGeminiApiKey(tempKey)
                        settingsManager.setGeminiModel(tempModel)
                        showApiKeyDialog = false
                        Toast.makeText(context, "API Key & Model updated!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Save & Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
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
