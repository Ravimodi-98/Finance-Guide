package com.example.financeguide.di

import android.content.Context
import com.example.financeguide.data.local.FinanceDatabase
import com.example.financeguide.data.local.SettingsManager
import com.example.financeguide.data.repository.AuthRepository
import com.example.financeguide.data.repository.FinanceRepository
import com.example.financeguide.domain.usecase.GenerateSuggestionsUseCase
import com.example.financeguide.domain.usecase.GetBudgetStatusUseCase
import com.example.financeguide.domain.usecase.GetMonthlySummaryUseCase
import com.example.financeguide.ui.FinanceViewModelFactory

/**
 * Dependency Injection container providing local database repositories and use cases.
 */
class AppContainer(private val context: Context) {

    private val database: FinanceDatabase by lazy {
        FinanceDatabase.getInstance(context)
    }

    private val financeRepository: FinanceRepository by lazy {
        FinanceRepository(
            transactionDao = database.transactionDao(),
            budgetDao = database.budgetDao()
        )
    }

    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase by lazy {
        GetMonthlySummaryUseCase(financeRepository)
    }

    private val getBudgetStatusUseCase: GetBudgetStatusUseCase by lazy {
        GetBudgetStatusUseCase(financeRepository)
    }

    private val generateSuggestionsUseCase: GenerateSuggestionsUseCase by lazy {
        GenerateSuggestionsUseCase()
    }

    val settingsManager: SettingsManager by lazy {
        SettingsManager(context)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(settingsManager)
    }

    // --- Factories ---

    val financeViewModelFactory: FinanceViewModelFactory by lazy {
        FinanceViewModelFactory(
            repository = financeRepository,
            getMonthlySummaryUseCase = getMonthlySummaryUseCase,
            getBudgetStatusUseCase = getBudgetStatusUseCase,
            generateSuggestionsUseCase = generateSuggestionsUseCase,
            settingsManager = settingsManager
        )
    }
}
