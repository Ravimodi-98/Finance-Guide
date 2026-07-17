package com.example.financeguide.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Suggestion(
    val title: String,
    val description: String,
    val priority: SuggestionPriority,
    val type: SuggestionType
) {
    val icon: ImageVector
        get() = when (type) {
            SuggestionType.BUDGET_WARNING -> Icons.Rounded.Warning
            SuggestionType.SAVING_TIP -> Icons.Rounded.Savings
            SuggestionType.OVERSPENDING -> Icons.Rounded.TrendingDown
            SuggestionType.ACHIEVEMENT -> Icons.Rounded.Celebration
            SuggestionType.SETUP_HINT -> Icons.Rounded.AccountBalance
            SuggestionType.GENERAL -> Icons.Rounded.Info
        }

    val color: Color
        get() = when (type) {
            SuggestionType.BUDGET_WARNING -> Color(0xFFF59E0B)
            SuggestionType.SAVING_TIP -> Color(0xFF10B981)
            SuggestionType.OVERSPENDING -> Color(0xFFEF4444)
            SuggestionType.ACHIEVEMENT -> Color(0xFF8B5CF6)
            SuggestionType.SETUP_HINT -> Color(0xFF3B82F6)
            SuggestionType.GENERAL -> Color(0xFF6B7280)
        }
}

enum class SuggestionPriority {
    HIGH, MEDIUM, LOW
}

enum class SuggestionType {
    BUDGET_WARNING,
    SAVING_TIP,
    OVERSPENDING,
    ACHIEVEMENT,
    SETUP_HINT,
    GENERAL
}
