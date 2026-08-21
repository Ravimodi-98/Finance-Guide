package com.example.financeguide.ui.screen.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financeguide.data.local.SettingsManager
import com.example.financeguide.data.repository.ChatbotRepository
import com.example.financeguide.domain.model.ChatMessage
import com.example.financeguide.domain.model.MessageStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatbotViewModel(
    private val repository: ChatbotRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    val themeMode: StateFlow<String> = settingsManager.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "onepiece")

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty() || _isLoading.value) return

        val userMessage = ChatMessage(
            text = trimmed,
            isUser = true,
            status = MessageStatus.SENT
        )

        val updatedList = _messages.value + userMessage
        _messages.value = updatedList
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = repository.getChatbotResponse(
                conversationHistory = updatedList,
                themeMode = themeMode.value
            )

            result.onSuccess { responseText ->
                val botMessage = ChatMessage(
                    text = responseText,
                    isUser = false,
                    status = MessageStatus.SENT
                )
                _messages.value = _messages.value + botMessage
                _isLoading.value = false
            }.onFailure { error ->
                val fallbackMessage = ChatMessage(
                    text = "Sorry, I encountered an issue: ${error.localizedMessage ?: "Please try again in a moment."}",
                    isUser = false,
                    status = MessageStatus.ERROR
                )
                _messages.value = _messages.value + fallbackMessage
                _isLoading.value = false
                _errorMessage.value = error.localizedMessage
            }
        }
    }

    fun clearConversation() {
        _messages.value = emptyList()
        _errorMessage.value = null
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}

class ChatbotViewModelFactory(
    private val repository: ChatbotRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatbotViewModel::class.java)) {
            return ChatbotViewModel(repository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
