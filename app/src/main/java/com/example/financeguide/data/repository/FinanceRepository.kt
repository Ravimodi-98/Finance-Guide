package com.example.financeguide.data.repository

import com.example.financeguide.data.local.dao.BudgetDao
import com.example.financeguide.data.local.dao.TransactionDao
import com.example.financeguide.data.local.entity.BudgetEntity
import com.example.financeguide.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao
) {
    // --- Transactions ---

    fun getAllTransactions(): Flow<List<TransactionEntity>> =
        transactionDao.getAllTransactions()

    fun getRecentTransactions(limit: Int = 10): Flow<List<TransactionEntity>> =
        transactionDao.getRecentTransactions(limit)

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)

    fun getTransactionsByCategory(category: String, startDate: Long, endDate: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByCategory(category, startDate, endDate)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        transactionDao.insert(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        transactionDao.update(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        transactionDao.delete(transaction)

    suspend fun deleteTransactionById(id: Long) =
        transactionDao.deleteById(id)

    suspend fun getTotalExpense(startDate: Long, endDate: Long): Double =
        transactionDao.getTotalExpense(startDate, endDate)

    suspend fun getTotalIncome(startDate: Long, endDate: Long): Double =
        transactionDao.getTotalIncome(startDate, endDate)

    suspend fun getTotalExpenseByCategory(category: String, startDate: Long, endDate: Long): Double =
        transactionDao.getTotalExpenseByCategory(category, startDate, endDate)

    suspend fun getTotalIncomeByCategory(category: String, startDate: Long, endDate: Long): Double =
        transactionDao.getTotalIncomeByCategory(category, startDate, endDate)

    // --- Budgets ---

    fun getBudgetsForMonth(monthYear: String): Flow<List<BudgetEntity>> =
        budgetDao.getBudgetsForMonth(monthYear)

    fun getAllBudgets(): Flow<List<BudgetEntity>> =
        budgetDao.getAllBudgets()

    suspend fun getBudgetByCategory(category: String, monthYear: String): BudgetEntity? =
        budgetDao.getBudgetByCategory(category, monthYear)

    suspend fun insertBudget(budget: BudgetEntity): Long =
        budgetDao.insert(budget)

    suspend fun updateBudget(budget: BudgetEntity) =
        budgetDao.update(budget)

    suspend fun deleteBudget(budget: BudgetEntity) =
        budgetDao.delete(budget)

    suspend fun deleteBudgetById(id: Long) =
        budgetDao.deleteById(id)

    suspend fun clearAllData() {
        transactionDao.clearAllTransactions()
        budgetDao.clearAllBudgets()
    }
}
