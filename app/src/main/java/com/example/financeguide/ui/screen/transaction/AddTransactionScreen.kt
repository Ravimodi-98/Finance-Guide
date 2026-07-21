package com.example.financeguide.ui.screen.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.financeguide.ui.components.LocalCurrency
import com.example.financeguide.ui.components.getCurrencySymbol

@OptIn(ExperimentalMaterial3Api::class)
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
    val isBasic  = strings.chatbotName == "FinBot"

    val accentGold = Color(0xFFF6C445)
    val accentOcean = Color(0xFF1E88E5)
    val accentCrimson = Color(0xFFC62828)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when {
                                isSpidey -> "🕷️ Log Entry"
                                isBasic  -> "📊 Log Transaction"
                                else     -> "⛵ Log Bounty Entry"
                            },
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
                    text = when {
                        isSpidey -> "Income ⚡"
                        isBasic  -> "Income 📈"
                        else     -> "Bounty Gain 💰"
                    },
                    selected = selectedType == TransactionType.INCOME,
                    gradient = when {
                        isSpidey -> listOf(Color(0xFF1D4ED8), Color(0xFF00388A))
                        isBasic  -> listOf(Color(0xFF00E599), Color(0xFF009966))
                        else     -> listOf(Color(0xFF1E88E5), Color(0xFFF6C445))
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
                    text = when {
                        isSpidey -> "Expense 🕸️"
                        isBasic  -> "Expense 💳"
                        else     -> "Bounty Loss ⚓"
                    },
                    selected = selectedType == TransactionType.EXPENSE,
                    gradient = when {
                        isSpidey -> listOf(Color(0xFFC1121F), Color(0xFF780000))
                        isBasic  -> listOf(Color(0xFFEF4444), Color(0xFF991B1B))
                        else     -> listOf(Color(0xFFC62828), Color(0xFFE85D04))
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
                    val currencyCode = LocalCurrency.current
                    val currencySymbol = getCurrencySymbol(currencyCode)
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
                            text = currencySymbol,
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
                label = { Text(when { isSpidey -> "Title or Reason"; isBasic -> "Title or Note"; else -> "Bounty Note or Reason" }) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = when {
                        isSpidey -> Color(0xFF1D4ED8)
                        isBasic  -> Color(0xFF00E599)
                        else     -> accentGold
                    },
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (selectedType == TransactionType.INCOME) {
                    when {
                        isSpidey -> "Income Category ⚡"
                        isBasic  -> "Income Category 📈"
                        else     -> "Bounty Gain Category 💰"
                    }
                } else {
                    when {
                        isSpidey -> "Expense Category 🕸️"
                        isBasic  -> "Expense Category 💳"
                        else     -> "Bounty Loss Category ⚓"
                    }
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

            CategoryGrid(
                categories = availableCategories,
                selectedCategory = selectedCategory,
                isSpidey = isSpidey,
                isBasic = isBasic,
                onSelect = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Creative Save Button
            val isFormValid = amount.isNotBlank() && title.isNotBlank()
            val saveGradient = when {
                isSpidey -> listOf(Color(0xFFC1121F), Color(0xFF1D4ED8))
                isBasic  -> listOf(Color(0xFF00E599), Color(0xFF00D2FF))
                selectedType == TransactionType.INCOME -> listOf(accentOcean, accentGold)
                else -> listOf(accentCrimson, Color(0xFFE85D04))
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
                    text = when {
                        isSpidey -> "SAVE ENTRY 🕸️"
                        isBasic  -> "SAVE TRANSACTION 🚀"
                        else     -> "LOG TO PIRATE LOGBOOK 📜"
                    },
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
private fun CategoryGrid(
    categories: List<Category>,
    selectedCategory: Category,
    isSpidey: Boolean,
    isBasic: Boolean = false,
    onSelect: (Category) -> Unit
) {
    // Split into rows of 3
    val rows = categories.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { category ->
                    CategoryCard(
                        category = category,
                        selected = category == selectedCategory,
                        isSpidey = isSpidey,
                        isBasic = isBasic,
                        onClick = { onSelect(category) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty slots in last row so items stay equal-width
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    selected: Boolean,
    isSpidey: Boolean,
    isBasic: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emoji = when (category) {
        Category.FOOD          -> when { isSpidey -> "🍕"; isBasic -> "🛒"; else -> "🍖" }
        Category.TRANSPORT     -> when { isSpidey -> "🚇"; isBasic -> "🚗"; else -> "🚢" }
        Category.BILLS         -> when { isSpidey -> "📜"; isBasic -> "📄"; else -> "📋" }
        Category.ENTERTAINMENT -> when { isSpidey -> "🎮"; isBasic -> "🎬"; else -> "🎪" }
        Category.HEALTH        -> when { isSpidey -> "🧪"; isBasic -> "💊"; else -> "💊" }
        Category.SHOPPING      -> when { isSpidey -> "🛍️"; isBasic -> "🛍️"; else -> "💎" }
        Category.EDUCATION     -> when { isSpidey -> "📚"; isBasic -> "📚"; else -> "📖" }
        Category.GIFTS         -> "🎁"
        Category.OTHER         -> "📁"
        Category.SALARY        -> "💼"
        Category.INVESTMENT    -> "📈"
        Category.FREELANCE     -> "💻"
        Category.BUSINESS      -> "🏢"
        Category.BONUS         -> "🏆"
        Category.INTEREST      -> "🏦"
        Category.OTHER_INCOME  -> "💵"
    }

    val accentColor = when { isSpidey -> Color(0xFFC1121F); isBasic -> Color(0xFF00E599); else -> Color(0xFFF6C445) }
    val categoryColor = category.color

    Surface(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (selected)
            categoryColor.copy(alpha = 0.18f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) categoryColor else Color.White.copy(alpha = 0.07f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = if (selected) 0.30f else 0.13f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 10.sp
                ),
                color = if (selected) categoryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
