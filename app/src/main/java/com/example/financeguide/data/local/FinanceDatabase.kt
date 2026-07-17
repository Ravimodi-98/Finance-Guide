package com.example.financeguide.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.financeguide.data.local.dao.BudgetDao
import com.example.financeguide.data.local.dao.TransactionDao
import com.example.financeguide.data.local.entity.BudgetEntity
import com.example.financeguide.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, BudgetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null

        fun getInstance(context: Context): FinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinanceDatabase::class.java,
                    "finance_guide_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
