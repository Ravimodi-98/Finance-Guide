package com.example.financeguide.ui.screen.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.financeguide.data.local.entity.TransactionEntity
import com.example.financeguide.domain.model.Category
import com.example.financeguide.domain.model.TransactionType
import com.example.financeguide.theme.LocalThemeStrings
import com.example.financeguide.ui.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(
    viewModel: FinanceViewModel,
    onBack: () -> Unit,
    initialType: TransactionType = TransactionType.INCOME,
    modifier: Modifier = Modifier
) {
    var amount by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(initialType) }
    var selectedCategory by remember(initialType) {
        mutableStateOf(if (initialType == TransactionType.INCOME) Category.SALARY else Category.FOOD)
    }

    val strings = LocalThemeStrings.current
    val isSpidey = strings.chatbotName == "Spider-Man"

    val accentGold = Color(0xFFF6C445)
    val accentOcean = Color(0xFF1E88E5)
    val accentCrimson = Color(0xFFC62828)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isSpidey) "🕷️ Log Entry" else "⛵ Log Bounty Entry",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Type Selector (Income vs Expense)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CreativeTypeButton(
                    text = if (isSpidey) "Income ⚡" else "Bounty Gain 💰",
                    selected = selectedType == TransactionType.INCOME,
                    gradient = if (isSpidey) {
                        listOf(Color(0xFF1D4ED8), Color(0xFF00388A))
                    } else {
                        listOf(Color(0xFF1E88E5), Color(0xFFF6C445))
                    },
                    onClick = {
                        selectedType = TransactionType.INCOME
                        if (!selectedCategory.isIncome) {
                            selectedCategory = Category.SALARY
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                CreativeTypeButton(
                    text = if (isSpidey) "Expense 🕸️" else "Bounty Loss ⚓",
                    selected = selectedType == TransactionType.EXPENSE,
                    gradient = if (isSpidey) {
                        listOf(Color(0xFFC1121F), Color(0xFF780000))
                    } else {
                        listOf(Color(0xFFC62828), Color(0xFFE85D04))
                    },
                    onClick = {
                        selectedType = TransactionType.EXPENSE
                        if (selectedCategory.isIncome) {
                            selectedCategory = Category.FOOD
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Creative Amount Input Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(
                    1.5.dp,
                    if (selectedType == TransactionType.INCOME) accentOcean else accentCrimson
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (selectedType == TransactionType.INCOME) accentOcean.copy(alpha = 0.2f) else accentCrimson.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSpidey) "$" else "฿",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == TransactionType.INCOME) accentOcean else accentCrimson
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = { Text("0.00", style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(if (isSpidey) "Title or Reason" else "Bounty Note or Reason") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isSpidey) Color(0xFF1D4ED8) else accentGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (selectedType == TransactionType.INCOME) {
                    if (isSpidey) "Income Category ⚡" else "Bounty Gain Category 💰"
                } else {
                    if (isSpidey) "Expense Category 🕸️" else "Bounty Loss Category ⚓"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Categories Grid (Separate for Income vs Expense)
            val availableCategories = if (selectedType == TransactionType.INCOME) {
                Category.incomeCategories()
            } else {
                Category.expenseCategories()
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                availableCategories.forEach { category ->
                    CreativeCategoryItem(
                        category = category,
                        selected = category == selectedCategory,
                        isSpidey = isSpidey,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Creative Save Button
            val isFormValid = amount.isNotBlank() && title.isNotBlank()
            val saveGradient = if (isSpidey) {
                listOf(Color(0xFFC1121F), Color(0xFF1D4ED8))
            } else if (selectedType == TransactionType.INCOME) {
                listOf(accentOcean, accentGold)
            } else {
                listOf(accentCrimson, Color(0xFFE85D04))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = if (isFormValid) Brush.linearGradient(saveGradient) else Brush.linearGradient(listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.3f)))
                    )
                    .clickable(enabled = isFormValid) {
                        val amountValue = amount.toDoubleOrNull()
                        if (amountValue != null && title.isNotBlank()) {
                            val transaction = TransactionEntity(
                                amount = amountValue,
                                title = title,
                                category = selectedCategory.name,
                                type = selectedType.name,
                                date = System.currentTimeMillis()
                            )
                            viewModel.addTransaction(transaction)
                            onBack()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSpidey) "SAVE ENTRY 🕸️" else "LOG TO PIRATE LOGBOOK 📜",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isFormValid) Color.White else Color.White.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CreativeTypeButton(
    text: String,
    selected: Boolean,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(
                brush = if (selected) Brush.linearGradient(gradient) else Brush.linearGradient(listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.08f)))
            )
            .border(
                border = BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) gradient.first() else Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(25.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CreativeCategoryItem(
    category: Category,
    selected: Boolean,
    isSpidey: Boolean,
    onClick: () -> Unit
) {
    val categoryEmoji = when (category) {
        Category.FOOD -> if (isSpidey) "🍕" else "🍖"
        Category.TRANSPORT -> if (isSpidey) "🕸️" else "🚢"
        Category.BILLS -> if (isSpidey) "📜" else "📜"
        Category.ENTERTAINMENT -> if (isSpidey) "🎮" else "🎪"
        Category.HEALTH -> if (isSpidey) "🧪" else "💊"
        Category.SHOPPING -> if (isSpidey) "🛍️" else "💎"
        Category.EDUCATION -> if (isSpidey) "📚" else "📖"
        Category.GIFTS -> if (isSpidey) "🎁" else "🎁"
        Category.OTHER -> if (isSpidey) "🧭" else "🧭"
        Category.SALARY -> if (isSpidey) "💼" else "💰"
        Category.INVESTMENT -> if (isSpidey) "📈" else "📊"
        Category.FREELANCE -> if (isSpidey) "🤝" else "📜"
        Category.BUSINESS -> if (isSpidey) "🏪" else "🪙"
        Category.BONUS -> if (isSpidey) "🏆" else "🏆"
        Category.INTEREST -> if (isSpidey) "🏦" else "⚓"
        Category.OTHER_INCOME -> if (isSpidey) "💵" else "💎"
    }

    val selectedColor = if (isSpidey) Color(0xFFC1121F) else Color(0xFFF6C445)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    if (selected) selectedColor.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
                )
                .border(
                    border = BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) selectedColor else Color.White.copy(alpha = 0.08f)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = categoryEmoji, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
