package com.example.financeguide.domain.usecase

import com.example.financeguide.data.repository.FinanceRepository
import com.example.financeguide.domain.model.BudgetStatus
import com.example.financeguide.domain.model.Category
import java.util.Calendar

class GetBudgetStatusUseCase(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(monthYear: String? = null): List<BudgetStatus> {
        val calendar = Calendar.getInstance()
        val targetMonthYear = monthYear ?: String.format(
            "%04d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1
        )

        // Parse month/year for date range
        val parts = targetMonthYear.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt() - 1 // Calendar months are 0-indexed

        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endDate = calendar.timeInMillis

        val budgetStatuses = mutableListOf<BudgetStatus>()

        Category.entries.forEach { category ->
            val budget = repository.getBudgetByCategory(category.name, targetMonthYear)
            if (budget != null) {
                val spent = repository.getTotalExpenseByCategory(category.name, startDate, endDate)
                budgetStatuses.add(
                    BudgetStatus(
                        category = category,
                        limitAmount = budget.limitAmount,
                        spentAmount = spent,
                        budgetId = budget.id
                    )
                )
            }
        }

        return budgetStatuses.sortedByDescending { it.percentage }
    }
}
