package com.example.financeguide.domain.usecase

import com.example.financeguide.data.repository.FinanceRepository
import com.example.financeguide.domain.model.Category
import com.example.financeguide.domain.model.FinanceSummary
import java.util.Calendar

class GetMonthlySummaryUseCase(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(year: Int? = null, month: Int? = null): FinanceSummary {
        val calendar = Calendar.getInstance()
        val targetYear = year ?: calendar.get(Calendar.YEAR)
        val targetMonth = month ?: calendar.get(Calendar.MONTH)

        // Start of month
        calendar.set(targetYear, targetMonth, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        // End of month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endDate = calendar.timeInMillis

        val totalIncome = repository.getTotalIncome(startDate, endDate)
        val totalExpense = repository.getTotalExpense(startDate, endDate)

        val categoryBreakdown = mutableMapOf<Category, Double>()
        val incomeCategoryBreakdown = mutableMapOf<Category, Double>()
        Category.entries.forEach { category ->
            val expAmount = repository.getTotalExpenseByCategory(category.name, startDate, endDate)
            if (expAmount > 0) {
                categoryBreakdown[category] = expAmount
            }
            val incAmount = repository.getTotalIncomeByCategory(category.name, startDate, endDate)
            if (incAmount > 0) {
                incomeCategoryBreakdown[category] = incAmount
            }
        }

        return FinanceSummary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = totalIncome - totalExpense,
            categoryBreakdown = categoryBreakdown,
            incomeCategoryBreakdown = incomeCategoryBreakdown
        )
    }
}
