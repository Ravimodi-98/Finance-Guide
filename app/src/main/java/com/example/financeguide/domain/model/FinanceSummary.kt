package com.example.financeguide.domain.model

data class FinanceSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val categoryBreakdown: Map<Category, Double> = emptyMap(),
    val incomeCategoryBreakdown: Map<Category, Double> = emptyMap()
)
