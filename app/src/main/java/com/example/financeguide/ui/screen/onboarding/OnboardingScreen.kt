package com.example.financeguide.ui.screen.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeguide.theme.*

@Composable
fun OnboardingScreen(
    onComplete: (currency: String, theme: String) -> Unit
) {
    var selectedCurrency by remember { mutableStateOf("BELI") }
    var selectedTheme by remember { mutableStateOf("system") }

    val currencies = listOf(
        CurrencyOption("BELI", "฿", "Beli (One Piece)"),
        CurrencyOption("INR", "₹", "Rupees (India)"),
        CurrencyOption("USD", "$", "Dollars (US)"),
        CurrencyOption("GBP", "£", "Pounds (UK)")
    )

    val themes = listOf(
        ThemeOption("system", "Default", "Marine System Default"),
        ThemeOption("dark", "Dark Mode", "Jolly Roger Style"),
        ThemeOption("light", "Light Mode", "Marine Base Style")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0C0E14),
                        Color(0xFF161B26),
                        Color(0xFF0C0E14)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.DirectionsBoat,
                contentDescription = "Ship",
                tint = Secondary,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Set Sail with FinanceGuide!",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Choose your bounty currency and theme style before embarking on your financial journey.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Currency Selection
            Text(
                text = "Select Currency",
                style = MaterialTheme.typography.titleMedium,
                color = Secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currencies.forEach { option ->
                    val isSelected = selectedCurrency == option.code
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Primary.copy(alpha = 0.2f) else SurfaceDarkVariant)
                            .border(
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Secondary else Color.White.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedCurrency = option.code }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = option.symbol,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Secondary else Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = option.code,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Theme Selection
            Text(
                text = "Select Interface Theme",
                style = MaterialTheme.typography.titleMedium,
                color = Secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                themes.forEach { option ->
                    val isSelected = selectedTheme == option.key
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Primary.copy(alpha = 0.2f) else SurfaceDarkVariant)
                            .border(
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Secondary else Color.White.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedTheme = option.key }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedTheme = option.key },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Secondary,
                                unselectedColor = Color.White.copy(alpha = 0.4f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = option.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = option.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Set Sail Button
            Button(
                onClick = { onComplete(selectedCurrency, selectedTheme) },
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "SET SAIL! ⛵",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSecondary
                )
            }
        }
    }
}

data class CurrencyOption(val code: String, val symbol: String, val name: String)
data class ThemeOption(val key: String, val name: String, val description: String)
