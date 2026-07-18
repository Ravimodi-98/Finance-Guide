package com.example.financeguide.domain.repository

import com.example.financeguide.data.remote.model.ChatMessageDto
import com.example.financeguide.data.remote.model.ChatResponseDto

interface FinanceAIRepository {
    suspend fun askQuestion(
        message: String,
        history: List<ChatMessageDto>
    ): Result<ChatResponseDto>

    suspend fun resetChat(): Result<Unit>
}
