package com.example.financeguide.domain.usecase

import com.example.financeguide.data.local.dao.TransactionDao
import com.example.financeguide.data.local.entity.TransactionEntity

class PersonalFinanceSyncUseCase(
    private val transactionDao: TransactionDao
) {
    suspend fun syncSharedExpenseToPersonal(
        title: String,
        userShareAmount: Double,
        category: String,
        date: Long,
        notes: String? = null
    ) {
        if (userShareAmount <= 0) return

        val syncNote = if (notes.isNullOrBlank()) {
            "[Shared Expense Split Share]"
        } else {
            "$notes [Shared Expense Split Share]"
        }

        val personalTx = TransactionEntity(
            amount = userShareAmount,
            title = title,
            category = category,
            type = "EXPENSE",
            date = date,
            note = syncNote,
            isRecurring = false
        )

        transactionDao.insert(personalTx)
    }
}
