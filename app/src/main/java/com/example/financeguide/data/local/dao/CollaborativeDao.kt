package com.example.financeguide.data.local.dao

import androidx.room.*
import com.example.financeguide.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CollaborativeDao {

    // --- Groups ---
    @Query("SELECT * FROM collaborative_groups ORDER BY name ASC")
    fun getAllGroupsFlow(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM collaborative_groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: String): GroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<GroupEntity>)

    @Query("DELETE FROM collaborative_groups WHERE id = :groupId")
    suspend fun deleteGroup(groupId: String)

    // --- Members ---
    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    fun getGroupMembersFlow(groupId: String): Flow<List<GroupMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMembers(members: List<GroupMemberEntity>)

    // --- Collaborative Expenses ---
    @Query("SELECT * FROM collaborative_expenses WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getExpensesByGroupFlow(groupId: String): Flow<List<CollaborativeExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: CollaborativeExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<CollaborativeExpenseEntity>)

    @Query("DELETE FROM collaborative_expenses WHERE id = :expenseId")
    suspend fun deleteExpense(expenseId: String)

    // --- Splits ---
    @Query("SELECT * FROM expense_splits WHERE expenseId = :expenseId")
    suspend fun getSplitsForExpense(expenseId: String): List<ExpenseSplitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplits(splits: List<ExpenseSplitEntity>)

    // --- Settlements ---
    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY settledAt DESC")
    fun getSettlementsByGroupFlow(groupId: String): Flow<List<SettlementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlements(settlements: List<SettlementEntity>)

    // --- Friends / Users ---
    @Query("SELECT * FROM collaborative_users WHERE isFriend = 1")
    fun getFriendsFlow(): Flow<List<CollaborativeUserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<CollaborativeUserEntity>)
}
