package com.example.financeguide.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financeguide.data.local.SettingsManager
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class FinanceViewModel(
    private val repository: FinanceRepository,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getBudgetStatusUseCase: GetBudgetStatusUseCase,
    private val generateSuggestionsUseCase: GenerateSuggestionsUseCase,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _summary = MutableStateFlow(FinanceSummary(totalIncome = 0.0, totalExpense = 0.0, categoryBreakdown = emptyMap(), incomeCategoryBreakdown = emptyMap()))
    val summary: StateFlow<FinanceSummary> = _summary.asStateFlow()

    private val _budgetStatuses = MutableStateFlow<List<BudgetStatus>>(emptyList())
    val budgetStatuses: StateFlow<List<BudgetStatus>> = _budgetStatuses.asStateFlow()

    private val _suggestions = MutableStateFlow<List<Suggestion>>(emptyList())
    val suggestions: StateFlow<List<Suggestion>> = _suggestions.asStateFlow()

    val recentTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val budgets: StateFlow<List<BudgetEntity>> = repository.getAllBudgets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadDashboardData()
    }

    fun syncFromRemote() {
        loadDashboardData()
    }

    fun loadDashboardData() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        viewModelScope.launch {
            _summary.value = getMonthlySummaryUseCase(year = currentYear, month = currentMonth)
            _budgetStatuses.value = getBudgetStatusUseCase()
            _suggestions.value = generateSuggestionsUseCase(_summary.value, _budgetStatuses.value)
        }
    }

    fun addTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
            loadDashboardData()
        }
    }

    fun addBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.insertBudget(budget)
            loadDashboardData()
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
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
            loadDashboardData()
        }
    }
}

class FinanceViewModelFactory(
    private val repository: FinanceRepository,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getBudgetStatusUseCase: GetBudgetStatusUseCase,
    private val generateSuggestionsUseCase: GenerateSuggestionsUseCase,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            return FinanceViewModel(
                repository = repository,
                getMonthlySummaryUseCase = getMonthlySummaryUseCase,
                getBudgetStatusUseCase = getBudgetStatusUseCase,
                generateSuggestionsUseCase = generateSuggestionsUseCase,
                settingsManager = settingsManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
