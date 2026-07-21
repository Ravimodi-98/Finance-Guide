package com.example.financeguide.domain.usecase

import com.example.financeguide.data.remote.model.ChatMessageDto
import com.example.financeguide.data.remote.model.ChatResponseDto
import com.example.financeguide.domain.repository.FinanceAIRepository

class AskFinanceQuestionUseCase(
    private val repository: FinanceAIRepository
) {
    suspend operator fun invoke(
        message: String,
        history: List<ChatMessageDto>,
        theme: String = "basic"
    ): Result<ChatResponseDto> {
        return repository.askQuestion(message, history, theme)
    }
}
