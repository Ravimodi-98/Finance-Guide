package com.example.financeguide.data.repository

import com.example.financeguide.data.local.dao.CollaborativeDao
import com.example.financeguide.data.local.dao.PendingSyncDao
import com.example.financeguide.data.local.entity.*
import com.example.financeguide.data.remote.CollaborativeApiService
import com.example.financeguide.data.remote.model.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class CollaborativeRepository(
    private val apiService: CollaborativeApiService,
    private val collaborativeDao: CollaborativeDao,
    private val pendingSyncDao: PendingSyncDao,
    private val gson: Gson = Gson()
) {

    // --- Groups ---
    fun getGroupsFlow(): Flow<List<GroupEntity>> = collaborativeDao.getAllGroupsFlow()

    suspend fun refreshGroups(): Result<Unit> {
        return try {
            val response = apiService.getGroups()
            if (response.isSuccessful && response.body() != null) {
                val groups = response.body()!!.map { g ->
                    GroupEntity(
                        id = g.id,
                        name = g.name,
                        icon = g.icon ?: "group",
                        description = g.description,
                        createdById = g.createdById,
                        memberCount = g.members?.size ?: 1,
                        syncStatus = "SYNCED"
                    )
                }
                collaborativeDao.insertGroups(groups)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch groups"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createGroup(name: String, description: String?, memberIds: List<String>): Result<GroupEntity> {
        val tempId = "grp_${System.currentTimeMillis()}"
        val localGroup = GroupEntity(
            id = tempId,
            name = name,
            description = description,
            createdById = "me",
            syncStatus = "PENDING_CREATE"
        )
        collaborativeDao.insertGroup(localGroup)

        return try {
            val response = apiService.createGroup(CreateGroupRequestDto(name, "group", description, memberIds))
            if (response.isSuccessful && response.body() != null) {
                val g = response.body()!!
                val syncedGroup = GroupEntity(
                    id = g.id,
                    name = g.name,
                    icon = g.icon ?: "group",
                    description = g.description,
                    createdById = g.createdById,
                    syncStatus = "SYNCED"
                )
                collaborativeDao.deleteGroup(tempId)
                collaborativeDao.insertGroup(syncedGroup)
                Result.success(syncedGroup)
            } else {
                // Queue offline sync
                pendingSyncDao.enqueue(
                    PendingSyncEntity(
                        entityType = "GROUP",
                        operation = "CREATE",
                        payloadJson = gson.toJson(CreateGroupRequestDto(name, "group", description, memberIds))
                    )
                )
                Result.success(localGroup)
            }
        } catch (e: Exception) {
            pendingSyncDao.enqueue(
                PendingSyncEntity(
                    entityType = "GROUP",
                    operation = "CREATE",
                    payloadJson = gson.toJson(CreateGroupRequestDto(name, "group", description, memberIds))
                )
            )
            Result.success(localGroup)
        }
    }

    // --- Collaborative Expenses ---
    fun getExpensesByGroupFlow(groupId: String): Flow<List<CollaborativeExpenseEntity>> =
        collaborativeDao.getExpensesByGroupFlow(groupId)

    suspend fun refreshExpenses(groupId: String): Result<Unit> {
        return try {
            val response = apiService.getExpensesByGroup(groupId)
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                val entities = list.map { e ->
                    CollaborativeExpenseEntity(
                        id = e.id,
                        groupId = e.groupId,
                        description = e.description,
                        totalAmount = e.totalAmount,
                        currency = e.currency,
                        category = e.category,
                        paidById = e.paidById,
                        paidByName = e.paidByName ?: e.paidById,
                        splitType = e.splitType,
                        notes = e.notes,
                        createdAt = e.createdAt ?: "",
                        syncStatus = "SYNCED"
                    )
                }
                collaborativeDao.insertExpenses(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch expenses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createExpense(
        groupId: String?,
        description: String,
        totalAmount: Double,
        paidById: String,
        paidByName: String,
        splitType: String,
        splits: List<ExpenseSplitDto>
    ): Result<CollaborativeExpenseEntity> {
        val tempId = "exp_${System.currentTimeMillis()}"
        val localExpense = CollaborativeExpenseEntity(
            id = tempId,
            groupId = groupId,
            description = description,
            totalAmount = totalAmount,
            paidById = paidById,
            paidByName = paidByName,
            splitType = splitType,
            syncStatus = "PENDING_CREATE"
        )
        collaborativeDao.insertExpense(localExpense)

        val req = CreateExpenseRequestDto(
            groupId = groupId,
            description = description,
            totalAmount = totalAmount,
            paidById = paidById,
            splitType = splitType,
            splits = splits
        )

        return try {
            val response = apiService.createExpense(req)
            if (response.isSuccessful && response.body() != null) {
                val e = response.body()!!
                val syncedExpense = CollaborativeExpenseEntity(
                    id = e.id,
                    groupId = e.groupId,
                    description = e.description,
                    totalAmount = e.totalAmount,
                    paidById = e.paidById,
                    paidByName = paidByName,
                    splitType = e.splitType,
                    syncStatus = "SYNCED"
                )
                collaborativeDao.deleteExpense(tempId)
                collaborativeDao.insertExpense(syncedExpense)
                Result.success(syncedExpense)
            } else {
                pendingSyncDao.enqueue(PendingSyncEntity(entityType = "EXPENSE", operation = "CREATE", payloadJson = gson.toJson(req)))
                Result.success(localExpense)
            }
        } catch (e: Exception) {
            pendingSyncDao.enqueue(PendingSyncEntity(entityType = "EXPENSE", operation = "CREATE", payloadJson = gson.toJson(req)))
            Result.success(localExpense)
        }
    }

    // --- Balances & Settlements ---
    suspend fun getBalancesForGroup(groupId: String): Result<GroupBalancesResponseDto> {
        return try {
            val response = apiService.getBalancesByGroup(groupId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch balances"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSettlement(groupId: String?, payeeId: String, payeeName: String, amount: Double): Result<SettlementEntity> {
        val req = CreateSettlementRequestDto(groupId = groupId, payeeId = payeeId, amount = amount)
        val tempId = "stl_${System.currentTimeMillis()}"
        val localSettlement = SettlementEntity(
            id = tempId,
            groupId = groupId,
            payerId = "me",
            payerName = "You",
            payeeId = payeeId,
            payeeName = payeeName,
            amount = amount,
            syncStatus = "PENDING_CREATE"
        )
        collaborativeDao.insertSettlement(localSettlement)

        return try {
            val response = apiService.createSettlement(req)
            if (response.isSuccessful && response.body() != null) {
                val s = response.body()!!
                val synced = SettlementEntity(
                    id = s.id,
                    groupId = s.groupId,
                    payerId = s.payerId,
                    payerName = s.payerName ?: "You",
                    payeeId = s.payeeId,
                    payeeName = s.payeeName ?: payeeName,
                    amount = s.amount,
                    syncStatus = "SYNCED"
                )
                collaborativeDao.insertSettlement(synced)
                Result.success(synced)
            } else {
                pendingSyncDao.enqueue(PendingSyncEntity(entityType = "SETTLEMENT", operation = "CREATE", payloadJson = gson.toJson(req)))
                Result.success(localSettlement)
            }
        } catch (e: Exception) {
            pendingSyncDao.enqueue(PendingSyncEntity(entityType = "SETTLEMENT", operation = "CREATE", payloadJson = gson.toJson(req)))
            Result.success(localSettlement)
        }
    }
}
