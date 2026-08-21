package com.example.financeguide.ui.screen.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeguide.theme.LocalThemeStrings
import com.example.financeguide.ui.FinanceViewModel
import com.example.financeguide.ui.components.BalanceCard
import com.example.financeguide.ui.components.SuggestionCard
import com.example.financeguide.ui.components.ThemeAppLogo
import com.example.financeguide.ui.components.TransactionItem
import com.example.financeguide.ui.components.animateEnter

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    onAddIncome: () -> Unit = {},
    onAddExpense: () -> Unit = {},
    onNavigateToChatbot: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val strings = LocalThemeStrings.current
    val isSpidey = strings.chatbotName == "Spider-Man"
    val isBasic = strings.chatbotName == "FinBot"

    val aiButtonGradient = when {
        isSpidey -> listOf(Color(0xFFC1121F), Color(0xFF1D4ED8))
        isBasic  -> listOf(Color(0xFF00E599), Color(0xFF00B4D8))
        else     -> listOf(Color(0xFF0284C7), Color(0xFFFFB703))
    }

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
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.dashboardTitle,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        // Top bar Ask AI Button
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.Transparent,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Brush.linearGradient(aiButtonGradient))
                                .clickable { onNavigateToChatbot() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = "Ask AI",
                                    tint = if (isBasic) Color(0xFF080D1A) else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Ask AI",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isBasic) Color(0xFF080D1A) else Color.White
                                )
                            }
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

        // AI Advisor Hero Card
        item {
            Box(modifier = Modifier.animateEnter(animIndex++)) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onNavigateToChatbot() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(aiButtonGradient)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = "AI Advisor",
                                tint = if (isBasic) Color(0xFF080D1A) else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Chat with ${strings.chatbotName}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "AI ⚡",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ask about savings, budgeting, investments, & stock tips.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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

