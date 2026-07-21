package com.example.financeguide.ui.screen.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeguide.theme.*
import com.example.financeguide.ui.components.ThemeAppLogo

data class TourStep(
    val icon: ImageVector,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val highlights: List<Pair<String, String>>
)

@Composable
fun OnboardingScreen(
    onComplete: (currency: String, theme: String) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var selectedTheme by remember { mutableStateOf("basic") }

    val tourSteps = listOf(
        TourStep(
            icon = Icons.Rounded.AccountBalanceWallet,
            emoji = "🧭",
            title = "Welcome to Finance Guide",
            subtitle = "Your ultimate personal finance companion. Easily track income, analyze spending, and build wealth.",
            highlights = listOf(
                "📊 Real-Time Balance & Cash Flow" to "Track every dollar in & out effortlessly.",
                "🎯 Smart Category Budgets" to "Set monthly limits and get alert notifications.",
                "🤖 AI Smart Assistant" to "Ask any question & log expenses conversationally."
            )
        ),
        TourStep(
            icon = Icons.Rounded.TrendingUp,
            emoji = "💰",
            title = "Track Balance & Cash Flow",
            subtitle = "Log transactions in seconds and view dynamic visual charts and category statistics.",
            highlights = listOf(
                "🟢 Income & Expense Breakdown" to "Detailed logs for income and expenses.",
                "📈 Interactive Analytics" to "Visual donut charts and monthly trends.",
                "🏷️ Smart Auto-Tagging" to "Categorize into Food, Bills, Shopping & more."
            )
        ),
        TourStep(
            icon = Icons.Rounded.PieChart,
            emoji = "🎯",
            title = "Smart Monthly Budgets",
            subtitle = "Never overspend again. Set monthly category limits and stay on top of your financial goals.",
            highlights = listOf(
                "⚠️ Automated Over-Budget Alerts" to "Get instant notifications when approaching limits.",
                "📊 Category Progress Indicators" to "Visual progress bars showing budget health.",
                "💡 AI Savings Recommendations" to "Personalized advice to optimize your savings."
            )
        ),
        TourStep(
            icon = Icons.Rounded.SmartToy,
            emoji = "🤖",
            title = "AI Smart Assistant",
            subtitle = "Ask ANY question — coding, tech, science, general advice, or personal finance with complete freedom!",
            highlights = listOf(
                "💬 Unlimited Freedom" to "Ask any question on any topic without restrictions.",
                "🎙️ Conversational Commands" to "'I spent $20 on coffee' logs automatically.",
                "⚡ Hands-Free Logging" to "Direct database integration for effortless tracking."
            )
        )
    )

    val currencies = listOf(
        CurrencyOption("USD", "$", "Dollars (US / Global)"),
        CurrencyOption("INR", "₹", "Rupees (India)"),
        CurrencyOption("EUR", "€", "Euros (Europe)"),
        CurrencyOption("GBP", "£", "Pounds (UK)"),
        CurrencyOption("JPY", "¥", "Yen (Japan)"),
        CurrencyOption("BELI", "฿", "Beli (Anime)")
    )

    val themes = listOf(
        ThemeOption("onepiece", "One Piece", "Jolly Roger · Pirate Crew Style 🏴‍☠️"),
        ThemeOption("spiderman", "Spider-Man", "Friendly Neighborhood · Web Slinger Style 🕷️"),
        ThemeOption("basic", "Finance Pro", "Clean · Modern · Professional Dark UI 📊")
    )

    val activeAccentColor = when (selectedTheme) {
        "spiderman" -> SpideyPrimary
        "basic" -> Color(0xFF6366F1)
        else -> Secondary
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF080D1A),
                        Color(0xFF131B2E),
                        Color(0xFF080D1A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Step Counter & Skip Tour Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STEP ${currentStep + 1} OF 5",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = activeAccentColor,
                    letterSpacing = 1.5.sp
                )

                if (currentStep < 4) {
                    TextButton(
                        onClick = { currentStep = 4 }
                    ) {
                        Text(
                            text = "Skip Tour ⏭️",
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Content Area with Animated Transitions
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                modifier = Modifier.weight(1f),
                label = "GuidedTourAnimation"
            ) { step ->
                if (step < 4) {
                    val info = tourSteps[step]
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Icon / Logo Header
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(activeAccentColor.copy(alpha = 0.15f))
                                .border(2.dp, activeAccentColor.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = info.icon,
                                contentDescription = info.title,
                                tint = activeAccentColor,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "${info.title} ${info.emoji}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = info.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // Highlight Cards
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            info.highlights.forEach { (title, desc) ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = SurfaceDarkVariant.copy(alpha = 0.6f)
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = desc,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Final Step: Preferences (Currency & Theme selection)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ThemeAppLogo(
                            themeMode = selectedTheme,
                            modifier = Modifier.size(72.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Customize Your App ⚙️",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Select your preferred currency and app theme to complete setup.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Currency Selector
                        Text(
                            text = "Select Currency",
                            style = MaterialTheme.typography.titleSmall,
                            color = activeAccentColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            currencies.forEach { option ->
                                val isSelected = selectedCurrency == option.code
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) activeAccentColor.copy(alpha = 0.2f) else SurfaceDarkVariant)
                                        .border(
                                            border = BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) activeAccentColor else Color.White.copy(alpha = 0.1f)
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedCurrency = option.code }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = option.symbol,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) activeAccentColor else Color.White
                                        )
                                        Text(
                                            text = option.code,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Theme Selector
                        Text(
                            text = "Select Interface Theme",
                            style = MaterialTheme.typography.titleSmall,
                            color = activeAccentColor,
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
                                        .background(if (isSelected) activeAccentColor.copy(alpha = 0.2f) else SurfaceDarkVariant)
                                        .border(
                                            border = BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) activeAccentColor else Color.White.copy(alpha = 0.1f)
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedTheme = option.key }
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedTheme = option.key },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = activeAccentColor,
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
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    val isCurrent = index == currentStep
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isCurrent) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrent) activeAccentColor else Color.White.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Navigation Buttons
            if (currentStep < 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(80.dp))
                    }

                    Button(
                        onClick = { currentStep++ },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = activeAccentColor)
                    ) {
                        Text(
                            text = "Next",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = "Next")
                    }
                }
            } else {
                Button(
                    onClick = { onComplete(selectedCurrency, selectedTheme) },
                    colors = ButtonDefaults.buttonColors(containerColor = activeAccentColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "FINISH & START EXPLORING 🚀",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

data class CurrencyOption(val code: String, val symbol: String, val name: String)
data class ThemeOption(val key: String, val name: String, val description: String)
