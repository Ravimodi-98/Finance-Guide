package com.example.financeguide.data.repository

import com.example.financeguide.data.remote.FinanceApiService
import com.example.financeguide.data.remote.model.FinnhubNewsArticleDto
import com.example.financeguide.data.remote.model.ExchangeRateResponseData

sealed class MarketsResult<out T> {
    data class Success<out T>(val data: T) : MarketsResult<T>()
    data class Error(val message: String) : MarketsResult<Nothing>()
}

class MarketsRepository(
    private val apiService: FinanceApiService
) {
    suspend fun getNews(forceRefresh: Boolean = false): MarketsResult<List<FinnhubNewsArticleDto>> {
        return try {
            val response = apiService.getNews(force = forceRefresh)
            if (response.isSuccessful && response.body()?.success == true) {
                MarketsResult.Success(response.body()?.data ?: emptyList())
            } else {
                MarketsResult.Error("Failed to fetch financial news.")
            }
        } catch (e: Exception) {
            MarketsResult.Error(e.localizedMessage ?: "Network connection error")
        }
    }

    suspend fun getExchangeRates(forceRefresh: Boolean = false): MarketsResult<ExchangeRateResponseData> {
        return try {
            val response = apiService.getExchangeRates(force = forceRefresh)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                MarketsResult.Success(response.body()!!.data!!)
            } else {
                MarketsResult.Error("Failed to fetch exchange rates.")
            }
        } catch (e: Exception) {
            MarketsResult.Error(e.localizedMessage ?: "Network connection error")
        }
    }
}
