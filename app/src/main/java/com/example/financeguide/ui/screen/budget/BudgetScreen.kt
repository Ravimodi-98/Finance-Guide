package com.example.financeguide.ui.screen.budget

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeguide.data.local.entity.BudgetEntity
import com.example.financeguide.domain.model.Category
import com.example.financeguide.ui.FinanceViewModel
import com.example.financeguide.ui.components.BudgetProgressBar
import com.example.financeguide.theme.LocalThemeStrings
import java.util.Calendar

import com.example.financeguide.ui.components.animateEnter

@Composable
fun BudgetScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val budgetStatuses by viewModel.budgetStatuses.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var animIndex = 0
    val strings = LocalThemeStrings.current

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Budget")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = strings.budgetScreenTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp).animateEnter(animIndex++)
                )
            }

            if (budgetStatuses.isEmpty()) {
                item {
                    Text(
                        text = "No ${strings.budgetScreenTitle.lowercase()} set yet. Add a budget to start tracking your spending limits.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 32.dp).animateEnter(animIndex++)
                    )
                }
            } else {
                items(budgetStatuses) { budgetStatus ->
                    val currentIdx = animIndex++
                    BudgetProgressBar(
                        budgetStatus = budgetStatus,
                        modifier = Modifier.animateEnter(currentIdx)
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp)) // FAB clearance
            }
        }

        if (showAddDialog) {
            AddBudgetDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { category, limit ->
                    val cal = Calendar.getInstance()
                    val monthYear = String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
                    val budget = BudgetEntity(
                        category = category.name,
                        monthYear = monthYear,
                        limitAmount = limit
                    )
                    viewModel.addBudget(budget)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onConfirm: (Category, Double) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(Category.FOOD) }
    var limit by remember { mutableStateOf("") }
    val strings = LocalThemeStrings.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.addBudgetTitle) },
        text = {
            Column {
                Text("Category", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                // Just using a simple text field for category in this MVP dialog for speed
                // Ideally this would be a dropdown or the visual grid
                OutlinedTextField(
                    value = selectedCategory.displayName,
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                // For MVP, just cycle through categories on click
                Button(
                    onClick = {
                        val entries = Category.entries
                        val nextIndex = (entries.indexOf(selectedCategory) + 1) % entries.size
                        selectedCategory = entries[nextIndex]
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Category")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = { Text("Monthly Limit (฿)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val limitVal = limit.toDoubleOrNull()
                    if (limitVal != null) {
                        onConfirm(selectedCategory, limitVal)
                    }
                },
                enabled = limit.isNotBlank() && limit.toDoubleOrNull() != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
