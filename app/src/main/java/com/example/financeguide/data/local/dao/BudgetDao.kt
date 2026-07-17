package com.example.financeguide.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financeguide.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE month_year = :monthYear")
    fun getBudgetsForMonth(monthYear: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE category = :category AND month_year = :monthYear LIMIT 1")
    suspend fun getBudgetByCategory(category: String, monthYear: String): BudgetEntity?

    @Query("SELECT * FROM budgets ORDER BY month_year DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("DELETE FROM budgets")
    suspend fun clearAllBudgets()

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
