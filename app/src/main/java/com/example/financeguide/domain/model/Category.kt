package com.example.financeguide.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class Category(
    val displayName: String,
    val icon: ImageVector,
    val color: Color,
    val isIncome: Boolean = false
) {
    // --- Expense Categories ---
    FOOD(
        displayName = "Food",
        icon = Icons.Rounded.Restaurant,
        color = Color(0xFFEF4444) // Red
    ),
    TRANSPORT(
        displayName = "Transport",
        icon = Icons.Rounded.DirectionsBus,
        color = Color(0xFF3B82F6) // Blue
    ),
    BILLS(
        displayName = "Bills",
        icon = Icons.Rounded.Receipt,
        color = Color(0xFFF59E0B) // Amber
    ),
    ENTERTAINMENT(
        displayName = "Entertainment",
        icon = Icons.Rounded.Movie,
        color = Color(0xFF8B5CF6) // Purple
    ),
    HEALTH(
        displayName = "Health",
        icon = Icons.Rounded.FitnessCenter,
        color = Color(0xFF10B981) // Emerald
    ),
    SHOPPING(
        displayName = "Shopping",
        icon = Icons.Rounded.ShoppingBag,
        color = Color(0xFFEC4899) // Pink
    ),
    EDUCATION(
        displayName = "Education",
        icon = Icons.Rounded.MenuBook,
        color = Color(0xFF06B6D4) // Cyan
    ),
    GIFTS(
        displayName = "Gifts",
        icon = Icons.Rounded.CardGiftcard,
        color = Color(0xFFF97316) // Orange
    ),
    OTHER(
        displayName = "Other Expense",
        icon = Icons.Rounded.MoreHoriz,
        color = Color(0xFF6B7280) // Gray
    ),

    // --- Income Categories ---
    SALARY(
        displayName = "Salary",
        icon = Icons.Rounded.Payments,
        color = Color(0xFF10B981), // Emerald
        isIncome = true
    ),
    INVESTMENT(
        displayName = "Investment",
        icon = Icons.Rounded.TrendingUp,
        color = Color(0xFF3B82F6), // Blue
        isIncome = true
    ),
    FREELANCE(
        displayName = "Freelance",
        icon = Icons.Rounded.Work,
        color = Color(0xFF8B5CF6), // Purple
        isIncome = true
    ),
    BUSINESS(
        displayName = "Business",
        icon = Icons.Rounded.Storefront,
        color = Color(0xFFF59E0B), // Amber
        isIncome = true
    ),
    BONUS(
        displayName = "Bonus",
        icon = Icons.Rounded.EmojiEvents,
        color = Color(0xFFEC4899), // Pink
        isIncome = true
    ),
    INTEREST(
        displayName = "Interest",
        icon = Icons.Rounded.AccountBalance,
        color = Color(0xFF06B6D4), // Cyan
        isIncome = true
    ),
    OTHER_INCOME(
        displayName = "Other Income",
        icon = Icons.Rounded.Savings,
        color = Color(0xFF2A9D8F), // Yellow/Green Teal
        isIncome = true
    );

    companion object {
        fun fromString(value: String): Category {
            return entries.find { it.name == value } ?: OTHER
        }

        fun expenseCategories(): List<Category> = entries.filter { !it.isIncome }
        fun incomeCategories(): List<Category> = entries.filter { it.isIncome }
    }
}
