package com.example.financeguide.domain.model

data class BudgetStatus(
    val category: Category,
    val limitAmount: Double,
    val spentAmount: Double,
    val budgetId: Long
) {
    val percentage: Float
        get() = if (limitAmount > 0) (spentAmount / limitAmount).toFloat().coerceIn(0f, 1.5f) else 0f

    val remaining: Double
        get() = (limitAmount - spentAmount).coerceAtLeast(0.0)

    val isOverBudget: Boolean
        get() = spentAmount > limitAmount

    val statusLevel: StatusLevel
        get() = when {
            percentage >= 1.0f -> StatusLevel.OVER
            percentage >= 0.85f -> StatusLevel.CRITICAL
            percentage >= 0.60f -> StatusLevel.WARNING
            else -> StatusLevel.SAFE
        }
}

enum class StatusLevel {
    SAFE,
    WARNING,
    CRITICAL,
    OVER
}
