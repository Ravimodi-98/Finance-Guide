package com.example.financeguide.ui.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeguide.ui.FinanceViewModel
import com.example.financeguide.ui.components.BalanceCard
import com.example.financeguide.ui.components.SuggestionCard
import com.example.financeguide.ui.components.TransactionItem
import com.example.financeguide.ui.components.animateEnter
import com.example.financeguide.theme.LocalThemeStrings

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    onAddIncome: () -> Unit = {},
    onAddExpense: () -> Unit = {},
    onNavigateToAI: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val strings = LocalThemeStrings.current
    // Edit dialog state
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<com.example.financeguide.data.local.entity.TransactionEntity?>(null) }
    var editedTitle by remember { mutableStateOf("") }
    var editedAmount by remember { mutableStateOf("") }
    var editedCategory by remember { mutableStateOf("") }
    var editedNote by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    var animIndex = 0

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Box(modifier = Modifier.animateEnter(animIndex++)) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.dashboardTitle,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(
                            onClick = onNavigateToAI
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Explore,
                                contentDescription = "Finance Navigator",
                                tint = com.example.financeguide.theme.Secondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    BalanceCard(
                        balance = summary.balance,
                        income = summary.totalIncome,
                        expense = summary.totalExpense,
                        onIncomeClick = onAddIncome,
                        onExpenseClick = onAddExpense
                    )
                }
            }
        }

        if (suggestions.isNotEmpty()) {
            item {
                Box(modifier = Modifier.animateEnter(animIndex++)) {
                    Column {
                        Text(
                            text = strings.tipTitle,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SuggestionCard(suggestion = suggestions.first())
                    }
                }
            }
        }

        item {
            Text(
                text = strings.transactionListTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp).animateEnter(animIndex++)
            )
        }

        if (recentTransactions.isEmpty()) {
            item {
                Text(
                    text = "No recent transactions. Add one to get started!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).animateEnter(animIndex++)
                )
            }
        } else {
            items(recentTransactions) { transaction ->
                val currentIdx = animIndex++
                TransactionItem(
                    transaction = transaction,
                    modifier = Modifier.animateEnter(currentIdx),
                    onEdit = { selected ->
                        selectedTransaction = selected
                        editedTitle = selected.title
                        editedAmount = selected.amount.toString()
                        editedCategory = selected.category
                        editedNote = selected.note ?: ""
                        showEditDialog = true
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Edit Dialog - placed outside LazyColumn as a sibling composable
    if (showEditDialog && selectedTransaction != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Transaction") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedAmount,
                        onValueChange = { editedAmount = it },
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedCategory,
                        onValueChange = { editedCategory = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedNote,
                        onValueChange = { editedNote = it },
                        label = { Text("Note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amount = editedAmount.toDoubleOrNull() ?: selectedTransaction!!.amount
                    val updated = selectedTransaction!!.copy(
                        title = editedTitle,
                        amount = amount,
                        category = editedCategory,
                        note = if (editedNote.isBlank()) null else editedNote
                    )
                    viewModel.updateTransaction(updated)
                    showEditDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }
}

