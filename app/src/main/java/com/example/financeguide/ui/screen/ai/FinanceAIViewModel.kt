package com.example.financeguide.ui.screen.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financeguide.data.local.SettingsManager
import com.example.financeguide.data.local.entity.BudgetEntity
import com.example.financeguide.data.local.entity.TransactionEntity
import com.example.financeguide.data.remote.SupabaseClient
import com.example.financeguide.data.remote.SupabaseTransaction
import com.example.financeguide.data.remote.SupabaseBudget
import com.example.financeguide.data.remote.model.ChatMessageDto
import com.example.financeguide.data.repository.FinanceRepository
import com.example.financeguide.domain.model.TransactionType
import com.example.financeguide.domain.usecase.AskFinanceQuestionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val suggestedQuestions: List<String> = listOf(
        "I spent 500 Beli on food",
        "I earned 10000 Beli from a treasure hunt",
        "Set budget of 3000 Beli for entertainment",
        "Reduce unnecessary expenses",
        "How much should I save every month?"
    )
)

private fun suggestionsForTheme(theme: String): List<String> = if (theme == "spiderman") {
    listOf(
        "What are top growth stocks & IPOs right now?",
        "How do central bank interest rates affect inflation?",
        "What's the latest stock market & financial news?",
        "I spent 500 Credits on mission gear",
        "I earned 10000 Credits from a S.H.I.E.L.D. mission",
        "Set budget of 3000 Credits for tech upgrades",
        "How should I split savings between stocks & mutual funds?"
    )
} else {
    listOf(
        "What are top growth stocks & IPOs to invest in?",
        "Explain banking interest rates & fixed deposits",
        "What's the latest financial & market news?",
        "I spent 500 Beli on food",
        "I earned 10000 Beli from a treasure hunt",
        "Set budget of 3000 Beli for entertainment",
        "How should I build a strong investment portfolio?"
    )
}

class FinanceAIViewModel(
    private val askFinanceQuestionUseCase: AskFinanceQuestionUseCase,
    private val localRepository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var chatJob: Job? = null

    init {
        // Update suggested questions whenever theme changes
        viewModelScope.launch {
            settingsManager.theme.collectLatest { theme ->
                _uiState.update { it.copy(suggestedQuestions = suggestionsForTheme(theme)) }
            }
        }
    }

    fun sendMessage(messageText: String) {
        if (messageText.isBlank()) return

        // 1. Add user message to UI state immediately
        val userMessage = ChatMessage(text = messageText, isFromUser = true)
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                isLoading = true,
                error = null
            )
        }

        // Cancel previous request if any is running to optimize performance
        chatJob?.cancel()

        chatJob = viewModelScope.launch {
            // Convert current UI messages into API history format
            // Excluding the message we just added (the last one) as it is the "current prompt"
            val historyDto = _uiState.value.messages.dropLast(1).map {
                ChatMessageDto(
                    role = if (it.isFromUser) "user" else "model",
                    text = it.text
                )
            }

            val result = askFinanceQuestionUseCase(messageText, historyDto, settingsManager.theme.value)
            result.onSuccess { responseDto ->
                // Process database commands returned by AI in the background
                responseDto.commands?.forEach { cmd ->
                    try {
                        executeCommand(cmd)
                    } catch (e: Exception) {
                        // Log database insertion error but do not crash the chat screen
                    }
                }

                _uiState.update { state ->
                    state.copy(
                        messages = state.messages + ChatMessage(text = responseDto.response, isFromUser = false),
                        isLoading = false
                    )
                }
            }
            result.onFailure { exception ->
                _uiState.update { state ->
                    state.copy(
                        error = exception.message ?: "An unexpected error occurred.",
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun executeCommand(cmd: com.example.financeguide.data.remote.model.CommandDto) {
        val token = settingsManager.accessToken.value

        when (cmd.action) {
            "add_transaction" -> {
                val transaction = TransactionEntity(
                    amount = cmd.amount ?: 0.0,
                    title = cmd.title ?: "Navigator Log",
                    category = cmd.category ?: "OTHER",
                    type = if (cmd.type == "INCOME") "INCOME" else "EXPENSE",
                    date = System.currentTimeMillis()
                )
                // 1. Insert into local SQLite database via Repository
                localRepository.insertTransaction(transaction)

                // 2. Sync to Supabase in the background if user is logged in
                if (token.isNotEmpty()) {
                    try {
                        SupabaseClient.api.insertTransaction(
                            apiKey = SupabaseClient.API_KEY,
                            token = "Bearer $token",
                            transaction = SupabaseTransaction(
                                amount = transaction.amount,
                                title = transaction.title,
                                category = transaction.category,
                                type = transaction.type,
                                date = transaction.date,
                                note = transaction.note,
                                isRecurring = transaction.isRecurring
                            )
                        )
                    } catch (e: Exception) {
                        // Ignore offline sync errors
                    }
                }
            }
            "add_budget" -> {
                val budget = BudgetEntity(
                    category = cmd.category ?: "OTHER",
                    monthYear = cmd.monthYear ?: getCurrentMonthYear(),
                    limitAmount = cmd.limitAmount ?: 0.0
                )
                // 1. Insert into local SQLite database via Repository
                localRepository.insertBudget(budget)

                // 2. Sync to Supabase in the background if user is logged in
                if (token.isNotEmpty()) {
                    try {
                        SupabaseClient.api.insertBudget(
                            apiKey = SupabaseClient.API_KEY,
                            token = "Bearer $token",
                            budget = SupabaseBudget(
                                category = budget.category,
                                monthYear = budget.monthYear,
                                limitAmount = budget.limitAmount
                            )
                        )
                    } catch (e: Exception) {
                        // Ignore offline sync errors
                    }
                }
            }
        }
    }

    private fun getCurrentMonthYear(): String {
        val sdf = SimpleDateFormat("MM/yyyy", Locale.US)
        return sdf.format(Date())
    }

    fun clearChat() {
        chatJob?.cancel()
        _uiState.update { state ->
            state.copy(
                messages = emptyList(),
                isLoading = false,
                error = null
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}

class FinanceAIViewModelFactory(
    private val askFinanceQuestionUseCase: AskFinanceQuestionUseCase,
    private val localRepository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceAIViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceAIViewModel(askFinanceQuestionUseCase, localRepository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
