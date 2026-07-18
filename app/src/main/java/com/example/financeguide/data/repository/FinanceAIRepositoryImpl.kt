package com.example.financeguide.data.repository

import com.example.financeguide.data.remote.FinanceApiService
import com.example.financeguide.data.remote.model.ChatMessageDto
import com.example.financeguide.data.remote.model.ChatRequestDto
import com.example.financeguide.data.remote.model.ChatResponseDto
import com.example.financeguide.domain.repository.FinanceAIRepository
import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

class FinanceAIRepositoryImpl(
    private val apiService: FinanceApiService
) : FinanceAIRepository {

    override suspend fun askQuestion(
        message: String,
        history: List<ChatMessageDto>,
        theme: String
    ): Result<ChatResponseDto> {
        return try {
            val response = apiService.chat(ChatRequestDto(message, history, theme))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Received an empty logbook from the server."))
                }
            } else {
                val errorMsg = when (response.code()) {
                    429 -> "Too many requests! Nami is drawing sea charts, please wait a minute before asking again."
                    500 -> "The backend ship engine encountered a problem (500). Please try again later!"
                    401, 403 -> "Secret clearance check failed (Unauthorized/Forbidden)."
                    else -> "The Navigator encountered an error: ${response.code()} ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Connection timed out. The seas are too rough to reach the server!"))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection. Please check your network and try again!"))
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP Error: ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "An unknown sea storm occurred."))
        }
    }

    override suspend fun resetChat(): Result<Unit> {
        return try {
            val response = apiService.resetChat()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to reset Navigator's logbook: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
