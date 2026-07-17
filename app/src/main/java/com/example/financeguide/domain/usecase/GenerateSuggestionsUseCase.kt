package com.example.financeguide.domain.usecase

import com.example.financeguide.domain.model.BudgetStatus
import com.example.financeguide.domain.model.Category
import com.example.financeguide.domain.model.FinanceSummary
import com.example.financeguide.domain.model.Suggestion
import com.example.financeguide.domain.model.SuggestionPriority
import com.example.financeguide.domain.model.SuggestionType
import com.example.financeguide.domain.model.StatusLevel
import java.util.Calendar
import java.util.Locale

class GenerateSuggestionsUseCase {

    operator fun invoke(
        summary: FinanceSummary,
        budgetStatuses: List<BudgetStatus>,
        currencyCode: String = "BELI"
    ): List<Suggestion> {
        val suggestions = mutableListOf<Suggestion>()
        val dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val maxDays = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
        val daysRemaining = maxDays - dayOfMonth

        // Check for over-budget categories
        budgetStatuses.filter { it.statusLevel == StatusLevel.OVER }.forEach { status ->
            suggestions.add(
                Suggestion(
                    title = "Over budget: ${status.category.displayName}",
                    description = "You've exceeded your ${formatAmount(status.limitAmount, currencyCode)} ${status.category.displayName} budget by ${formatAmount(status.spentAmount - status.limitAmount, currencyCode)}. Consider cutting back for the rest of the month.",
                    priority = SuggestionPriority.HIGH,
                    type = SuggestionType.OVERSPENDING
                )
            )
        }

        // Check for critical budgets (85%+)
        budgetStatuses.filter { it.statusLevel == StatusLevel.CRITICAL }.forEach { status ->
            suggestions.add(
                Suggestion(
                    title = "${status.category.displayName} budget almost full",
                    description = "You've used ${(status.percentage * 100).toInt()}% of your ${status.category.displayName} budget with $daysRemaining days left. You have ${formatAmount(status.remaining, currencyCode)} remaining.",
                    priority = SuggestionPriority.HIGH,
                    type = SuggestionType.BUDGET_WARNING
                )
            )
        }

        // Saving tip when income is significantly higher than expenses
        if (summary.totalIncome > 0 && summary.totalExpense > 0) {
            val savingsRate = (summary.totalIncome - summary.totalExpense) / summary.totalIncome
            if (savingsRate > 0.3) {
                suggestions.add(
                    Suggestion(
                        title = "Great savings potential! 💰",
                        description = "You're saving ${(savingsRate * 100).toInt()}% of your income this month. Consider investing ${formatAmount(summary.balance * 0.5, currencyCode)} for long-term growth.",
                        priority = SuggestionPriority.MEDIUM,
                        type = SuggestionType.SAVING_TIP
                    )
                )
            } else if (savingsRate < 0.1 && savingsRate >= 0) {
                suggestions.add(
                    Suggestion(
                        title = "Low savings this month",
                        description = "You're only saving ${(savingsRate * 100).toInt()}% of your income. Try the 50/30/20 rule: 50% needs, 30% wants, 20% savings.",
                        priority = SuggestionPriority.HIGH,
                        type = SuggestionType.SAVING_TIP
                    )
                )
            }
        }

        // Achievement for under-budget categories
        val underBudgetCount = budgetStatuses.count { it.statusLevel == StatusLevel.SAFE }
        if (underBudgetCount >= 3) {
            suggestions.add(
                Suggestion(
                    title = "Amazing discipline! 🎉",
                    description = "You're under budget in $underBudgetCount categories this month. Keep up the great work!",
                    priority = SuggestionPriority.LOW,
                    type = SuggestionType.ACHIEVEMENT
                )
            )
        }

        // Suggest setting budgets for categories without them
        val categoriesWithBudget = budgetStatuses.map { it.category }.toSet()
        val categoriesWithSpending = summary.categoryBreakdown.keys
        val unbudgetedSpending = categoriesWithSpending.filter { it !in categoriesWithBudget }
        if (unbudgetedSpending.isNotEmpty()) {
            val topUnbudgeted = unbudgetedSpending.maxByOrNull { summary.categoryBreakdown[it] ?: 0.0 }
            if (topUnbudgeted != null) {
                val amount = summary.categoryBreakdown[topUnbudgeted] ?: 0.0
                suggestions.add(
                    Suggestion(
                        title = "Set a ${topUnbudgeted.displayName} budget",
                        description = "You've spent ${formatAmount(amount, currencyCode)} on ${topUnbudgeted.displayName} this month without a budget. Setting one can help you stay on track.",
                        priority = SuggestionPriority.MEDIUM,
                        type = SuggestionType.SETUP_HINT
                    )
                )
            }
        }

        // No transactions yet
        if (summary.totalIncome == 0.0 && summary.totalExpense == 0.0) {
            suggestions.add(
                Suggestion(
                    title = "Welcome! Start tracking 🚀",
                    description = "Add your first income or expense to start your financial journey. Tap the + button below to get started!",
                    priority = SuggestionPriority.HIGH,
                    type = SuggestionType.SETUP_HINT
                )
            )
        }

        // No budgets set
        if (budgetStatuses.isEmpty() && summary.totalExpense > 0) {
            suggestions.add(
                Suggestion(
                    title = "Create your first budget",
                    description = "You're spending but haven't set any budgets yet. Go to Budgets tab to set monthly limits and stay in control.",
                    priority = SuggestionPriority.MEDIUM,
                    type = SuggestionType.SETUP_HINT
                )
            )
        }

        return suggestions.sortedBy { it.priority.ordinal }
    }

    private fun formatAmount(amount: Double, currencyCode: String): String {
        val symbol = when (currencyCode) {
            "BELI" -> "฿"
            "INR" -> "₹"
            "USD" -> "$"
            "GBP" -> "£"
            else -> ""
        }
        val numStr = if (amount >= 100000) {
            String.format(Locale.US, "%.1fL", amount / 100000)
        } else if (amount >= 1000) {
            String.format(Locale.US, "%.1fK", amount / 1000)
        } else {
            String.format(Locale.US, "%.0f", amount)
        }
        return "$symbol$numStr"
    }
}
