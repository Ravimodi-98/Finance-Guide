package com.example.financeguide.ui.screen.collaborative

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financeguide.data.remote.model.ExpenseSplitDto
import com.example.financeguide.data.repository.CollaborativeRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCollaborativeExpenseScreen(
    groupId: String,
    repository: CollaborativeRepository,
    onBackClick: () -> Unit,
    onExpenseAdded: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedSplitType by remember { mutableStateOf("EQUAL") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val splitOptions = listOf("EQUAL", "PERCENTAGE", "EXACT", "SHARES")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Collaborative Expense", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Expense Description (e.g. Goa Lunch)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Total Amount ($)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Split Method", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                splitOptions.forEach { type ->
                    FilterChip(
                        selected = selectedSplitType == type,
                        onClick = { selectedSplitType = type },
                        label = { Text(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (description.isNotEmpty() && amount > 0) {
                        coroutineScope.launch {
                            isLoading = true
                            // Default equal split
                            val splits = listOf(ExpenseSplitDto(userId = "me", owedAmount = amount))
                            repository.createExpense(
                                groupId = groupId,
                                description = description,
                                totalAmount = amount,
                                paidById = "me",
                                paidByName = "You",
                                splitType = selectedSplitType,
                                splits = splits
                            )
                            isLoading = false
                            onExpenseAdded()
                        }
                    }
                },
                enabled = !isLoading && description.isNotEmpty() && (amountText.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Expense", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
