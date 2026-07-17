package com.example.financeguide.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "type")
    val type: String, // "INCOME" or "EXPENSE"

    @ColumnInfo(name = "date")
    val date: Long, // epoch millis

    @ColumnInfo(name = "note")
    val note: String? = null,

    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean = false
)
