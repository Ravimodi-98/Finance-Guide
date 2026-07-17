package com.example.financeguide.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financeguide.data.local.entity.BudgetEntity
import com.example.financeguide.data.local.entity.TransactionEntity
import com.example.financeguide.data.repository.FinanceRepository
import com.example.financeguide.domain.model.BudgetStatus
import com.example.financeguide.domain.model.FinanceSummary
import com.example.financeguide.domain.model.Suggestion
import com.example.financeguide.domain.usecase.GenerateSuggestionsUseCase
import com.example.financeguide.domain.usecase.GetBudgetStatusUseCase
import com.example.financeguide.domain.usecase.GetMonthlySummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.financeguide.data.remote.*

class FinanceViewModel(
    private val repository: FinanceRepository,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getBudgetStatusUseCase: GetBudgetStatusUseCase,
    private val generateSuggestionsUseCase: GenerateSuggestionsUseCase,
    private val settingsManager: com.example.financeguide.data.local.SettingsManager
) : ViewModel() {

    val recentTransactions: StateFlow<List<TransactionEntity>> = repository.getRecentTransactions(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _summary = MutableStateFlow(FinanceSummary())
    val summary: StateFlow<FinanceSummary> = _summary

    private val _budgetStatuses = MutableStateFlow<List<BudgetStatus>>(emptyList())
    val budgetStatuses: StateFlow<List<BudgetStatus>> = _budgetStatuses

    private val _suggestions = MutableStateFlow<List<Suggestion>>(emptyList())
    val suggestions: StateFlow<List<Suggestion>> = _suggestions

    init {
        viewModelScope.launch {
            settingsManager.currency.collect {
                loadDashboardData()
            }
        }
        viewModelScope.launch {
            settingsManager.isLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    syncFromRemote()
                } else {
                    repository.clearAllData()
                    loadDashboardData()
                }
            }
        }
    }

    fun syncFromRemote() {
        val token = settingsManager.accessToken.value
        if (token.isEmpty()) return
        
        viewModelScope.launch {
            try {
                repository.clearAllData()

                // Fetch remote transactions
                val txResponse = SupabaseClient.api.getTransactions(SupabaseClient.API_KEY, "Bearer $token")
                if (txResponse.isSuccessful && txResponse.body() != null) {
                    txResponse.body()!!.forEach { remote ->
                        repository.insertTransaction(
                            TransactionEntity(
                                amount = remote.amount,
                                title = remote.title,
                                category = remote.category,
                                type = remote.type,
                                date = remote.date,
                                note = remote.note,
                                isRecurring = remote.isRecurring
                            )
                        )
                    }
                }

                // Fetch remote budgets
                val budgetResponse = SupabaseClient.api.getBudgets(SupabaseClient.API_KEY, "Bearer $token")
                if (budgetResponse.isSuccessful && budgetResponse.body() != null) {
                    budgetResponse.body()!!.forEach { remote ->
                        repository.insertBudget(
                            BudgetEntity(
                                category = remote.category,
                                monthYear = remote.monthYear,
                                limitAmount = remote.limitAmount
                            )
                        )
                    }
                }
                
                loadDashboardData()
            } catch (e: Exception) {
                // Connection offline
            }
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            val currencyCode = settingsManager.currency.value
            val summaryData = getMonthlySummaryUseCase()
            val budgetsData = getBudgetStatusUseCase()
            _summary.value = summaryData
            _budgetStatuses.value = budgetsData
            _suggestions.value = generateSuggestionsUseCase(summaryData, budgetsData, currencyCode)
        }
    }

    fun addTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
            loadDashboardData() // Refresh dashboard data

            val token = settingsManager.accessToken.value
            if (token.isNotEmpty()) {
                try {
                    SupabaseClient.api.insertTransaction(
                        apiKey = SupabaseClient.API_KEY,
                        token = "Bearer $token",
                        transaction = SupabaseTransaction(
                            amount = transaction.amount,
                            title = transaction.title,
                            category = transaction.category,
                            type = transaction.type,
                            date = transaction.date,
                            note = transaction.note,
                            isRecurring = transaction.isRecurring
                        )
                    )
                } catch (e: Exception) {
                    // Ignore offline sync errors
                }
            }
        }
    }

    fun addBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.insertBudget(budget)
            loadDashboardData()

            val token = settingsManager.accessToken.value
            if (token.isNotEmpty()) {
                try {
                    SupabaseClient.api.insertBudget(
                        apiKey = SupabaseClient.API_KEY,
                        token = "Bearer $token",
                        budget = SupabaseBudget(
                            category = budget.category,
                            monthYear = budget.monthYear,
                            limitAmount = budget.limitAmount
                        )
                    )
                } catch (e: Exception) {
                    // Ignore offline sync errors
                }
            }
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            loadDashboardData()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            loadDashboardData()

            val token = settingsManager.accessToken.value
            if (token.isNotEmpty()) {
                try {
                    SupabaseClient.api.deleteTransaction(
                        apiKey = SupabaseClient.API_KEY,
                        token = "Bearer $token",
                        filter = "date=eq.${transaction.date}"
                    )
                } catch (e: Exception) {
                    // Ignore offline sync errors
                }
            }
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
            loadDashboardData()

            val token = settingsManager.accessToken.value
            if (token.isNotEmpty()) {
                try {
                    SupabaseClient.api.deleteBudget(
                        apiKey = SupabaseClient.API_KEY,
                        token = "Bearer $token",
                        filter = "and(category.eq.${budget.category},month_year.eq.${budget.monthYear})"
                    )
                } catch (e: Exception) {
                    // Ignore offline sync errors
                }
            }
        }
    }
}

class FinanceViewModelFactory(
    private val repository: FinanceRepository,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getBudgetStatusUseCase: GetBudgetStatusUseCase,
    private val generateSuggestionsUseCase: GenerateSuggestionsUseCase,
    private val settingsManager: com.example.financeguide.data.local.SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(
                repository,
                getMonthlySummaryUseCase,
                getBudgetStatusUseCase,
                generateSuggestionsUseCase,
                settingsManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
