package com.example.financeguide.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "month_year")
    val monthYear: String, // format: "2026-07"

    @ColumnInfo(name = "limit_amount")
    val limitAmount: Double
)
