package com.example.financeguide.ui.screen.insights

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeguide.domain.model.Category
import com.example.financeguide.domain.model.TransactionType
import com.example.financeguide.theme.LocalThemeStrings
import com.example.financeguide.ui.FinanceViewModel
import com.example.financeguide.ui.components.ChartData
import com.example.financeguide.ui.components.DonutChart
import com.example.financeguide.ui.components.LocalCurrency
import com.example.financeguide.ui.components.SuggestionCard
import com.example.financeguide.ui.components.animateEnter
import com.example.financeguide.ui.components.formatAmountString

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InsightsScreen(
    viewModel: FinanceViewModel,
    onNavigateToChatbot: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val strings = LocalThemeStrings.current
    val isSpidey = strings.chatbotName == "Spider-Man"
    val isBasic  = strings.chatbotName == "FinBot"
    var selectedBreakdownType by remember { mutableStateOf(TransactionType.INCOME) }
    var animIndex = 0

    val accentGold = Color(0xFFF6C445)
    val accentCrimson = Color(0xFFC62828)
    val accentYellowGreen = Color(0xFF2A9D8F) // Vibrant Emerald Green / Yellow-Green for Income

    val expenseCategoryColors = mapOf(
        Category.FOOD to Color(0xFFD00000),
        Category.TRANSPORT to Color(0xFF1E88E5),
        Category.BILLS to Color(0xFFFFB703),
        Category.ENTERTAINMENT to Color(0xFF9B5DE5),
        Category.HEALTH to Color(0xFF2A9D8F),
        Category.SHOPPING to Color(0xFFF15BB5),
        Category.EDUCATION to Color(0xFF00F5D4),
        Category.GIFTS to Color(0xFFE85D04),
        Category.OTHER to Color(0xFF6B7280)
    )

    val currentTotal = if (selectedBreakdownType == TransactionType.EXPENSE) {
        summary.totalExpense
    } else {
        summary.totalIncome
    }

    val incomeGradientBrush = androidx.compose.ui.graphics.Brush.linearGradient(
        colors = listOf(
            Color(0xFF2A9D8F), // Emerald Green
            Color(0xFFF6C445), // Treasure Gold
            Color(0xFFAACC00)  // Neon Lime
        )
    )

    // Donut chart data calculation
    val chartData = if (selectedBreakdownType == TransactionType.INCOME) {
        // Income Wheel: Decreases as user logs expenses with rich gradient brush
        val netBalance = summary.balance.coerceAtLeast(0.0)
        val spentExpense = summary.totalExpense
        if (summary.totalIncome > 0) {
            listOf(
                ChartData(value = netBalance.toFloat(), color = accentYellowGreen, brush = incomeGradientBrush),
                ChartData(value = spentExpense.toFloat(), color = accentCrimson.copy(alpha = 0.35f))
            )
        } else {
            listOf(ChartData(value = 0f, color = accentYellowGreen, brush = incomeGradientBrush))
        }
    } else {
        // Expense Wheel: Category breakdown
        summary.categoryBreakdown.entries.map { (category, amount) ->
            ChartData(
                value = amount.toFloat(),
                color = expenseCategoryColors[category] ?: category.color
            )
        }.sortedByDescending { it.value }
    }

    val currency = LocalCurrency.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column(modifier = Modifier.animateEnter(animIndex++)) {
                Text(
                    text = if (isSpidey) "Hero Web Insights 🕷️" else "Grand Line Insights 📊",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isSpidey) "Full breakdown of incomes, expenses & AI recommendations" else "Full visual breakdown of pirate incomes, expenses & crew recommendations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Overview Summary Cards (Income vs Expense)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateEnter(animIndex++),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryMetricCard(
                    title = when {
                        isSpidey -> "Income ⚡"
                        isBasic  -> "Total Income 📈"
                        else     -> "Bounty Gain 💰"
                    },
                    amount = formatAmountString(summary.totalIncome, currency),
                    color = accentYellowGreen,
                    modifier = Modifier.weight(1f)
                )
                SummaryMetricCard(
                    title = when {
                        isSpidey -> "Expense 🕸️"
                        isBasic  -> "Total Expense 💳"
                        else     -> "Bounty Loss ⚓"
                    },
                    amount = formatAmountString(summary.totalExpense, currency),
                    color = accentCrimson,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Interactive Breakdown Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateEnter(animIndex++),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(
                    1.dp,
                    if (isSpidey) Color(0xFF1D4ED8).copy(alpha = 0.4f) else if (isBasic) Color(0xFF00E599).copy(alpha = 0.4f) else accentGold.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Type Toggle Pills (Income vs Expense)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PillTab(
                            text = when {
                                isSpidey -> "Incomes ⚡"
                                isBasic  -> "Income 📈"
                                else     -> "Bounty Gain 💰"
                            },
                            selected = selectedBreakdownType == TransactionType.INCOME,
                            selectedColor = accentYellowGreen,
                            onClick = { selectedBreakdownType = TransactionType.INCOME },
                            modifier = Modifier.weight(1f)
                        )
                        PillTab(
                            text = when {
                                isSpidey -> "Expenses 🕸️"
                                isBasic  -> "Expenses 💳"
                                else     -> "Bounty Loss ⚓"
                            },
                            selected = selectedBreakdownType == TransactionType.EXPENSE,
                            selectedColor = accentCrimson,
                            onClick = { selectedBreakdownType = TransactionType.EXPENSE },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val centerAmount = if (selectedBreakdownType == TransactionType.EXPENSE) {
                        summary.totalExpense
                    } else {
                        summary.balance
                    }

                    val centerLabel = if (selectedBreakdownType == TransactionType.EXPENSE) {
                        when {
                            isSpidey -> "WEB EXPENSE"
                            isBasic  -> "TOTAL EXPENSE"
                            else     -> "BOUNTY SPENT"
                        }
                    } else {
                        when {
                            isSpidey -> "NET HERO BALANCE"
                            isBasic  -> "NET BALANCE"
                            else     -> "NET BOUNTY BALANCE"
                        }
                    }

                    // Centered Donut Chart (Yellow/Green color wheel for Income)
                    DonutChart(
                        data = chartData,
                        totalAmount = centerAmount,
                        labelText = centerLabel,
                        labelColor = if (selectedBreakdownType == TransactionType.EXPENSE) accentCrimson else accentYellowGreen
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (selectedBreakdownType == TransactionType.INCOME) {
                        // Legend chips for Income Balance & Expenses
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val balancePercent = if (summary.totalIncome > 0) ((summary.balance / summary.totalIncome) * 100).toInt().coerceAtLeast(0) else 0
                            val spentPercent = if (summary.totalIncome > 0) ((summary.totalExpense / summary.totalIncome) * 100).toInt().coerceAtMost(100) else 0

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = accentYellowGreen.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, accentYellowGreen.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(accentYellowGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Net Balance: ${formatAmountString(summary.balance, currency)} ($balancePercent%)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = accentCrimson.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, accentCrimson.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(accentCrimson)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Spent Expenses: ${formatAmountString(summary.totalExpense, currency)} ($spentPercent%)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    } else if (currentTotal > 0 && chartData.isNotEmpty()) {
                        // Category Legend Chips for Expense
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            summary.categoryBreakdown.forEach { (cat, amount) ->
                                val catColor = expenseCategoryColors[cat] ?: cat.color
                                val percent = ((amount / currentTotal) * 100).toInt()
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = catColor.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, catColor.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(catColor)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${cat.displayName}: ${formatAmountString(amount, currency)} ($percent%)",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = when {
                                isSpidey -> "No Web Expenses Recorded Yet"
                                isBasic  -> "No Expenses Recorded Yet"
                                else     -> "No Bounty Losses Recorded Yet"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = when {
                    isSpidey -> "AI Web Suggestions 💡"
                    isBasic  -> "FinBot Smart Insights 💡"
                    else     -> "Smart Crew Suggestions ⛵"
                },
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .animateEnter(animIndex++)
            )
        }

        if (suggestions.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateEnter(animIndex++),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "⛵", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You're all set! No spending warnings at the moment.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        } else {
            items(suggestions) { suggestion ->
                val currentIdx = animIndex++
                SuggestionCard(
                    suggestion = suggestion,
                    modifier = Modifier.animateEnter(currentIdx)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SummaryMetricCard(
    title: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
private fun PillTab(
    text: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) selectedColor else Color.Transparent
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
