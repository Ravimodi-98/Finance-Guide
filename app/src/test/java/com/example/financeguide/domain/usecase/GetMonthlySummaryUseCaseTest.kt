package com.example.financeguide.domain.usecase

import com.example.financeguide.data.local.dao.BudgetDao
import com.example.financeguide.data.local.dao.TransactionDao
import com.example.financeguide.data.local.entity.BudgetEntity
import com.example.financeguide.data.local.entity.TransactionEntity
import com.example.financeguide.data.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMonthlySummaryUseCaseTest {

    @Test
    fun testGetMonthlySummaryCalculation() = runTest {
        val fakeDao = FakeTransactionDao()
        val fakeBudgetDao = FakeBudgetDao()
        val repository = FinanceRepository(fakeDao, fakeBudgetDao)
        val useCase = GetMonthlySummaryUseCase(repository)

        val summary = useCase(2026, 6)
        assertEquals(5000.0, summary.totalIncome, 0.01)
        assertEquals(2000.0, summary.totalExpense, 0.01)
        assertEquals(3000.0, summary.balance, 0.01)
    }

    private class FakeTransactionDao : TransactionDao {
        override suspend fun insert(transaction: TransactionEntity): Long = 1L
        override suspend fun update(transaction: TransactionEntity) {}
        override suspend fun delete(transaction: TransactionEntity) {}
        override fun getAllTransactions(): Flow<List<TransactionEntity>> = flowOf(emptyList())
        override fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>> = flowOf(emptyList())
        override fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>> = flowOf(emptyList())
        override suspend fun getTotalIncome(startDate: Long, endDate: Long): Double = 5000.0
        override suspend fun getTotalExpense(startDate: Long, endDate: Long): Double = 2000.0
        override suspend fun getTotalExpenseByCategory(category: String, startDate: Long, endDate: Long): Double = 0.0
        override suspend fun getTotalIncomeByCategory(category: String, startDate: Long, endDate: Long): Double = 0.0
        override fun getTransactionsByCategory(category: String, startDate: Long, endDate: Long): Flow<List<TransactionEntity>> = flowOf(emptyList())
        override suspend fun deleteById(id: Long) {}
        override suspend fun clearAllTransactions() {}
    }

    private class FakeBudgetDao : BudgetDao {
        override suspend fun insert(budget: BudgetEntity): Long = 1L
        override suspend fun update(budget: BudgetEntity) {}
        override suspend fun delete(budget: BudgetEntity) {}
        override fun getBudgetsForMonth(monthYear: String): Flow<List<BudgetEntity>> = flowOf(emptyList())
        override suspend fun getBudgetByCategory(category: String, monthYear: String): BudgetEntity? = null
        override fun getAllBudgets(): Flow<List<BudgetEntity>> = flowOf(emptyList())
        override suspend fun clearAllBudgets() {}
        override suspend fun deleteById(id: Long) {}
    }
}
