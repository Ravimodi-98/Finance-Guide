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
        val isSpidey = theme == "spiderman"
        val isBasic = theme == "basic"
        return try {

            val response = apiService.chat(ChatRequestDto(message, history, theme))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    val emptyMsg = when {
                        isSpidey -> "Received an empty case file from the server."
                        isBasic  -> "Received an empty response from the AI server."
                        else     -> "Received an empty logbook from the server."
                    }
                    Result.failure(Exception(emptyMsg))
                }
            } else {
                val errorMsg = when (response.code()) {
                    429 -> when {
                        isSpidey -> "Too many requests! Spider-Sense overloaded, please wait a minute."
                        isBasic  -> "Too many requests! FinBot is processing queries, please wait a minute."
                        else     -> "Too many requests! Nami is drawing sea charts, please wait a minute."
                    }
                    500 -> when {
                        isSpidey -> "The hero network server encountered a problem (500). Please try again!"
                        isBasic  -> "The AI financial server encountered a problem (500). Please try again!"
                        else     -> "The backend ship engine encountered a problem (500). Please try again!"
                    }
                    401, 403 -> "Authorization check failed (Unauthorized/Forbidden)."
                    else -> "An error occurred (${response.code()}): ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: SocketTimeoutException) {
            val timeoutMsg = when {
                isSpidey -> "Connection timed out. Web signal interrupted!"
                isBasic  -> "Connection timed out. Unable to reach AI server."
                else     -> "Connection timed out. The seas are too rough to reach the server!"
            }
            Result.failure(Exception(timeoutMsg))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection. Please check your network and try again!"))
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP Error: ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "An unexpected error occurred."))
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
