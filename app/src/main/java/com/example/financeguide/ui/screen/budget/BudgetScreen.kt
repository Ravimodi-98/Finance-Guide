package com.example.financeguide.ui.screen.budget

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.financeguide.data.local.entity.BudgetEntity
import com.example.financeguide.domain.model.BudgetStatus
import com.example.financeguide.domain.model.Category
import com.example.financeguide.theme.LocalThemeStrings
import com.example.financeguide.ui.FinanceViewModel
import com.example.financeguide.ui.components.BudgetProgressBar
import com.example.financeguide.ui.components.animateEnter
import java.util.Calendar

@Composable
fun BudgetScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val budgetStatuses by viewModel.budgetStatuses.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var budgetToEdit by remember { mutableStateOf<BudgetStatus?>(null) }
    var budgetToDelete by remember { mutableStateOf<BudgetStatus?>(null) }
    var animIndex = 0
    val strings = LocalThemeStrings.current
    val isSpidey = strings.chatbotName == "Spider-Man"

    val accentGold = Color(0xFFF6C445)
    val accentOcean = Color(0xFF1E88E5)

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (isSpidey) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFC1121F), Color(0xFF1D4ED8))
                            )
                        )
                        .clickable { showAddDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add Budget", tint = Color.White)
                }
            } else {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = accentOcean,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add Budget")
                }
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
                Column(modifier = Modifier.animateEnter(animIndex++)) {
                    Text(
                        text = if (isSpidey) "Hero Limits & Budgets 🕸️" else "Bounty Limits (Budgets) ⚓",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isSpidey) "Set spending caps to protect your financial ledger" else "Set category spending limits to guard your treasure chest on the Grand Line",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            if (budgetStatuses.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .animateEnter(animIndex++),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(
                            1.dp,
                            if (isSpidey) Color(0xFF1D4ED8).copy(alpha = 0.4f) else accentGold.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSpidey) Color(0xFFC1121F).copy(alpha = 0.15f) else accentGold.copy(alpha = 0.15f)
                                    )
                                    .border(
                                        border = BorderStroke(
                                            2.dp,
                                            if (isSpidey) Color(0xFFC1121F) else accentGold
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isSpidey) "🛡️" else "💎",
                                    fontSize = 36.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (isSpidey) "No Hero Budgets Set Yet!" else "No Bounty Limits Created Yet!",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isSpidey) "Set monthly spending limits for Food, Transport, and Shopping to stay in full control." else "Guard your Berry bounty by creating budget limits for Food, Ship Transport, Entertainment, and more!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            val ctaGradient = if (isSpidey) {
                                listOf(Color(0xFFC1121F), Color(0xFF1D4ED8))
                            } else {
                                listOf(accentOcean, accentGold)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(brush = Brush.linearGradient(ctaGradient))
                                    .clickable { showAddDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isSpidey) "+ SET FIRST HERO BUDGET 🕸️" else "+ SET FIRST BOUNTY LIMIT 📜",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                items(budgetStatuses, key = { it.budgetId }) { budgetStatus ->
                    val currentIdx = animIndex++
                    BudgetProgressBar(
                        budgetStatus = budgetStatus,
                        onEditClick = { budgetToEdit = budgetStatus },
                        onDeleteClick = { budgetToDelete = budgetStatus },
                        modifier = Modifier.animateEnter(currentIdx)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        if (showAddDialog) {
            CreativeAddBudgetDialog(
                isSpidey = isSpidey,
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

        budgetToEdit?.let { targetStatus ->
            CreativeEditBudgetDialog(
                budgetStatus = targetStatus,
                isSpidey = isSpidey,
                onDismiss = { budgetToEdit = null },
                onConfirm = { updatedLimit ->
                    val cal = Calendar.getInstance()
                    val monthYear = String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
                    val budget = BudgetEntity(
                        id = targetStatus.budgetId,
                        category = targetStatus.category.name,
                        monthYear = monthYear,
                        limitAmount = updatedLimit
                    )
                    viewModel.addBudget(budget)
                    budgetToEdit = null
                }
            )
        }

        budgetToDelete?.let { targetStatus ->
            ConfirmDeleteBudgetDialog(
                budgetStatus = targetStatus,
                isSpidey = isSpidey,
                onDismiss = { budgetToDelete = null },
                onConfirm = {
                    val cal = Calendar.getInstance()
                    val monthYear = String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
                    val budget = BudgetEntity(
                        id = targetStatus.budgetId,
                        category = targetStatus.category.name,
                        monthYear = monthYear,
                        limitAmount = targetStatus.limitAmount
                    )
                    viewModel.deleteBudget(budget)
                    budgetToDelete = null
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreativeAddBudgetDialog(
    isSpidey: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Category, Double) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(Category.FOOD) }
    var limit by remember { mutableStateOf("") }
    val accentGold = Color(0xFFF6C445)
    val accentOcean = Color(0xFF1E88E5)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isSpidey) "Set Hero Budget Cap 🕸️" else "Set Bounty Limit ⚓",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Select Category",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Category Selection Grid
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Category.expenseCategories().forEach { category ->
                        val isSelected = category == selectedCategory
                        val categoryEmoji = when (category) {
                            Category.FOOD -> if (isSpidey) "🍕" else "🍖"
                            Category.TRANSPORT -> if (isSpidey) "🕸️" else "🚢"
                            Category.BILLS -> if (isSpidey) "📜" else "📜"
                            Category.ENTERTAINMENT -> if (isSpidey) "🎮" else "🎪"
                            Category.HEALTH -> if (isSpidey) "🧪" else "💊"
                            Category.SHOPPING -> if (isSpidey) "🛍️" else "💎"
                            Category.EDUCATION -> if (isSpidey) "📚" else "📖"
                            Category.GIFTS -> if (isSpidey) "🎁" else "🎁"
                            else -> if (isSpidey) "🧭" else "🧭"
                        }

                        Surface(
                            modifier = Modifier.clickable { selectedCategory = category },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) (if (isSpidey) Color(0xFFC1121F).copy(alpha = 0.2f) else accentGold.copy(alpha = 0.2f)) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) (if (isSpidey) Color(0xFFC1121F) else accentGold) else Color.White.copy(alpha = 0.08f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = categoryEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = { Text("Monthly Limit Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isSpidey) Color(0xFF1D4ED8) else accentGold,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limitVal = limit.toDoubleOrNull()
                    if (limitVal != null && limitVal > 0) {
                        onConfirm(selectedCategory, limitVal)
                    }
                },
                enabled = limit.isNotBlank() && (limit.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSpidey) Color(0xFFC1121F) else accentOcean,
                    contentColor = Color.White
                )
            ) {
                Text("Set Limit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
fun CreativeEditBudgetDialog(
    budgetStatus: BudgetStatus,
    isSpidey: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var limit by remember { mutableStateOf(budgetStatus.limitAmount.toInt().toString()) }
    val accentGold = Color(0xFFF6C445)
    val accentOcean = Color(0xFF1E88E5)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit ${budgetStatus.category.displayName} Budget ✏️",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Update monthly spending limit for ${budgetStatus.category.displayName}:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = { Text("New Monthly Limit") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isSpidey) Color(0xFF1D4ED8) else accentGold,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limitVal = limit.toDoubleOrNull()
                    if (limitVal != null && limitVal > 0) {
                        onConfirm(limitVal)
                    }
                },
                enabled = limit.isNotBlank() && (limit.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSpidey) Color(0xFFC1121F) else accentOcean,
                    contentColor = Color.White
                )
            ) {
                Text("Update Limit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
fun ConfirmDeleteBudgetDialog(
    budgetStatus: BudgetStatus,
    isSpidey: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Budget Limit? 🗑️",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Are you sure you want to remove the budget limit for ${budgetStatus.category.displayName}? This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                )
            ) {
                Text("Delete", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
