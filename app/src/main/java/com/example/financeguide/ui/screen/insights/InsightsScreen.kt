package com.example.financeguide.ui.screen.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeguide.ui.FinanceViewModel
import com.example.financeguide.ui.components.ChartData
import com.example.financeguide.ui.components.DonutChart
import com.example.financeguide.ui.components.SuggestionCard

import com.example.financeguide.ui.components.animateEnter

@Composable
fun InsightsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    var animIndex = 0

    val chartData = summary.categoryBreakdown.map { (category, amount) ->
        ChartData(
            value = amount.toFloat(),
            color = category.color
        )
    }.sortedByDescending { it.value }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Insights",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp).animateEnter(animIndex++)
            )
        }

        if (summary.totalExpense > 0) {
            item {
                Box(modifier = Modifier.animateEnter(animIndex++)) {
                    Column {
                        Text(
                            text = "Spending Breakdown",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            DonutChart(
                                data = chartData,
                                totalAmount = summary.totalExpense
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Smart Suggestions",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).animateEnter(animIndex++)
            )
        }

        if (suggestions.isEmpty()) {
            item {
                Text(
                    text = "You're all set! No suggestions at the moment.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.animateEnter(animIndex++)
                )
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
            Spacer(modifier = Modifier.height(80.dp)) // FAB/nav bar clearance
        }
    }
}
