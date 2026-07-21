package com.example.financeguide.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.financeguide.data.local.FinanceDatabase
import com.example.financeguide.data.remote.CollaborativeApiService
import com.example.financeguide.data.remote.model.CreateExpenseRequestDto
import com.example.financeguide.data.remote.model.CreateGroupRequestDto
import com.example.financeguide.data.remote.model.CreateSettlementRequestDto
import com.google.gson.Gson

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val apiService: CollaborativeApiService,
    private val database: FinanceDatabase,
    private val gson: Gson = Gson()
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val pendingItems = database.pendingSyncDao().getAllPending()
        if (pendingItems.isEmpty()) return Result.success()

        var hasFailure = false

        for (item in pendingItems) {
            try {
                val success = when (item.entityType) {
                    "GROUP" -> {
                        val req = gson.fromJson(item.payloadJson, CreateGroupRequestDto::class.java)
                        val res = apiService.createGroup(req)
                        res.isSuccessful
                    }
                    "EXPENSE" -> {
                        val req = gson.fromJson(item.payloadJson, CreateExpenseRequestDto::class.java)
                        val res = apiService.createExpense(req)
                        res.isSuccessful
                    }
                    "SETTLEMENT" -> {
                        val req = gson.fromJson(item.payloadJson, CreateSettlementRequestDto::class.java)
                        val res = apiService.createSettlement(req)
                        res.isSuccessful
                    }
                    else -> true
                }

                if (success) {
                    database.pendingSyncDao().delete(item)
                } else {
                    hasFailure = true
                }
            } catch (e: Exception) {
                hasFailure = true
            }
        }

        return if (hasFailure) Result.retry() else Result.success()
    }
}
