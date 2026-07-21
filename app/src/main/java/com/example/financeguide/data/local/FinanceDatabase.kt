package com.example.financeguide.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.financeguide.data.local.dao.BudgetDao
import com.example.financeguide.data.local.dao.CollaborativeDao
import com.example.financeguide.data.local.dao.PendingSyncDao
import com.example.financeguide.data.local.dao.TransactionDao
import com.example.financeguide.data.local.entity.*

@Database(
    entities = [
        TransactionEntity::class,
        BudgetEntity::class,
        CollaborativeUserEntity::class,
        GroupEntity::class,
        GroupMemberEntity::class,
        CollaborativeExpenseEntity::class,
        ExpenseSplitEntity::class,
        SettlementEntity::class,
        PendingSyncEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun collaborativeDao(): CollaborativeDao
    abstract fun pendingSyncDao(): PendingSyncDao

    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null

        fun getInstance(context: Context): FinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinanceDatabase::class.java,
                    "finance_guide_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
