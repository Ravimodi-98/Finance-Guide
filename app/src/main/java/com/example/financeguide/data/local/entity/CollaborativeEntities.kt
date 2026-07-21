package com.example.financeguide.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collaborative_users")
data class CollaborativeUserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val themePreference: String = "PRO",
    val isFriend: Boolean = false,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "collaborative_groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val icon: String? = "group",
    val description: String? = null,
    val createdById: String,
    val memberCount: Int = 1,
    val totalBalance: Double = 0.0,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "group_members")
data class GroupMemberEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val userId: String,
    val userName: String,
    val role: String = "MEMBER"
)

@Entity(tableName = "collaborative_expenses")
data class CollaborativeExpenseEntity(
    @PrimaryKey val id: String,
    val groupId: String? = null,
    val description: String,
    val totalAmount: Double,
    val currency: String = "USD",
    val category: String = "General",
    val paidById: String,
    val paidByName: String = "",
    val splitType: String = "EQUAL",
    val notes: String? = null,
    val createdAt: String = "",
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "expense_splits")
data class ExpenseSplitEntity(
    @PrimaryKey val id: String,
    val expenseId: String,
    val userId: String,
    val owedAmount: Double,
    val percentage: Double? = null,
    val shareCount: Double? = null
)

@Entity(tableName = "settlements")
data class SettlementEntity(
    @PrimaryKey val id: String,
    val groupId: String? = null,
    val payerId: String,
    val payerName: String,
    val payeeId: String,
    val payeeName: String,
    val amount: Double,
    val currency: String = "USD",
    val note: String? = null,
    val settledAt: String = "",
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "pending_sync_queue")
data class PendingSyncEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val entityType: String, // EXPENSE, GROUP, SETTLEMENT
    val operation: String,  // CREATE, UPDATE, DELETE
    val payloadJson: String,
    val timestamp: Long = System.currentTimeMillis()
)
