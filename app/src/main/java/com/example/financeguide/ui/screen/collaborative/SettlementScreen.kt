package com.example.financeguide.ui.screen.collaborative

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financeguide.data.repository.CollaborativeRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlementScreen(
    groupId: String?,
    payeeId: String,
    payeeName: String,
    initialAmount: Double,
    repository: CollaborativeRepository,
    onBackClick: () -> Unit,
    onSettled: () -> Unit
) {
    var amountText by remember { mutableStateOf(initialAmount.toString()) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Settlement", fontWeight = FontWeight.Bold) },
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
            Text("Paying to: $payeeName", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Settlement Amount ($)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        coroutineScope.launch {
                            isLoading = true
                            repository.createSettlement(groupId, payeeId, payeeName, amount)
                            isLoading = false
                            onSettled()
                        }
                    }
                },
                enabled = !isLoading && (amountText.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Confirm Settlement", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
